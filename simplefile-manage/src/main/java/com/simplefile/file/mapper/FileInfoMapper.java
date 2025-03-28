package com.simplefile.file.mapper;

import java.io.File;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplefile.file.domain.FileInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 文件信息Mapper接口
 *
 * @author zhujiabao
 * @date 2024-08-26
 */
public interface FileInfoMapper extends BaseMapper<FileInfo>
{
    /**
     * 查询文件信息
     *
     * @param fileId 文件信息主键
     * @return 文件信息
     */
    public FileInfo selectFileInfoByFileId(Long fileId);

    /**
     * 查询文件信息列表
     *
     * @param fileInfo 文件信息
     * @return 文件信息集合
     */
    public List<FileInfo> selectFileInfoList(Page<FileInfo> page, @Param("fileInfo") FileInfo fileInfo);

    /**
     * 查询回收站文件列表
     * @param page
     * @param fileInfo
     * @return
     */
    public List<FileInfo> selectRecoveryFileInfoList(Page<FileInfo> page, @Param("fileInfo") FileInfo fileInfo);

    /**
     * 新增文件信息
     *
     * @param fileInfo 文件信息
     * @return 结果
     */
    public int insertFileInfo(FileInfo fileInfo);

    /**
     * 修改文件信息
     *
     * @param fileInfo 文件信息
     * @return 结果
     */
    public int updateFileInfo(FileInfo fileInfo);

    /**
     * 删除文件信息
     *
     * @param fileId 文件信息主键
     * @return 结果
     */
    public int deleteFileInfoByFileId(Long fileId);

    /**
     * 物理删除
     * @param fileId
     * @return
     */
    public int deleteWlFileInfoByFileIds(Long fileId);

    /**
     * 批量删除文件信息
     *
     * @param fileIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFileInfoByFileIds(Long[] fileIds);

    /**
     * 映射路径删除
     * @param pointPath
     * @return
     */
    int deleteWlFileInfoByPointPath(@Param("path") String pointPath);

    /**
     * 获取需要删除回收站的文件列表
     * @param recycleTime
     * @return
     */
    public List<FileInfo> getRecycleFileList(Date recycleTime);
}
