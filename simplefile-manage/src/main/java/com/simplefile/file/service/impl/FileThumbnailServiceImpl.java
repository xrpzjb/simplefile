package com.simplefile.file.service.impl;

import java.util.List;
import com.simplefile.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.simplefile.file.mapper.FileThumbnailMapper;
import com.simplefile.file.domain.FileThumbnail;
import com.simplefile.file.service.IFileThumbnailService;

/**
 * 缩略图Service业务层处理
 *
 * @author zhujiabao
 * @date 2024-09-03
 */
@Service
public class FileThumbnailServiceImpl implements IFileThumbnailService
{
    @Autowired
    private FileThumbnailMapper fileThumbnailMapper;

    /**
     * 查询缩略图
     *
     * @param thumbnailId 缩略图主键
     * @return 缩略图
     */
    @Override
    public FileThumbnail selectFileThumbnailByThumbnailId(Long thumbnailId)
    {
        return fileThumbnailMapper.selectFileThumbnailByThumbnailId(thumbnailId);
    }

    /**
     * 查询缩略图列表
     *
     * @param fileThumbnail 缩略图
     * @return 缩略图
     */
    @Override
    public List<FileThumbnail> selectFileThumbnailList(FileThumbnail fileThumbnail)
    {
        return fileThumbnailMapper.selectFileThumbnailList(fileThumbnail);
    }

    /**
     * 新增缩略图
     *
     * @param fileThumbnail 缩略图
     * @return 结果
     */
    @Override
    public int insertFileThumbnail(FileThumbnail fileThumbnail)
    {
        fileThumbnail.setCreateTime(DateUtils.getNowDate());
        return fileThumbnailMapper.insertFileThumbnail(fileThumbnail);
    }

    /**
     * 修改缩略图
     *
     * @param fileThumbnail 缩略图
     * @return 结果
     */
    @Override
    public int updateFileThumbnail(FileThumbnail fileThumbnail)
    {
        fileThumbnail.setUpdateTime(DateUtils.getNowDate());
        return fileThumbnailMapper.updateFileThumbnail(fileThumbnail);
    }

    /**
     * 批量删除缩略图
     *
     * @param thumbnailIds 需要删除的缩略图主键
     * @return 结果
     */
    @Override
    public int deleteFileThumbnailByThumbnailIds(Long[] thumbnailIds)
    {
        return fileThumbnailMapper.deleteFileThumbnailByThumbnailIds(thumbnailIds);
    }

    /**
     * 删除缩略图信息
     *
     * @param thumbnailId 缩略图主键
     * @return 结果
     */
    @Override
    public int deleteFileThumbnailByThumbnailId(Long thumbnailId)
    {
        return fileThumbnailMapper.deleteFileThumbnailByThumbnailId(thumbnailId);
    }
}
