package com.simplefile.file.mapper;

import java.util.List;
import com.simplefile.file.domain.FileThumbnail;

/**
 * 缩略图Mapper接口
 * 
 * @author zhujiabao
 * @date 2024-09-03
 */
public interface FileThumbnailMapper 
{
    /**
     * 查询缩略图
     * 
     * @param thumbnailId 缩略图主键
     * @return 缩略图
     */
    public FileThumbnail selectFileThumbnailByThumbnailId(Long thumbnailId);

    /**
     * 查询缩略图列表
     * 
     * @param fileThumbnail 缩略图
     * @return 缩略图集合
     */
    public List<FileThumbnail> selectFileThumbnailList(FileThumbnail fileThumbnail);

    /**
     * 新增缩略图
     * 
     * @param fileThumbnail 缩略图
     * @return 结果
     */
    public int insertFileThumbnail(FileThumbnail fileThumbnail);

    /**
     * 修改缩略图
     * 
     * @param fileThumbnail 缩略图
     * @return 结果
     */
    public int updateFileThumbnail(FileThumbnail fileThumbnail);

    /**
     * 删除缩略图
     * 
     * @param thumbnailId 缩略图主键
     * @return 结果
     */
    public int deleteFileThumbnailByThumbnailId(Long thumbnailId);

    /**
     * 批量删除缩略图
     * 
     * @param thumbnailIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFileThumbnailByThumbnailIds(Long[] thumbnailIds);
}
