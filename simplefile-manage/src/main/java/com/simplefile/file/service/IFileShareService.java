package com.simplefile.file.service;

import java.util.Date;
import java.util.List;
import com.simplefile.file.domain.FileShare;
import com.simplefile.file.dto.DiskFileShareDto;

/**
 * 文件分享Service接口
 * 
 * @author zhujiabao
 * @date 2024-09-03
 */
public interface IFileShareService 
{
    /**
     * 查询文件分享
     * 
     * @param shareId 文件分享主键
     * @return 文件分享
     */
    public FileShare selectFileShareByShareId(Long shareId);

    /**
     * 查询文件分享列表
     * 
     * @param fileShare 文件分享
     * @return 文件分享集合
     */
    public List<FileShare> selectFileShareList(FileShare fileShare);

    /**
     * 新增文件分享
     * 
     * @param fileShare 文件分享
     * @return 结果
     */
    public int insertFileShare(FileShare fileShare);

    /**
     * 修改文件分享
     * 
     * @param fileShare 文件分享
     * @return 结果
     */
    public int updateFileShare(FileShare fileShare);

    /**
     * 批量删除文件分享
     * 
     * @param shareIds 需要删除的文件分享主键集合
     * @return 结果
     */
    public int deleteFileShareByShareIds(Long[] shareIds);

    /**
     * 删除文件分享信息
     * 
     * @param shareId 文件分享主键
     * @return 结果
     */
    public int deleteFileShareByShareId(Long shareId);

    /**
     * 分享
     */
    void share(DiskFileShareDto shareDto);
}
