package com.simplefile.file.mapper;

import java.util.List;
import com.simplefile.file.domain.FileShare;

/**
 * 文件分享Mapper接口
 * 
 * @author zhujiabao
 * @date 2024-09-03
 */
public interface FileShareMapper 
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
     * 删除文件分享
     * 
     * @param shareId 文件分享主键
     * @return 结果
     */
    public int deleteFileShareByShareId(Long shareId);

    /**
     * 批量删除文件分享
     * 
     * @param shareIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFileShareByShareIds(Long[] shareIds);
}
