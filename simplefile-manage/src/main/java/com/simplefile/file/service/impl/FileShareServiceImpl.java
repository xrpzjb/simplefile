package com.simplefile.file.service.impl;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.simplefile.common.utils.DateUtils;
import com.simplefile.file.dto.DiskFileShareDto;
import com.simplefile.file.enums.ShareEncryTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.simplefile.file.mapper.FileShareMapper;
import com.simplefile.file.domain.FileShare;
import com.simplefile.file.service.IFileShareService;

/**
 * 文件分享Service业务层处理
 *
 * @author zhujiabao
 * @date 2024-09-03
 */
@Service
public class FileShareServiceImpl implements IFileShareService
{
    @Autowired
    private FileShareMapper fileShareMapper;

    /**
     * 查询文件分享
     *
     * @param shareId 文件分享主键
     * @return 文件分享
     */
    @Override
    public FileShare selectFileShareByShareId(Long shareId)
    {
        return fileShareMapper.selectFileShareByShareId(shareId);
    }

    /**
     * 查询文件分享列表
     *
     * @param fileShare 文件分享
     * @return 文件分享
     */
    @Override
    public List<FileShare> selectFileShareList(FileShare fileShare)
    {
        return fileShareMapper.selectFileShareList(fileShare);
    }

    /**
     * 新增文件分享
     *
     * @param fileShare 文件分享
     * @return 结果
     */
    @Override
    public int insertFileShare(FileShare fileShare)
    {
        fileShare.setCreateTime(DateUtils.getNowDate());
        return fileShareMapper.insertFileShare(fileShare);
    }

    /**
     * 修改文件分享
     *
     * @param fileShare 文件分享
     * @return 结果
     */
    @Override
    public int updateFileShare(FileShare fileShare)
    {
        fileShare.setUpdateTime(DateUtils.getNowDate());
        return fileShareMapper.updateFileShare(fileShare);
    }

    /**
     * 批量删除文件分享
     *
     * @param shareIds 需要删除的文件分享主键
     * @return 结果
     */
    @Override
    public int deleteFileShareByShareIds(Long[] shareIds)
    {
        return fileShareMapper.deleteFileShareByShareIds(shareIds);
    }

    /**
     * 删除文件分享信息
     *
     * @param shareId 文件分享主键
     * @return 结果
     */
    @Override
    public int deleteFileShareByShareId(Long shareId)
    {
        return fileShareMapper.deleteFileShareByShareId(shareId);
    }

    /**
     * 分享
     */
    @Override
    public void share(DiskFileShareDto shareDto) {
        // 判断是否有分享
        FileShare fileShare  = new FileShare();
        fileShare.setFileId(shareDto.getFileId());
        List<FileShare> fileShareList = selectFileShareList(fileShare);
        if(fileShareList.isEmpty()){
            fileShare  = new FileShare();
            fileShare.setFileId(shareDto.getFileId());
            fileShare.setVisitNum(0L);
            fileShare.setCreateTime(new Date());
        }else {
            fileShare = fileShareList.get(0);
        }
        fileShare.setShareEncryCode(shareDto.getShareEncryCode());
        fileShare.setShareEncryType(StringUtils.isBlank(shareDto.getShareEncryCode())
                ? ShareEncryTypeEnum.SHARE_NOT_ENCRY.getDataValue()
                : ShareEncryTypeEnum.SHARE_ENCRY.getDataValue());
        fileShare.setShareTime(shareDto.getShareTime());
        fileShare.setShareCode(FileShare.newShareCode());
        if(fileShare.getShareId() == null){
            fileShare.setShareId(IdWorker.getId());
            insertFileShare(fileShare);
        }else{
            fileShare.setUpdateTime(new Date());
            updateFileShare(fileShare);
        }
    }
}
