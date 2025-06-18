package com.simplefile.wedav.service;

import com.simplefile.wedav.domain.SysWebdavFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * WebDAV文件Service接口
 *
 * @author doubao
 */
public interface ISysWebdavFileService
{
    /**
     * 查询文件列表
     *
     * @param file 文件信息
     * @return 文件集合
     */
    public List<SysWebdavFile> selectFileList(SysWebdavFile file);

    /**
     * 检查路径是否存在
     *
     * @param filePath 文件路径
     * @return 存在返回true，否则返回false
     */
    public boolean checkPathExists(String filePath);

    /**
     * 根据路径选择文件
     *
     * @param filePath 文件路径
     * @return 文件对象
     */
    public SysWebdavFile selectFileByPath(String filePath);

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @param response 响应对象
     */
    public void downloadFile(String filePath, HttpServletResponse response) throws IOException;

    /**
     * 创建文件
     *
     * @param file 文件信息
     * @return 结果
     */
    public int createFile(SysWebdavFile file);

    /**
     * 创建目录
     *
     * @param path 目录路径
     * @param parentId 父目录ID
     * @return 结果
     */
    public int createDirectory(String path, Long parentId);

    /**
     * 更新文件内容
     *
     * @param fileId 文件ID
     * @param content 文件内容
     * @return 结果
     */
    public int updateFileContent(Long fileId, byte[] content);

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 结果
     */
    public int deleteFile(String filePath);

    /**
     * 移动文件
     *
     * @param sourcePath 源路径
     * @param targetPath 目标路径
     * @return 结果
     */
    public int moveFile(String sourcePath, String targetPath);

    /**
     * 复制文件
     *
     * @param sourcePath 源路径
     * @param targetPath 目标路径
     * @return 结果
     */
    public int copyFile(String sourcePath, String targetPath);
}
