package com.simplefile.file.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.simplefile.file.domain.FileInfo;
import com.simplefile.file.domain.FilePoint;

/**
 * 文件映射Service接口
 * 
 * @author zhujiabao
 * @date 2024-09-03
 */
public interface IFilePointService extends IService<FilePoint>
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
     * 批量删除文件映射
     * 
     * @param pointIds 需要删除的文件映射主键集合
     * @return 结果
     */
    public int deleteFilePointByPointIds(Long[] pointIds);

    /**
     * 删除文件映射信息
     * 
     * @param pointId 文件映射主键
     * @return 结果
     */
    public int deleteFilePointByPointId(Long pointId);

    /**
     * 文件全量扫描
     * @param piontId
     */
    public void filepointScannAll(Long piontId);

    /**
     * 文件增量扫描
     * @param piontId
     */
    public void filepointScannUpdate(Long piontId);
}
