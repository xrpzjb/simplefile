package com.simplefile.file.service;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.simplefile.common.file.DiskFile;
import com.simplefile.file.domain.FileInfo;
import com.simplefile.file.dto.DiskFileListDto;
import com.simplefile.file.vo.DirCountFileSizeVo;
import com.simplefile.file.vo.DiskFileDataVo;

/**
 * 文件信息Service接口
 *
 * @author zhujiabao
 * @date 2024-08-26
 */
public interface IFileInfoService extends IService<FileInfo>
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
    public List<FileInfo> selectFileInfoList(FileInfo fileInfo);

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
     * 批量删除文件信息
     *
     * @param fileIds 需要删除的文件信息主键集合
     * @return 结果
     */
    public int deleteFileInfoByFileIds(Long[] fileIds);

    /**
     * 删除文件信息信息
     *
     * @param fileId 文件信息主键
     * @return 结果
     */
    public int deleteFileInfoByFileId(Long fileId);

    /**
     * 扫描目录文件
     * @param path
     */
    public void scannFile(Long pointId, String systemPath, String path, Integer type ,Long pid);

    /**
     * 获取目录文件
     * @param diskFileListRequest
     * @return
     */
    public DiskFileDataVo getDiskFileList(DiskFileListDto diskFileListRequest);

    /**
     * 回收站文件
     * @param diskFileListRequest
     * @return
     */
    public DiskFileDataVo getRecoveryDiskFileList(DiskFileListDto diskFileListRequest);

    /**
     * 文件移动
     * @param fileId
     */
    public void mv(Long fileId, Long dirId);

    /**
     * 文件复制
     * @param fileId
     */
    public void copy(Long fileId, Long dirId);

    /**
     * 文件删除,放入回收站
     * @param fileId
     */
    public void del(Long fileId);

    /**
     * 文件删除，回收站删除文件
     * @param fileId
     */
    public void delFile(Long fileId);

    /**
     * 文件删除还原
     * @param fileId
     */
    public void diskRestore(Long fileId);

    /**
     * 文件重命名
     * @param fileId
     * @param name
     */
    public void rename(Long fileId, String name);

    /**
     * 统计文件夹的大小
     * @param fileId
     * @return
     */
    public DirCountFileSizeVo countDirFileSize(Long fileId, Boolean hide, Boolean recycleBol);

    /**
     * 创建文件夹
     * @param dirId
     * @param name
     */
    public void createDir(Long dirId, String name);

    /**
     * 文件转成DiskFile
     * @param fileInfo
     * @return
     */
    public DiskFile fileToDiskFile(FileInfo fileInfo);

    /**
     * 获取回收站需要删除文件列表
     * @param recycleTime
     * @return
     */
    public List<FileInfo> getRecycleFileList(Date recycleTime);

    /**
     * 扫描指定的文件或目录
     */
    public void addScannFile(String systemPath);

    /**
     * 文件解压
     * @param fileId
     */
    public void unzipFile(Long fileId);
}
