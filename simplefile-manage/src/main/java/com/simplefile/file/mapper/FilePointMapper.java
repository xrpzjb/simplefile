package com.simplefile.file.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplefile.file.domain.FileInfo;
import com.simplefile.file.domain.FilePoint;

/**
 * 文件映射Mapper接口
 *
 * @author zhujiabao
 * @date 2024-09-03
 */
public interface FilePointMapper extends BaseMapper<FilePoint>
{
    /**
     * 查询文件映射
     *
     * @param pointId 文件映射主键
     * @return 文件映射
     */
    public FilePoint selectFilePointByPointId(Long pointId);

    /**
     * 查询文件映射列表
     *
     * @param filePoint 文件映射
     * @return 文件映射集合
     */
    public List<FilePoint> selectFilePointList(FilePoint filePoint);

    /**
     * 新增文件映射
     *
     * @param filePoint 文件映射
     * @return 结果
     */
    public int insertFilePoint(FilePoint filePoint);

    /**
     * 修改文件映射
     *
     * @param filePoint 文件映射
     * @return 结果
     */
    public int updateFilePoint(FilePoint filePoint);

    /**
     * 删除文件映射
     *
     * @param pointId 文件映射主键
     * @return 结果
     */
    public int deleteFilePointByPointId(Long pointId);

    /**
     * 批量删除文件映射
     *
     * @param pointIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFilePointByPointIds(Long[] pointIds);
}
