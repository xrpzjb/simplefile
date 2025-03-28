package com.simplefile.web.controller.file;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.simplefile.common.cache.GuavaCommonLocalCache;
import com.simplefile.common.core.domain.AjaxResult;
import com.simplefile.common.file.DiskFile;
import com.simplefile.file.domain.FileInfo;
import com.simplefile.file.domain.FileShare;
import com.simplefile.file.dto.DiskFileListDto;
import com.simplefile.file.service.IFileInfoService;
import com.simplefile.file.service.IFilePointService;
import com.simplefile.file.service.IFileShareService;
import com.simplefile.file.vo.DiskFileDataVo;
import com.simplefile.file.vo.ShareDetailVo;
import com.simplefile.file.vo.ShareFileIdEncryVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 文件分享
 */
@RestController
@RequestMapping("/share")
public class ShareController {

    @Resource
    private IFileInfoService fileInfoService;

    @Resource
    private IFilePointService filePointService;

    @Resource
    private IFileShareService fileShareService;


    /**
     * 分享详情
     *
     * 访问主页获取分享详情数据
     *
     */
    @PostMapping("/detail/{shareCode}")
    public AjaxResult shareDetail(@PathVariable("shareCode") String shareCode)
    {
        if(StringUtils.isBlank(shareCode)){
            return AjaxResult.error("未指定分享码");
        }
        FileShare fileShare = new FileShare();
        fileShare.setShareCode(shareCode);
        List<FileShare> fileShareList = fileShareService.selectFileShareList(fileShare);
        if(fileShareList.isEmpty()){
            return AjaxResult.success();
        }
        FileShare share = fileShareList.get(0);
        FileInfo fileInfo = fileInfoService.selectFileInfoByFileId(share.getFileId());
        ShareDetailVo shareDetailVo = new ShareDetailVo();
        shareDetailVo.setShareCode(shareCode);
        shareDetailVo.setShareName(fileInfo.getName());
        shareDetailVo.setHavPwd(false);
        if(StringUtils.isBlank(share.getShareEncryCode())){
            shareDetailVo.setHavPwd(true);
        }
        return AjaxResult.success();
    }

    /**
     * 分享的文件列表
     */
    @PostMapping("/list/{shareIdToken}")
    public AjaxResult list(@RequestBody DiskFileListDto diskFileListRequest,
                           @PathVariable("shareIdToken") String shareIdToken)
    {
        // 加密之后的
        String dirId = diskFileListRequest.getDirId();
        // 获取临时token
        Object tokenObject = GuavaCommonLocalCache.getCacheObject(GuavaCommonLocalCache.KEY_SHARE_ID_TOKEN, shareIdToken);
        if(tokenObject == null){
            return AjaxResult.error("非法请求");
        }
        String shareCode = tokenObject.toString();
        FileShare fileShare = new FileShare();
        fileShare.setShareCode(shareCode);
        List<FileShare> fileShareList = fileShareService.selectFileShareList(fileShare);
        if(fileShareList.isEmpty()){
            return AjaxResult.error("没有分享的内容");
        }
        FileShare share = fileShareList.get(0);
        FileInfo shareParent = fileInfoService.selectFileInfoByFileId(share.getFileId());
        if(dirId == null){
            // 默认
            dirId = shareParent.getParentId().toString();
            diskFileListRequest.setNowFileId(share.getFileId().toString());
        }
        FileInfo fileInfo = fileInfoService.selectFileInfoByFileId(Long.valueOf(dirId));
        if(fileInfo == null){
            return AjaxResult.error("访问分享的文件错误");
        }
        if(!fileInfo.getPointPath().startsWith(shareParent.getPointPath())){
            return AjaxResult.error("非法访问");
        }

//        ShareFileIdEncryVo shareFile = (ShareFileIdEncryVo) dirObject;
//        if(!shareFile.getShareCode().equals(shareCode)){
//            return AjaxResult.error("非法请求");
//        }
        diskFileListRequest.setDirId(dirId);
        diskFileListRequest.setRecycleBol(false);
        DiskFileDataVo diskFile = fileInfoService.getDiskFileList(diskFileListRequest);
//        List<DiskFile> diskFileList = diskFile.getDiskFileList();
//        diskFileList.stream().forEach(k->{
//            String shareEncryId = IdWorker.get32UUID();
//            ShareFileIdEncryVo share = new ShareFileIdEncryVo(shareEncryId, k.getFileId().toString(), shareCode);
//            GuavaCommonLocalCache.setCacheObject(GuavaCommonLocalCache.KEY_SHARE_ID_ENCRY, shareEncryId, share);
//            k.setShareFileId(shareEncryId);
//            k.setFileId(null);
//        });
        return AjaxResult.success(diskFile);
    }

    /**
     * 访问验证
     */
    @PostMapping("/verify/{shareCode}")
    public AjaxResult share(@PathVariable("shareCode") String shareCode,
                            @RequestParam(value = "pwd") String pwd)
    {
        if(StringUtils.isBlank(shareCode)){
            return AjaxResult.error("非法请求");
        }
        FileShare fileShare = new FileShare();
        fileShare.setShareCode(shareCode);
        List<FileShare> fileShareList = fileShareService.selectFileShareList(fileShare);
        if(!fileShareList.isEmpty()){
            return AjaxResult.error("未找到相关分享信息");
        }
        fileShare = fileShareList.get(0);
        if(StringUtils.isBlank(fileShare.getShareEncryCode())){
            return AjaxResult.success();
        }
        if(StringUtils.isNotBlank(fileShare.getShareEncryCode())
                && StringUtils.isBlank(pwd)){
            return AjaxResult.error("访问资源密码错误");
        }
        // 验证成功
        String shareEncryId = IdWorker.get32UUID();
        // 存临时key
        GuavaCommonLocalCache.setCacheObject(GuavaCommonLocalCache.KEY_SHARE_ID_TOKEN, shareEncryId, shareCode);
        return AjaxResult.success(shareEncryId);
    }

}
