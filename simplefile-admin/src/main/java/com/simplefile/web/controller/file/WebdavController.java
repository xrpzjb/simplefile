package com.simplefile.web.controller.file;

import com.simplefile.common.core.controller.BaseController;
import com.simplefile.common.core.domain.entity.SysUser;
import com.simplefile.common.utils.SecurityUtils;
import com.simplefile.wedav.domain.SysWebdavFile;
import com.simplefile.wedav.service.ISysWebdavFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * WebDAV文件Controller
 * @author doubao
 */
@Slf4j
@RestController
@RequestMapping("/system/webdav")
public class WebdavController extends BaseController {

    @Resource
    private ISysWebdavFileService fileService;

    // 定义WebDAV方法常量
    private static final String METHOD_MKCOL = "MKCOL";
    private static final String METHOD_PROPFIND = "PROPFIND";
    private static final String METHOD_PROPPATCH = "PROPPATCH";
    private static final String METHOD_LOCK = "LOCK";
    private static final String METHOD_UNLOCK = "UNLOCK";
    private static final String METHOD_MOVE = "MOVE";
    private static final String METHOD_COPY = "COPY";


    /**
     * 获取当前认证用户
     */
    private SysUser getCurrentUser() {
        try {
            return SecurityUtils.getLoginUser().getUser();
        } catch (Exception e) {
            // 如果无法获取用户，返回null
            return null;
        }
    }

    /**
     * 检查用户是否已认证
     */
    private boolean isAuthenticated() {
        return getCurrentUser() != null;
    }

    /**
     * 检查用户是否有权限访问指定路径
     */
    private boolean hasPermission(String filePath) {
        SysUser user = getCurrentUser();

        if (user == null) {
            return false;
        }

        // 管理员可以访问所有文件
        if (SecurityUtils.isAdmin(user.getUserId())) {
            return true;
        }

        // 普通用户只能访问自己的目录
        // 假设用户目录为 /user/{userId}/
        String userDir = "/user/" + user.getUserId() + "/";
        return filePath.startsWith(userDir);
    }

    /**
     * 获取文件列表 (GET)
     */
    @GetMapping("/list")
    public void list(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 构建查询条件
        SysWebdavFile query = new SysWebdavFile();
        String path = request.getParameter("path");
        if (path == null || path.isEmpty()) {
            path = "/";
        }

        // 权限检查
        if (!hasPermission(path)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权限访问此路径");
            return;
        }

        // 管理员可以查看所有文件，普通用户只能查看自己的
        if (!SecurityUtils.isAdmin(getCurrentUser().getUserId())) {
            String userDir = "/user/" + getCurrentUser().getUserId() + "/";
            if (!path.startsWith(userDir)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权限访问此路径");
                return;
            }
        }

        query.setFilePath(path + "%");

        // 获取文件列表
        startPage();
        List<SysWebdavFile> list = fileService.selectFileList(query);

        // 生成XML响应
        response.setContentType("text/xml; charset=UTF-8");
        response.setStatus(HttpStatus.MULTI_STATUS.value());
        response.getWriter().write(generateFileListResponse(list, path));
    }

    /**
     * 下载文件 (GET)
     */
    @GetMapping(value = "/{filePath:.+}")
    public void downloadFile(@PathVariable String filePath, HttpServletResponse response) throws IOException {
        if (!isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!hasPermission(filePath)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权限访问此文件");
            return;
        }

        fileService.downloadFile(filePath, response);
    }

    /**
     * 上传文件 (PUT)
     */
    @PutMapping(value = "/{filePath:.+}")
    public void uploadFile(@PathVariable String filePath,
                           @RequestBody byte[] content,
                           HttpServletResponse response) throws IOException {
        if (!isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!hasPermission(filePath)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权限访问此路径");
            return;
        }

        try {
            // 检查父目录是否存在
            String parentPath = filePath.substring(0, filePath.lastIndexOf('/'));
            if (!fileService.checkPathExists(parentPath)) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "父目录不存在");
                return;
            }

            // 检查文件是否存在
            SysWebdavFile file = fileService.selectFileByPath(filePath);
            if (file != null) {
                // 文件存在，更新内容
                fileService.updateFileContent(file.getFileId(), content);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                // 文件不存在，创建新文件
                file = new SysWebdavFile();
                file.setFilePath(filePath);
                file.setFileName(filePath.substring(filePath.lastIndexOf('/') + 1));
                file.setParentId(getParentId(parentPath));
                file.setIsDirectory(false);
                file.setFileSize((long) content.length);
                file.setCreateTime(new Date());
                file.setUpdateTime(new Date());
                file.setCreateBy(getCurrentUser().getUserName());
                file.setUpdateBy(getCurrentUser().getUserName());

                fileService.createFile(file);
                fileService.updateFileContent(file.getFileId(), content);
                response.setStatus(HttpServletResponse.SC_CREATED);
            }
        } catch (Exception e) {
            log.error("上传文件失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除文件或目录 (DELETE)
     */
    @DeleteMapping(value = "/{filePath:.+}")
    public void deleteFile(@PathVariable String filePath, HttpServletResponse response) throws IOException {
        if (!isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!hasPermission(filePath)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权限访问此路径");
            return;
        }

        try {
            if (!fileService.checkPathExists(filePath)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            int result = fileService.deleteFile(filePath);
            if (result > 0) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("删除文件失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 创建目录 (MKCOL)
     */
    @RequestMapping(method = RequestMethod.POST, headers = "X-HTTP-Method-Override=MKCOL")
    public void createDirectory(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String path = request.getPathInfo();
        if (!hasPermission(path)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权限访问此路径");
            return;
        }

        try {
            if (path == null || path.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少路径信息");
                return;
            }

            // 检查路径是否存在
            if (fileService.checkPathExists(path)) {
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "路径已存在");
                return;
            }

            // 创建目录
            fileService.createDirectory(path, getParentId(path));
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            log.error("创建目录失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取文件属性 (PROPFIND)
     */
    @RequestMapping(method = RequestMethod.POST, headers = "X-HTTP-Method-Override=PROPFIND")
    public void handlePropfind(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String path = request.getPathInfo();
        if (path == null) path = "/";

        // 确保用户只能访问自己的目录
        if (!"/".equals(path) && !hasPermission(path)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权限访问此路径");
            return;
        }

        // 设置WebDAV特定响应头
        response.setHeader("DAV", "1,2");
        response.setHeader("MS-Author-Via", "DAV");
        response.setContentType("text/xml; charset=UTF-8");

        // 检查路径是否存在
        if (!fileService.checkPathExists(path)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 获取深度头信息
        String depth = request.getHeader("Depth");
        if (depth == null) depth = "1";

        // 获取文件/目录信息
        SysWebdavFile file = fileService.selectFileByPath(path);

        // 生成XML响应
        String xmlResponse = generatePropfindResponse(file, depth);

        // 返回Multi-Status响应
        response.setStatus(HttpStatus.MULTI_STATUS.value());
        response.getWriter().write(xmlResponse);
    }

    /**
     * 移动文件或目录 (MOVE)
     */
    @RequestMapping(method = RequestMethod.POST, headers = "X-HTTP-Method-Override=MOVE")
    public void moveFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String sourcePath = request.getPathInfo();
            String destination = request.getHeader("Destination");

            if (sourcePath == null || sourcePath.isEmpty() || destination == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少必要信息");
                return;
            }

            // 从Destination头中提取目标路径
            String targetPath = extractPathFromUrl(destination);

            // 检查权限
            if (!hasPermission(sourcePath) || !hasPermission(targetPath)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权限访问此路径");
                return;
            }

            // 检查源文件是否存在
            if (!fileService.checkPathExists(sourcePath)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 检查目标路径是否存在
            if (fileService.checkPathExists(targetPath)) {
                // 处理覆盖逻辑（这里简化处理，直接返回错误）
                response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "目标路径已存在");
                return;
            }

            // 执行移动操作
            int result = fileService.moveFile(sourcePath, targetPath);

            if (result > 0) {
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("移动文件失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 复制文件或目录 (COPY)
     */
    @RequestMapping(method = RequestMethod.POST, headers = "X-HTTP-Method-Override=COPY")
    public void copyFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isAuthenticated()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String sourcePath = request.getPathInfo();
            String destination = request.getHeader("Destination");

            if (sourcePath == null || sourcePath.isEmpty() || destination == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少必要信息");
                return;
            }

            // 从Destination头中提取目标路径
            String targetPath = extractPathFromUrl(destination);

            // 检查权限
            if (!hasPermission(sourcePath) || !hasPermission(targetPath)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权限访问此路径");
                return;
            }

            // 检查源文件是否存在
            if (!fileService.checkPathExists(sourcePath)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // 检查目标路径是否存在
            if (fileService.checkPathExists(targetPath)) {
                // 处理覆盖逻辑（这里简化处理，直接返回错误）
                response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "目标路径已存在");
                return;
            }

            // 获取深度头信息
            String depth = request.getHeader("Depth");
            if (depth == null) depth = "1";

            // 执行复制操作
            int result = fileService.copyFile(sourcePath, targetPath);

            if (result > 0) {
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("复制文件失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 生成PROPFIND请求的XML响应
     */
    private String generatePropfindResponse(SysWebdavFile file, String depth) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        xml.append("<D:multistatus xmlns:D=\"DAV:\">\n");

        // 添加当前资源的属性
        addResourceProperties(xml, file);

        // 如果是目录且深度允许，添加子资源
        if (file.getIsDirectory() && !"0".equals(depth)) {
            SysWebdavFile query = new SysWebdavFile();
            query.setFilePath(file.getFilePath() + "%");
            List<SysWebdavFile> children = fileService.selectFileList(query);

            for (SysWebdavFile child : children) {
                // 只包含直接子项（如果深度为1）
                if ("1".equals(depth) && child.getFilePath().split("/").length != file.getFilePath().split("/").length + 1) {
                    continue;
                }

                // 检查权限
                if (hasPermission(child.getFilePath())) {
                    addResourceProperties(xml, child);
                }
            }
        }

        xml.append("</D:multistatus>");
        return xml.toString();
    }

    /**
     * 生成文件列表响应
     */
    private String generateFileListResponse(List<SysWebdavFile> files, String basePath) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        xml.append("<D:multistatus xmlns:D=\"DAV:\">\n");

        // 添加当前目录
        SysWebdavFile directory = new SysWebdavFile();
        directory.setFilePath(basePath);
        directory.setFileName(basePath.isEmpty() ? "/" : basePath.substring(basePath.lastIndexOf('/') + 1));
        directory.setIsDirectory(true);
        directory.setFileSize(0L);
        directory.setUpdateTime(new Date());
        addResourceProperties(xml, directory);

        // 添加子项
        for (SysWebdavFile file : files) {
            // 确保只显示直接子项
            if (file.getFilePath().equals(basePath)) {
                continue;
            }

            if (file.getFilePath().split("/").length != basePath.split("/").length + 1) {
                continue;
            }

            addResourceProperties(xml, file);
        }

        xml.append("</D:multistatus>");
        return xml.toString();
    }

    /**
     * 添加资源属性到XML响应
     */
    private void addResourceProperties(StringBuilder xml, SysWebdavFile file) {
        xml.append("  <D:response>\n");
        xml.append("    <D:href>").append(file.getFilePath()).append("</D:href>\n");
        xml.append("    <D:propstat>\n");
        xml.append("      <D:prop>\n");
        xml.append("        <D:displayname>").append(file.getFileName()).append("</D:displayname>\n");
        xml.append("        <D:getcontentlength>").append(file.getFileSize()).append("</D:getcontentlength>\n");
        xml.append("        <D:getlastmodified>").append(formatDate(file.getUpdateTime())).append("</D:getlastmodified>\n");
        xml.append("        <D:getcontenttype>").append(getContentType(file)).append("</D:getcontenttype>\n");
        xml.append("        <D:resourcetype>");
        if (file.getIsDirectory()) {
            xml.append("<D:collection/>");
        }
        xml.append("</D:resourcetype>\n");
        xml.append("      </D:prop>\n");
        xml.append("      <D:status>HTTP/1.1 200 OK</D:status>\n");
        xml.append("    </D:propstat>\n");
        xml.append("  </D:response>\n");
    }

    /**
     * 格式化日期为HTTP标准格式
     */
    private String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    /**
     * 获取文件内容类型
     */
    private String getContentType(SysWebdavFile file) {
        if (file.getIsDirectory()) {
            return "httpd/unix-directory";
        }

        if (file.getFileType() != null) {
            return file.getFileType();
        }

        // 根据文件扩展名猜测内容类型
        String fileName = file.getFileName();
        int extIndex = fileName.lastIndexOf('.');
        if (extIndex > 0) {
            String ext = fileName.substring(extIndex + 1).toLowerCase();
            switch (ext) {
                case "txt": return "text/plain";
                case "html": return "text/html";
                case "jpg": case "jpeg": return "image/jpeg";
                case "png": return "image/png";
                case "pdf": return "application/pdf";
                case "zip": return "application/zip";
                default: return "application/octet-stream";
            }
        }

        return "application/octet-stream";
    }

    /**
     * 从URL中提取路径
     */
    private String extractPathFromUrl(String url) {
        int index = url.indexOf("/system/webdav");
        if (index >= 0) {
            return url.substring(index + "/system/webdav".length());
        }
        return url;
    }

    /**
     * 获取父目录ID
     */
    private Long getParentId(String path) {
        if ("/".equals(path) || path.isEmpty()) return 0L;

        SysWebdavFile parent = fileService.selectFileByPath(path);
        if (parent != null) {
            return parent.getFileId();
        }

        // 如果父目录不存在，递归查找
        String parentPath = path.substring(0, path.lastIndexOf('/'));
        return getParentId(parentPath);
    }
}
