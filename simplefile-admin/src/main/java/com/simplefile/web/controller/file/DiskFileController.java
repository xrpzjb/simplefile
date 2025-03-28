package com.simplefile.web.controller.file;

import com.aspose.words.Document;
import com.simplefile.common.core.domain.AjaxResult;

import com.simplefile.common.file.DiskFile;
import com.simplefile.common.file.DiskFileUtil;
import com.simplefile.common.file.DiskTreeNode;
import com.simplefile.common.utils.file.FileUtils;
import com.simplefile.file.domain.FileInfo;
import com.simplefile.file.domain.FileShare;
import com.simplefile.file.dto.DiskFileHandleDto;
import com.simplefile.file.dto.DiskFileListDto;
import com.simplefile.file.dto.DiskFileShareDto;
import com.simplefile.file.enums.FileRecycleLevelEnum;
import com.simplefile.file.enums.FileTypeEnum;
import com.simplefile.file.manager.FileAsyncManager;
import com.simplefile.file.manager.factory.FileAsyncFactory;
import com.simplefile.file.service.IFileInfoService;
import com.simplefile.file.service.IFileShareService;
import com.simplefile.file.vo.DirCountFileSizeVo;
import com.simplefile.file.vo.DiskFileDataVo;
import com.simplefile.system.domain.SysConfig;
import com.simplefile.system.service.ISysConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件控制器
 *
 * @author zhujiaboa
 * @date 2024/08/22
 *
 */
@RestController
@RequestMapping("/simplefile/file")
public class DiskFileController {

    @Resource
    private IFileInfoService fileInfoService;

    @Resource
    private IFileShareService fileShareService;

    @Resource
    private ISysConfigService sysConfigService;

    /**
     * 正常文件列表
     */
    // @PreAuthorize("@ss.hasPermi('system:config:list')")
    @PostMapping("/list")
    public AjaxResult list(@RequestBody DiskFileListDto diskFileListRequest)
    {
        diskFileListRequest.setRecycleBol(false);
        DiskFileDataVo diskFileList = fileInfoService.getDiskFileList(diskFileListRequest);
        return AjaxResult.success(diskFileList);
    }

    /**
     * 统计占用总量
     */
    @PostMapping("/count")
    public AjaxResult count(@RequestBody DiskFileListDto diskFileListRequest)
    {
        if(StringUtils.isBlank(diskFileListRequest.getDirId())){
            diskFileListRequest.setDirId("0");
        }
        DirCountFileSizeVo dirCountFileSizeVo = fileInfoService.countDirFileSize(Long.valueOf(diskFileListRequest.getDirId()),
                null,
                diskFileListRequest.getRecycleBol());
        String fileSizeName = DiskFileUtil.getFileSizeName(dirCountFileSizeVo.getFileSize());
        dirCountFileSizeVo.setFileSizeName(fileSizeName);
        return AjaxResult.success(dirCountFileSizeVo);
    }


    /**
     * 回收站文件列表
     * @param diskFileListRequest
     * @return
     */
    @PostMapping("/recoverylist")
    public AjaxResult recoverylist(@RequestBody DiskFileListDto diskFileListRequest)
    {
        diskFileListRequest.setRecycleBol(true);
        diskFileListRequest.setHide(true);
        if(diskFileListRequest.getDirId() == null){
            diskFileListRequest.setRecycleLevel(FileRecycleLevelEnum.RECYCLE_ONE.getDataValue());
        }
        DiskFileDataVo diskFileList = fileInfoService.getDiskFileList(diskFileListRequest);
        return AjaxResult.success(diskFileList);
    }

    /**
     * 文件详情接口
     * @param fileId
     * @return
     */
    @GetMapping("/get")
    public AjaxResult get(Long fileId)
    {
        FileInfo fileInfo = fileInfoService.selectFileInfoByFileId(fileId);
        DiskFile diskFile = fileInfoService.fileToDiskFile(fileInfo);
        diskFile.setPages(1);
        // 判断docx判断页数
        if(fileInfo != null && fileInfo.getFileType() == FileTypeEnum.DOC.getDataValue()){
            try {
                Document document = new Document(FileUtils.getInputStream(new File(fileInfo.getPath())));
                if(document != null){
                    diskFile.setPages(document.getPageCount());
                    if(document.getPageCount() > 20){
                        diskFile.setPages(20);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return AjaxResult.success(diskFile);
    }

    /**
     * 压缩文件获取文件接口
     * @param fileId
     * @return
     */
    @GetMapping("/zipget")
    public AjaxResult zipget(Long fileId)
    {
        FileInfo fileInfo = fileInfoService.selectFileInfoByFileId(fileId);
        if(fileInfo == null){
            return AjaxResult.error();
        }
        if(fileInfo != null && fileInfo.getFileType() == FileTypeEnum.ZIP.getDataValue()){
            DiskTreeNode diskTreeNode = DiskFileUtil.buildTree(fileInfo.getPath());
            List<DiskTreeNode> diskTreeNodeList = new ArrayList<>();
            diskTreeNodeList.add(diskTreeNode);
            return AjaxResult.success(diskTreeNodeList);
        }
        return AjaxResult.success();
    }

    /**
     * 解压文件
     * @param fileId
     * @return
     */
    @GetMapping("/unzip")
    public AjaxResult unzip(Long fileId, Integer type)
    {
        if(fileId == null){
            return AjaxResult.error("请求错误");
        }
        fileInfoService.unzipFile(fileId);
        return AjaxResult.success();
    }

    /**
     * 文件移动
     */
    // @PreAuthorize("@ss.hasPermi('system:config:list')")
    @PostMapping("/mv")
    public AjaxResult mv(@RequestBody DiskFileHandleDto diskFileListRequest)
    {
        fileInfoService.mv(diskFileListRequest.getFileId(), diskFileListRequest.getDirId());
        return AjaxResult.success();
    }

    /**
     * 文件复制
     */
    // @PreAuthorize("@ss.hasPermi('system:config:list')")
    @PostMapping("/copy")
    public AjaxResult copy(@RequestBody DiskFileHandleDto diskFileListRequest)
    {
        fileInfoService.copy(diskFileListRequest.getFileId(), diskFileListRequest.getDirId());
        return AjaxResult.success();
    }

    /**
     * 文件删除
     */
    // @PreAuthorize("@ss.hasPermi('system:config:list')")
    @PostMapping("/del")
    public AjaxResult del(@RequestBody DiskFileHandleDto diskFileListRequest)
    {
        fileInfoService.del(diskFileListRequest.getFileId());
        return AjaxResult.success();
    }

    /**
     * 文件物理立即删除
     */
    // @PreAuthorize("@ss.hasPermi('system:config:list')")
    @PostMapping("/diskDel")
    public AjaxResult diskDel(@RequestBody DiskFileHandleDto diskFileListRequest)
    {
        fileInfoService.delFile(diskFileListRequest.getFileId());
        return AjaxResult.success();
    }

    /**
     * 回收站文件物理立即删除全部
     */
    // @PreAuthorize("@ss.hasPermi('system:config:list')")
    @PostMapping("/diskDelAll")
    public AjaxResult diskDelAll()
    {
        List<FileInfo> recycleFileList = fileInfoService.getRecycleFileList(null);
        if(recycleFileList != null){
            recycleFileList.stream().forEach(r->{
                fileInfoService.delFile(r.getFileId());
            });
        }
        return AjaxResult.success();
    }

    /**
     * 文件删除还原
     */
    // @PreAuthorize("@ss.hasPermi('system:config:list')")
    @PostMapping("/diskRestore")
    public AjaxResult diskRestore(@RequestBody DiskFileHandleDto diskFileListRequest)
    {
        fileInfoService.diskRestore(diskFileListRequest.getFileId());
        return AjaxResult.success();
    }

    /**
     * 文件重命名
     */
    // @PreAuthorize("@ss.hasPermi('system:config:list')")
    @PostMapping("/rename")
    public AjaxResult rename(@RequestBody DiskFileHandleDto diskFileListRequest)
    {
        if(diskFileListRequest.getFileId() == null
                || StringUtils.isBlank(diskFileListRequest.getNewName())){
            return AjaxResult.error("请求错误");
        }
        fileInfoService.rename(diskFileListRequest.getFileId(), diskFileListRequest.getNewName());
        return AjaxResult.success();
    }

    /**
     * 创建文件夹
     */
    // @PreAuthorize("@ss.hasPermi('system:config:list')")
    @PostMapping("/createDir")
    public AjaxResult createDir(@RequestBody DiskFileHandleDto diskFileListRequest)
    {
        if(StringUtils.isBlank(diskFileListRequest.getNewName())){
            return AjaxResult.error("请求错误");
        }
        fileInfoService.createDir(diskFileListRequest.getFileId(), diskFileListRequest.getNewName());
        return AjaxResult.success();
    }

    /**
     * 分享
     */
    @PostMapping("/share")
    public AjaxResult share(@RequestBody DiskFileShareDto shareDto)
    {
        if(shareDto.getFileId() == null){
            return AjaxResult.error("请指定文件分享");
        }
        fileShareService.share(shareDto);
        FileShare fileShare = new FileShare();
        fileShare.setFileId(shareDto.getFileId());
        List<FileShare> fileShareList = fileShareService.selectFileShareList(fileShare);
        if(fileShareList.isEmpty()){
            return AjaxResult.error("分享失败");
        }else{
            FileShare share = fileShareList.get(0);
            String siteAddress = sysConfigService.selectConfigByKey(SysConfig.CONFIG_SITE_ADDRES);
            String link = siteAddress + "/share/" + share.getShareCode();
            return AjaxResult.success("分享成功", link);
        }
    }

    /**
     * 分享详情
     */
    @GetMapping("/shareDetail")
    public AjaxResult shareDetail(DiskFileHandleDto shareDto)
    {
        if(shareDto.getFileId() == null){
            return AjaxResult.error("未指定文件");
        }
        FileShare fileShare = new FileShare();
        fileShare.setFileId(shareDto.getFileId());
        List<FileShare> fileShareList = fileShareService.selectFileShareList(fileShare);
        if(fileShareList.isEmpty()){
            fileShare.setShareEncryCode(FileShare.getNewShareEncryCode());
            return AjaxResult.success(fileShare);
        }else{
            FileShare share = fileShareList.get(0);
            String siteAddress = sysConfigService.selectConfigByKey(SysConfig.CONFIG_SITE_ADDRES);
            share.setLink(siteAddress + "/share/" + share.getShareCode());
        }
        return AjaxResult.success(fileShareList.get(0));
    }

}
