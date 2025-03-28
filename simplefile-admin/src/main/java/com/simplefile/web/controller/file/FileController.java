package com.simplefile.web.controller.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.simplefile.common.core.domain.AjaxResult;
import com.simplefile.common.file.DiskFileUtil;
import com.simplefile.common.utils.file.FileUtils;
import com.simplefile.file.domain.FileInfo;
import com.simplefile.file.domain.FilePoint;
import com.simplefile.file.enums.FileTypeEnum;
import com.simplefile.file.service.IFileInfoService;
import com.simplefile.file.service.IFilePointService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

/**
 * 具体文件的预览、上传、下载
 *
 */
@RestController
@RequestMapping("/simplefile/file")
public class FileController {

    @Resource
    private IFileInfoService fileInfoService;

    @Resource
    private IFilePointService filePointService;


    /**
     * 文件预览
     *
     * 图片、txt、doc、excel
     *
     * @param fileId
     * @param response
     * @param request
     */
    @RequestMapping(value = "/preview/{fileId}/{type}" , method = RequestMethod.GET)
    public void preview(@PathVariable("fileId") Long fileId,
                             @PathVariable("type") String type,
                             Integer current,
                             HttpServletResponse response,
                             HttpServletRequest request){
        FileInfo fileInfo = fileInfoService.selectFileInfoByFileId(fileId);
        if(fileInfo == null){
            return;
        }
        if(current == null){
            current = 1;
        }
        String fileName = fileInfo.getName();
        String fileSuffix = DiskFileUtil.getFileSuffix(fileName);
        String contentType = DiskFileUtil.diskFileContentTypeMap.get(fileSuffix);

        OutputStream sos = null;
        try {
            response.setContentType(contentType);
            response.setHeader("Content-Type", contentType);
            // 获取原文件名
            String fileNewName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置下载文件名
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileNewName + "\"");
            sos = response.getOutputStream();
            String filePath = fileInfo.getPath();
            if(fileInfo.getFileType() == FileTypeEnum.PIC.getDataValue()){
                // 图片
                if(fileInfo.getThumbnail() != null && fileInfo.getThumbnail()){
                    LambdaQueryWrapper<FilePoint> dirQueryWrapper = new QueryWrapper<FilePoint>().lambda();
                    dirQueryWrapper.eq(FilePoint::getPointPath, "/");
                    dirQueryWrapper.last("limit 1");
                    FilePoint filePoint = filePointService.getOne(dirQueryWrapper);
                    if(filePoint == null){
                        filePath = filePoint.getSystemPath() + fileInfo.getThumbnailPath() + "/" + type + ".jpg";
                    }
                }
            }
//            else if(fileInfo.getFileType() == FileTypeEnum.DOC.getDataValue()){
//                response.setContentType("image/png");
//                response.setHeader("Content-Type", "image/png");
//                // 文档
//                Document document = new Document(FileUtils.getInputStream(new File(filePath)));
//                // 只转第一页，如果有多页，这里得循环一下
//                int pageCount = document.getPageCount();
//                if(current > pageCount){
//                    return;
//                }
//                Document page = document.extractPages(current - 1, 1);
//                page.save(sos, SaveFormat.PNG);
//                return;
//            }
//            else if(fileInfo.getFileType() == FileTypeEnum.EXCEL.getDataValue()){
//                response.setContentType("image/png");
//                response.setHeader("Content-Type", "image/png");
//                Workbook workbook = new Workbook(filePath);
//                Workbook page = workbook.getWorksheets().get(0).getWorkbook();
//                page.save(sos, SaveFormat.PNG);
//            }
            if(fileInfo.getFileSize() > 1024*1024*20){
                response.getOutputStream().print("文件过大");
                return;
            }
            FileUtils.writeBytes(filePath, sos);
        } catch (IOException e) {
            sos = null;
            e.printStackTrace();
        } catch (Exception e) {
            sos = null;
            e.printStackTrace();
        } finally {

        }
    }

    /**
     * 文件下载
     *
     *
     * @param fileId
     * @param response
     * @param request
     */
    @RequestMapping(value = "/download/{fileId}" , method = RequestMethod.GET)
    public void download(@PathVariable("fileId") Long fileId,
                        HttpServletResponse response,
                        HttpServletRequest request){
        FileInfo fileInfo = fileInfoService.selectFileInfoByFileId(fileId);
        if(fileInfo == null){
            return;
        }
        String fileName = fileInfo.getName();
        OutputStream sos = null;
        try {
            sos = response.getOutputStream();
            response.setContentType("multipart/form-data");
            // 获取原文件名
            String fileNewName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            // 设置下载文件名
            response.addHeader("Content-Disposition", "inline; filename=\"" + fileNewName + "\"");

            String filePath = fileInfo.getPath();
            FileUtils.writeBytes(filePath, sos);
        } catch (IOException e) {
            sos = null;
            e.printStackTrace();
        } catch (Exception e) {
            sos = null;
            e.printStackTrace();
        } finally {

        }
    }

    /**
     * 文件上传
     * @param file
     * @param request
     * @return
     */
    @PostMapping("/upload")
    @ResponseBody
    public AjaxResult handleFileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return AjaxResult.error( "文件为空，请重新上传");
        }
        String newPath = null;
        try {
            // 获取文件名
            String fileName = file.getOriginalFilename();
            String dirId = request.getParameter("dirId");
            if(StringUtils.isBlank(dirId)){
                return AjaxResult.error( "上传目录为空，无法上传" + file.getOriginalFilename(), fileName);
            }
            // 判断该目录是否存在该文件名称，存在就改成数量+1
            LambdaQueryWrapper<FileInfo> fileInfoWrapper = new QueryWrapper<FileInfo>().lambda();
            fileInfoWrapper.eq(FileInfo::getParentId, dirId);
            fileInfoWrapper.eq(FileInfo::getName, fileName);
            long count = fileInfoService.count(fileInfoWrapper);
            if(count > 0) {
                return AjaxResult.error( "存在文件: " + fileName);
            }
            FileInfo dicFileInfo = fileInfoService.getById(dirId);
            String path = null;
            String pointPath = null;
            if(dirId.equals("0")){
                LambdaQueryWrapper<FilePoint> dirQueryWrapper = new QueryWrapper<FilePoint>().lambda();
                dirQueryWrapper.eq(FilePoint::getPointPath, "/");
                dirQueryWrapper.last("limit 1");
                FilePoint filePoint = filePointService.getOne(dirQueryWrapper);
                if(filePoint != null){
                    path = filePoint.getSystemPath();
                    pointPath = filePoint.getPointPath();
                }
            }else{
                path = dicFileInfo.getPath();
                pointPath = dicFileInfo.getPointPath();
            }

            // 获取文件的字节
            byte[] bytes = file.getBytes();

            newPath = path + "/" + fileName;

            FileOutputStream fos = new FileOutputStream(newPath);
            fos.write(bytes);

            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileId(IdWorker.getId());
            fileInfo.setFileSize(file.getSize());
            fileInfo.setThumbnail(false);
            fileInfo.setName(fileName);
            fileInfo.setPointPath(pointPath + "/" + fileName);
            fileInfo.setPath(newPath);
            fileInfo.setParentId(Long.valueOf(dirId));
            Integer type = DiskFileUtil.getFileType(fileInfo.getName());
            fileInfo.setFileType(type);
            fileInfo.setChildCount(0);
            fileInfo.setDirBol(false);
            fileInfo.setHide(false);
            fileInfo.setCreateTime(new Date());
            fileInfo.setUpdateTime(new Date());
            fileInfo.setFileUpdateTime(new Date());
            fileInfo.setVisitNum(0);
            fileInfoService.save(fileInfo);

            // 返回成功响应
            return AjaxResult.success( "文件上传成功: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            if(StringUtils.isNotBlank(newPath)){
                FileUtils.deleteFile(newPath);
            }
            return AjaxResult.error( "文件上传失败，请重试" + file.getOriginalFilename());
        }
    }

}
