package com.simplefile.file.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.simplefile.common.exception.ServiceException;
import com.simplefile.common.utils.DateUtils;
import com.simplefile.file.domain.FileInfo;
import com.simplefile.file.enums.FilePointScannStatusEnum;
import com.simplefile.file.manager.FileAsyncManager;
import com.simplefile.file.manager.factory.FileAsyncFactory;
import com.simplefile.file.mapper.FileInfoMapper;
import com.simplefile.file.service.IFileInfoService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.simplefile.file.mapper.FilePointMapper;
import com.simplefile.file.domain.FilePoint;
import com.simplefile.file.service.IFilePointService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文件映射Service业务层处理
 *
 * @author zhujiabao
 * @date 2024-09-03
 */
@Service
public class FilePointServiceImpl extends ServiceImpl<FilePointMapper, FilePoint> implements IFilePointService
{
    @Autowired
    private FilePointMapper filePointMapper;

    @Autowired
    private IFileInfoService fileInfoService;

    @Autowired
    private FileInfoMapper fileInfoMapper;

    /**
     * 查询文件映射
     *
     * @param pointId 文件映射主键
     * @return 文件映射
     */
    @Override
    public FilePoint selectFilePointByPointId(Long pointId)
    {
        return filePointMapper.selectFilePointByPointId(pointId);
    }

    /**
     * 查询文件映射列表
     *
     * @param filePoint 文件映射
     * @return 文件映射
     */
    @Override
    public List<FilePoint> selectFilePointList(FilePoint filePoint)
    {
        return filePointMapper.selectFilePointList(filePoint);
    }

    /**
     * 新增文件映射
     *
     * @param filePoint 文件映射
     * @return 结果
     */
    @Override
    public int insertFilePoint(FilePoint filePoint)
    {
        filePoint.setScannStatus(FilePointScannStatusEnum.NOT.getDataValue());
        filePoint.setCreateTime(DateUtils.getNowDate());
        return filePointMapper.insertFilePoint(filePoint);
    }

    /**
     * 修改文件映射
     *
     * @param filePoint 文件映射
     * @return 结果
     */
    @Override
    public int updateFilePoint(FilePoint filePoint)
    {
        filePoint.setUpdateTime(DateUtils.getNowDate());
        return filePointMapper.updateFilePoint(filePoint);
    }

    /**
     * 批量删除文件映射
     *
     * @param pointIds 需要删除的文件映射主键
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteFilePointByPointIds(Long[] pointIds)
    {
        for(Long pointId : pointIds){
            FilePoint point = filePointMapper.selectFilePointByPointId(pointId);
            String systemPath = point.getSystemPath();
            LambdaQueryWrapper<FileInfo> fileInfoLambdaQueryWrapper = new QueryWrapper<FileInfo>().lambda();
            fileInfoLambdaQueryWrapper.eq(FileInfo::getFilePointId, pointId);
            fileInfoLambdaQueryWrapper.likeRight(FileInfo::getPath, systemPath);
            fileInfoMapper.delete(fileInfoLambdaQueryWrapper);
        }
        return filePointMapper.deleteFilePointByPointIds(pointIds);
    }

    /**
     * 删除文件映射信息
     *
     * @param pointId 文件映射主键
     * @return 结果
     */
    @Override
    public int deleteFilePointByPointId(Long pointId)
    {
        return filePointMapper.deleteFilePointByPointId(pointId);
    }

    /**
     * 全量扫描
     * @param piontId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void filepointScannAll(Long piontId) {
        FilePoint filePoint = filePointMapper.selectFilePointByPointId(piontId);
        if(filePoint == null){
            return;
        }
        if(filePoint.getScannStatus() == FilePointScannStatusEnum.ING.getDataValue()){
            throw new ServiceException("扫描中当前不允许扫描");
        }
        filePoint.setScannStatus(FilePointScannStatusEnum.ING.getDataValue());
        filePointMapper.updateFilePoint(filePoint);
        // 删除该路径数据[可能存在其他相同路径同时映射路径不一样的]
        String systemPath = filePoint.getSystemPath();
        LambdaQueryWrapper<FileInfo> fileInfoLambdaQueryWrapper = new QueryWrapper<FileInfo>().lambda();
        fileInfoLambdaQueryWrapper.eq(FileInfo::getFilePointId, piontId);
        fileInfoLambdaQueryWrapper.likeRight(FileInfo::getPath, systemPath);
        fileInfoMapper.delete(fileInfoLambdaQueryWrapper);
        FileAsyncManager.me().execute(FileAsyncFactory.scannFile(piontId, filePoint.getSystemPath(), filePoint.getPointPath(), 1, 0L));
    }

    /**
     * 增量扫描
     * @param piontId
     */
    @Override
    public void filepointScannUpdate(Long piontId) {
        FilePoint filePoint = filePointMapper.selectFilePointByPointId(piontId);
        if(filePoint == null){
            return;
        }
        if(filePoint.getScannStatus() == FilePointScannStatusEnum.ING.getDataValue()){
            throw new ServiceException("扫描中当前不允许扫描");
        }
        FileAsyncManager.me().execute(FileAsyncFactory.scannFile(piontId, filePoint.getSystemPath(), filePoint.getPointPath(), 1, 0L));
    }
}
