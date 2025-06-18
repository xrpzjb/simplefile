package com.simplefile.file.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.simplefile.common.core.domain.AjaxResult;
import com.simplefile.common.exception.ServiceException;
import com.simplefile.common.file.DiskFile;
import com.simplefile.common.file.DiskFileDir;
import com.simplefile.common.file.DiskFileUtil;
import com.simplefile.common.utils.DateUtils;
import com.simplefile.common.utils.StringUtils;
import com.simplefile.file.domain.FilePoint;
import com.simplefile.file.dto.DiskFileListDto;
import com.simplefile.file.enums.*;
import com.simplefile.file.manager.FileAsyncManager;
import com.simplefile.file.manager.factory.FileAsyncFactory;
import com.simplefile.file.service.IFilePointService;
import com.simplefile.file.vo.DirCountFileSizeVo;
import com.simplefile.file.vo.DiskFileDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.simplefile.file.mapper.FileInfoMapper;
import com.simplefile.file.domain.FileInfo;
import com.simplefile.file.service.IFileInfoService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文件信息Service业务层处理
 *
 * @author zhujiabao
 * @date 2024-08-26
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements IFileInfoService
{
    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Autowired
    private IFilePointService filePointService;

    /**
     * 查询文件信息
     *
     * @param fileId 文件信息主键
     * @return 文件信息
     */
    @Override
    public FileInfo selectFileInfoByFileId(Long fileId)
    {
        return fileInfoMapper.selectFileInfoByFileId(fileId);
    }

    /**
     * 查询文件信息列表
     *
     * @param fileInfo 文件信息
     * @return 文件信息
     */
    @Override
    public List<FileInfo> selectFileInfoList(FileInfo fileInfo)
    {
        Page<FileInfo> page = new Page<FileInfo>(1, 10000);
        page.setSearchCount(false);
        return fileInfoMapper.selectFileInfoList(page, fileInfo);
    }

    /**
     * 新增文件信息
     *
     * @param fileInfo 文件信息
     * @return 结果
     */
    @Override
    public int insertFileInfo(FileInfo fileInfo)
    {
        fileInfo.setCreateTime(DateUtils.getNowDate());
        return fileInfoMapper.insertFileInfo(fileInfo);
    }

    /**
     * 修改文件信息
     *
     * @param fileInfo 文件信息
     * @return 结果
     */
    @Override
    public int updateFileInfo(FileInfo fileInfo)
    {
        fileInfo.setUpdateTime(DateUtils.getNowDate());
        return fileInfoMapper.updateFileInfo(fileInfo);
    }

    /**
     * 批量删除文件信息
     *
     * @param fileIds 需要删除的文件信息主键
     * @return 结果
     */
    @Override
    public int deleteFileInfoByFileIds(Long[] fileIds)
    {
        return fileInfoMapper.deleteFileInfoByFileIds(fileIds);
    }

    /**
     * 删除文件信息信息
     *
     * @param fileId 文件信息主键
     * @return 结果
     */
    @Override
    public int deleteFileInfoByFileId(Long fileId)
    {
        return fileInfoMapper.deleteFileInfoByFileId(fileId);
    }

    /**
     * 扫描文件目录
     * 情况1.删除同目录下一个文件，不通过系统系统操作放入了新的一个文件
     * 情况2.直接操作更换了目录结构
     * 情况3.分享情况下的目录修改的影响，复制不复制分享信息，剪贴移动操作剪贴分线信息
     *
     * 扫描类型：全目录扫描、当前目录扫描
     *
     * 更新规则
     * 1.当前目录全新的文件插入数据库，通过目录加名字判断是否存在
     * # 2.mysql数据存在，文件不存在，通过目录+文件名移除
     *
     * @param systemPath 系统目录
     * @param path 指向目录
     * @param type 扫描类型1全目录2当前目录
     * @param pid 父id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void scannFile(Long pointId, String systemPath, String path, Integer type, Long pid) {
        if(pid == 0 && !path.equals("/")){
            // 父级扫描
            List<DiskFileDir> diskFileDirList = DiskFileUtil.handleFilePath(path);
            for(DiskFileDir fileDir : diskFileDirList){
                // 当前目录
                LambdaQueryWrapper<FileInfo> fileInfoWapper = new QueryWrapper<FileInfo>()
                        .lambda()
                        .eq(FileInfo::getPointPath, fileDir.getFilePath())
                        .last("limit 1");
                LambdaQueryWrapper<FileInfo> parsentFileInfoWapper = new QueryWrapper<FileInfo>()
                        .lambda()
                        .eq(FileInfo::getPointPath, fileDir.getParsentPath())
                        .last("limit 1");
                Long parentId = 0L;
                if(StringUtils.isNotBlank(fileDir.getParsentPath())){
                    FileInfo parsentFileDir = getOne(parsentFileInfoWapper);
                    if(parsentFileDir == null){
                        throw new ServiceException("父级目录不存在，扫描异常");
                    }else{
                        parentId = parsentFileDir.getFileId();
                    }
                }

                FileInfo nowFileDir = getOne(fileInfoWapper);
                if(nowFileDir == null){
                    FileInfo newDirFileInfo = FileInfo.getNewDefault();
                    newDirFileInfo.setFileType(FileTypeEnum.DIR.getDataValue());
                    newDirFileInfo.setDirBol(true);
                    newDirFileInfo.setName(fileDir.getName());
                    if(fileDir.getFilePath().equals(path)){
                        newDirFileInfo.setPath(systemPath);
                        if(pid == 0){
                            pid = newDirFileInfo.getFileId();
                        }
                    }
                    newDirFileInfo.setFileUpdateTime(new Date());
                    newDirFileInfo.setParentId(parentId);
                    newDirFileInfo.setPointPath(fileDir.getFilePath());
                    newDirFileInfo.setHide(false);
                    newDirFileInfo.setFilePointId(pointId);
                    save(newDirFileInfo);
                }
            }
        }
        if(path.equals("/")){
            path = "";
        }
        // 1.获取文件
        List<DiskFile> diskFileList = DiskFileUtil.list(systemPath);
        // 2.目录文件处理
        Long finalPid = pid;
        String finalPath = path;
        diskFileList.stream().forEach(diskFile -> {
            String filePath = diskFile.getPath();
            LambdaQueryWrapper<FileInfo> fileInfoWapper = new QueryWrapper<FileInfo>()
                    .lambda()
                    .eq(FileInfo::getPath, filePath)
                    .last("limit 1");
            FileInfo one = getOne(fileInfoWapper);
            FileInfo newDirFileInfo = new FileInfo();
            newDirFileInfo.setFileSize(diskFile.getFileSize());
            newDirFileInfo.setFileType(diskFile.getType());
            newDirFileInfo.setDirBol(diskFile.isDir());
            newDirFileInfo.setName(diskFile.getName());
            newDirFileInfo.setPath(diskFile.getPath());
            newDirFileInfo.setDirBol(diskFile.isDir());
            if(one == null){
                // 没有该目录.新增
                newDirFileInfo.setFileId(IdWorker.getId());
                newDirFileInfo.setChildCount(0);
                newDirFileInfo.setParentId(finalPid);
                newDirFileInfo.setPointPath(finalPath + "/" + diskFile.getName());
                newDirFileInfo.setPreCode(FileInfo.getNewPreCode());
                newDirFileInfo.setShareEncryType(ShareEncryTypeEnum.SHARE_NOT_ENCRY.getDataValue());
                newDirFileInfo.setShareStatus(ShareStatusEnum.NOT_SHARE.getDataValue());
                newDirFileInfo.setStatus(FileStatusEnum.NORMAL.getDataValue());
                newDirFileInfo.setVisitNum(0);
                newDirFileInfo.setCreateTime(new Date());
                newDirFileInfo.setUpdateTime(new Date());
                newDirFileInfo.setDelFlag(DeletedEnum.NORMAL.getDataValue());
                newDirFileInfo.setFileUpdateTime(diskFile.getLastModifiedTime());
                newDirFileInfo.setHide(false);
                newDirFileInfo.setFilePointId(pointId);
                save(newDirFileInfo);
            }else{
                newDirFileInfo.setUpdateTime(new Date());
                newDirFileInfo.setFileId(one.getFileId());
                newDirFileInfo.setFilePointId(pointId);
                updateById(newDirFileInfo);
            }
            diskFile.setFileId(newDirFileInfo.getFileId());
        });

        // 3.递归获取文件
        if(type != null && type == FileInfo.FILE_SCAN_TYPE_ALL){
            for(DiskFile diskFile : diskFileList){
                if(diskFile.isDir()){
                    scannFile(pointId, systemPath + "/" + diskFile.getName(), path + "/" + diskFile.getName(), FileInfo.FILE_SCAN_TYPE_ALL, diskFile.getFileId());
                }
            }
        }
        if(pointId != null){
            FilePoint filePoint = filePointService.selectFilePointByPointId(pointId);
            if(filePoint != null){
                filePoint.setUpdateTime(new Date());
                filePoint.setUpdateBy("扫描任务");
                filePoint.setScannTime(new Date());
                filePoint.setScannStatus(FilePointScannStatusEnum.COMPLETE.getDataValue());
                filePointService.updateFilePoint(filePoint);
            }
        }
    }

    /**
     * 获取目录文件
     * 1.第一种情况无文件id，直接取根目录
     * 2.根目录先取数据库，数据库没有值，直接扫描根目录
     * 3.有文件id，直接取mysql
     * 4.刷新级别的取文件列表，异步扫描本目录下所有文件
     *
     * @param diskFileListRequest
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public DiskFileDataVo getDiskFileList(DiskFileListDto diskFileListRequest) {
        DiskFileDataVo diskFileDataVo = new DiskFileDataVo();
        List<DiskFile> diskFileList = new ArrayList<DiskFile>();
        List<FileInfo> fileInfoList = new ArrayList<>();
        // 获取文件
        if(StringUtils.isNotBlank(diskFileListRequest.getDirId())){
            // 文件id 当前目录不分页
            FileInfo fileInfo = new FileInfo();
            fileInfo.setParentId(Long.valueOf(diskFileListRequest.getDirId()));
            fileInfo.setHide(diskFileListRequest.getHide());
            fileInfo.setName(diskFileListRequest.getFileName());
            fileInfo.setRecycleLevel(diskFileListRequest.getRecycleLevel());
            if(StringUtils.isNotBlank(diskFileListRequest.getNowFileId())){
                fileInfo.setFileId(Long.valueOf(diskFileListRequest.getNowFileId()));
            }
            Page<FileInfo> fileInfoPage = new Page<FileInfo>(diskFileListRequest.getPageNum(), 10000);
            fileInfoPage.setSearchCount(false);
            fileInfo.setRecycleBol(diskFileListRequest.getRecycleBol());
            fileInfoList = fileInfoMapper.selectFileInfoList(fileInfoPage, fileInfo);
            if(diskFileListRequest.getRefresh() != null && diskFileListRequest.getRefresh() == true){
                FileAsyncManager.me().execute(
                        FileAsyncFactory.scannFile(null, diskFileListRequest.getFilePath(),
                                diskFileListRequest.getFilePath(),
                                FileInfo.FILE_SCAN_TYPE_ALL,
                                Long.valueOf(diskFileListRequest.getDirId())));
            }
        }else{
            // 进入该区域情况
            // 1.根目录进入，存在扫描和未扫描
            // 2.防止重复当前扫描
            // 3.添加映射，未扫描，不主动扫描
            LambdaQueryWrapper<FileInfo> fileInfoLambdaQueryWrapper = new QueryWrapper<FileInfo>().lambda();
            if(!diskFileListRequest.getRecycleBol()){
                fileInfoLambdaQueryWrapper.eq(FileInfo::getParentId, 0L);
            }
            fileInfoLambdaQueryWrapper.eq(FileInfo::getHide, diskFileListRequest.getHide());
            fileInfoLambdaQueryWrapper.eq(FileInfo::getRecycleBol, diskFileListRequest.getRecycleBol());
            if(StringUtils.isNotBlank(diskFileListRequest.getFileName())){
                fileInfoLambdaQueryWrapper.like(FileInfo::getName, diskFileListRequest.getFileName());
            }
            if(diskFileListRequest.getRecycleLevel() != null){
                fileInfoLambdaQueryWrapper.eq(FileInfo::getRecycleLevel, diskFileListRequest.getRecycleLevel());
            }
            if(StringUtils.isNotBlank(diskFileListRequest.getNowFileId())){
                fileInfoLambdaQueryWrapper.like(FileInfo::getFileId, diskFileListRequest.getNowFileId());
            }
            Page<FileInfo> fileInfoPage = new Page<FileInfo>(diskFileListRequest.getPageNum(), diskFileListRequest.getPageSize());
            Page<FileInfo> queryPage = fileInfoMapper.selectPage(fileInfoPage, fileInfoLambdaQueryWrapper);
            fileInfoList = queryPage.getRecords();
        }

//        HashMap<String, Integer> paramsMap = new HashMap<String, Integer>();
        fileInfoList.stream().forEach(f->{
            // 统计文件夹大小
            if(f.getDirBol()){
                DirCountFileSizeVo countFileSizeVo = countDirFileSize(f.getFileId(), null, diskFileListRequest.getRecycleBol());
                f.setFileSize(countFileSizeVo.getFileSize());
                f.setChildCount(countFileSizeVo.getFileCount());
            }
            // 对象转换
            diskFileList.add(fileToDiskFile(f));
//            String fileSuffix = DiskFileUtil.getFileSuffix(f.getName());
//            if(StringUtils.isNotBlank(fileSuffix)){
//                if(paramsMap.get(fileSuffix) == null){
//                    paramsMap.put(fileSuffix, 0);
//                }
//                paramsMap.get(fileSuffix).
//            }
        });

        if(diskFileListRequest.getDirId() != null){
            // 当前目录
            FileInfo nowFileInfo = fileInfoMapper.selectById(diskFileListRequest.getDirId());
            DiskFile nowDisk = fileToDiskFile(nowFileInfo);

            LinkedList<DiskFileDir> diskFileDirList = new LinkedList<DiskFileDir>();
            Long pid = Long.valueOf(diskFileListRequest.getDirId());
            while(pid != 0){
                LambdaQueryWrapper<FileInfo> fileInfoLambdaQueryWrapper = new QueryWrapper<FileInfo>().lambda();
                fileInfoLambdaQueryWrapper.eq(FileInfo::getFileId, pid);
                FileInfo fileInfo = fileInfoMapper.selectOne(fileInfoLambdaQueryWrapper);
                if(fileInfo != null){
                    DiskFileDir diskFileDir = new DiskFileDir();
                    diskFileDir.setId(fileInfo.getFileId().toString());
                    diskFileDir.setName(fileInfo.getName());
                    diskFileDir.setFilePath(fileInfo.getPath());
                    diskFileDirList.addFirst(diskFileDir);
                    pid = fileInfo.getParentId();
                }
            }
            DirCountFileSizeVo countFileSizeVo = countDirFileSize(nowFileInfo.getFileId(), null, diskFileListRequest.getRecycleBol());
            if(countFileSizeVo != null){
                nowDisk.setFileSize(countFileSizeVo.getFileSize());
                nowDisk.setFileSizeName(DiskFileUtil.getFileSizeName(nowDisk.getFileSize()));
            }
            diskFileDataVo.setNowDisk(nowDisk);
            diskFileDataVo.setDiskFileDirList(diskFileDirList);
        }else{
            if(StringUtils.isBlank(diskFileListRequest.getFileName())){
                // 根目录
                DiskFile nowDisk = new DiskFile();
                nowDisk.setFileId(0L);
                DirCountFileSizeVo countFileSizeVo = countDirFileSize(0L, null, diskFileListRequest.getRecycleBol());
                if(countFileSizeVo != null){
                    nowDisk.setFileSize(countFileSizeVo.getFileSize());
                    nowDisk.setFileSizeName(DiskFileUtil.getFileSizeName(nowDisk.getFileSize()));
                }
                diskFileDataVo.setNowDisk(nowDisk);
            }
        }
        diskFileDataVo.setDiskFileList(diskFileList);

        return diskFileDataVo;
    }

    /**
     * 回收站文件
     * @param diskFileListRequest
     * @return
     */
    @Override
    public DiskFileDataVo getRecoveryDiskFileList(DiskFileListDto diskFileListRequest) {
        DiskFileDataVo diskFileDataVo = new DiskFileDataVo();
        List<DiskFile> diskFileList = new ArrayList<DiskFile>();
        List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
        Page<FileInfo> fileInfoPage = new Page<FileInfo>(diskFileListRequest.getPageNum(), diskFileListRequest.getPageSize());
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(diskFileListRequest.getFileName());
        fileInfoList = fileInfoMapper.selectFileInfoList(fileInfoPage, fileInfo);
        fileInfoList.stream().forEach(f->{
            // 统计文件夹大小
            if(f.getDirBol()){
                DirCountFileSizeVo countFileSizeVo = countDirFileSize(f.getFileId(),null, diskFileListRequest.getRecycleBol());
                f.setFileSize(countFileSizeVo.getFileSize());
                f.setChildCount(countFileSizeVo.getFileCount());
            }
            // 对象转换
            diskFileList.add(fileToDiskFile(f));
        });
        diskFileDataVo.setDiskFileList(diskFileList);
        return null;
    }

    /**
     * 文件移动
     *
     * 异常处理
     * 先操作数据库，在进行文件操作，文件操作失败，抛出异常回滚
     *
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void mv(Long fileId, Long dirId) {
        if(fileId == null || dirId == null){
            throw new ServiceException("操作失败");
        }
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if(fileInfo == null){
            throw new ServiceException("原文件错误");
        }
        FileInfo dirFileInfo = null;
        if(dirId == 0){
            // 根目录
            FileInfo rootFile = getFileInfoRoot();
            if(rootFile == null){
                throw new ServiceException("未找到根目录实际路径");
            }
            dirFileInfo = rootFile;
        }else{
            dirFileInfo = fileInfoMapper.selectById(dirId);
        }
        if(dirFileInfo == null){
            throw new ServiceException("未找到目录实际路径");
        }

        String oldPath = fileInfo.getPath();

        // 判断是否存在文件
        LambdaQueryWrapper<FileInfo> repeatQueryWrapper = new LambdaQueryWrapper<FileInfo>()
                .eq(FileInfo::getPointPath, dirFileInfo.getPointPath() + "/" + fileInfo.getName());
        Long result = fileInfoMapper.selectCount(repeatQueryWrapper);
        if(result != 0){
            throw new ServiceException("已存在文件“" + fileInfo.getName() + "“");
        }

        // 数据移动
        fileInfo.setCreateTime(new Date());
        fileInfo.setParentId(dirFileInfo.getFileId());
        fileInfo.setPointPath(dirFileInfo.getPointPath() + "/" + fileInfo.getName());
        fileInfo.setPath(dirFileInfo.getPath() + "/" + fileInfo.getName());
        fileInfo.setFilePointId(dirFileInfo.getFilePointId());
        updateById(fileInfo);

        String newPath = fileInfo.getPath();

        // 文件移动
        boolean mv = DiskFileUtil.mv(oldPath, newPath);
        if(!mv){
            throw new ServiceException("文件移动失败");
        }
        // 如果是文件夹，需要异步手动扫描
        if(fileInfo.getDirBol()){
            FileAsyncManager.me().execute(FileAsyncFactory.scannFile(fileInfo.getFilePointId(), dirFileInfo.getPath(), dirFileInfo.getPointPath(), 1, dirFileInfo.getFileId()));
        }
    }

    /**
     * 文件复制
     * @param fileId
     * @param dirId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void copy(Long fileId, Long dirId) {
        if(fileId == null || dirId == null){
            throw new ServiceException("操作失败");
        }
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if(fileInfo == null){
            throw new ServiceException("原文件错误");
        }
        FileInfo dirFileInfo = null;
        if(dirId == 0){
            // 根目录
            FileInfo rootFile = getFileInfoRoot();
            if(rootFile == null){
                throw new ServiceException("未找到根目录实际路径");
            }
            dirFileInfo = rootFile;
        }else{
            dirFileInfo = fileInfoMapper.selectById(dirId);
        }
        if(dirFileInfo == null){
            throw new ServiceException("未找到目录实际路径");
        }
        // 判断是否存在文件
        LambdaQueryWrapper<FileInfo> repeatQueryWrapper = new LambdaQueryWrapper<FileInfo>()
                .eq(FileInfo::getPointPath, dirFileInfo.getPointPath() + "/" + fileInfo.getName());
        Long result = fileInfoMapper.selectCount(repeatQueryWrapper);
        if(result != 0){
            throw new ServiceException("已存在文件“" + fileInfo.getName() + "“");
        }

        String oldPath = fileInfo.getPath();

        // 数据复制
        fileInfo.setFileId(IdWorker.getId());
        fileInfo.setCreateTime(new Date());
        fileInfo.setParentId(dirFileInfo.getFileId());
        fileInfo.setPointPath(dirFileInfo.getPointPath() + "/" + fileInfo.getName());
        fileInfo.setPath(dirFileInfo.getPath() + "/" + fileInfo.getName());
        save(fileInfo);

        String newPath = fileInfo.getPath();

        // 文件复制
        boolean copy = DiskFileUtil.copy(oldPath, newPath);
        if(!copy){
            throw new ServiceException("文件复制失败");
        }
        // 如果是文件夹，需要异步手动扫描
        if(fileInfo.getDirBol()){
            FileAsyncManager.me().execute(FileAsyncFactory.scannFile(fileInfo.getFilePointId(), dirFileInfo.getPath(), dirFileInfo.getPointPath(), 1, dirFileInfo.getFileId()));
        }
    }

    /**
     * 文件删除
     *
     * 逻辑删除，放入回收站
     * 回收站过期，任务文件
     *
     * @param fileId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void del(Long fileId) {
        if(fileId == null){
            throw new ServiceException("操作失败");
        }
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if(fileInfo == null){
            throw new ServiceException("原文件错误");
        }
        fileInfo.setRecycleTime(new Date());
        fileInfo.setRecycleBol(true);
        deleteFileInfoByFileId(fileInfo.getFileId());

        if(fileInfo.getDirBol()){
            // 子目录文件删除
            String pointPath = fileInfo.getPointPath();
            LambdaQueryWrapper<FileInfo> fileInfoLambdaQueryWrapper = new QueryWrapper<FileInfo>().lambda();
            fileInfoLambdaQueryWrapper.likeRight(FileInfo::getPointPath, pointPath);
            fileInfoLambdaQueryWrapper.ne(FileInfo::getFileId, fileInfo.getFileId());
            FileInfo updateFileInfo = new FileInfo();
            updateFileInfo.setHide(true);
            updateFileInfo.setDelFlag(null);
            updateFileInfo.setRecycleTime(new Date());
            updateFileInfo.setRecycleBol(true);
            updateFileInfo.setRecycleLevel(FileRecycleLevelEnum.RECYCLE_NORMAL.getDataValue());
            update(updateFileInfo, fileInfoLambdaQueryWrapper);
        }
    }

    /**
     * 回收站删除文件
     * @param fileId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delFile(Long fileId) {
        if(fileId == null){
            throw new ServiceException("操作失败");
        }
        FileInfo fileInfo = fileInfoMapper.selectFileInfoByFileId(fileId);
        if(fileInfo == null){
            throw new ServiceException("删除文件错误");
        }
        // 删除数据
        fileInfoMapper.deleteWlFileInfoByFileIds(fileInfo.getFileId());
        if(fileInfo.getDirBol()){
            // 删除文件夹
            fileInfoMapper.deleteWlFileInfoByPointPath(fileInfo.getPointPath());
        }
        // 删除文件
        boolean del = DiskFileUtil.del(fileInfo.getPath());
        if(!del){
            throw new ServiceException("删除文件失败");
        }
    }

    /**
     * 文件删除还原
     * @param fileId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void diskRestore(Long fileId) {
        if(fileId == null){
            throw new ServiceException("操作失败");
        }
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if(fileInfo == null){
            throw new ServiceException("原文件错误");
        }
        fileInfo.setRecycleTime(new Date());
        fileInfo.setHide(false);
        fileInfo.setRecycleBol(false);
        fileInfo.setRecycleLevel(FileRecycleLevelEnum.RECYCLE_NORMAL.getDataValue());
        updateById(fileInfo);

        if(fileInfo.getDirBol()){
            // 子目录文件还原
            String pointPath = fileInfo.getPointPath();
            LambdaQueryWrapper<FileInfo> fileInfoLambdaQueryWrapper = new QueryWrapper<FileInfo>().lambda();
            fileInfoLambdaQueryWrapper.likeLeft(FileInfo::getPointPath, pointPath);
            fileInfoLambdaQueryWrapper.ne(FileInfo::getFileId, fileInfo.getFileId());
            FileInfo updateFileInfo = new FileInfo();
            updateFileInfo.setHide(false);
            updateFileInfo.setDelFlag(null);
            updateFileInfo.setRecycleTime(new Date());
            updateFileInfo.setRecycleBol(false);
            updateFileInfo.setRecycleLevel(FileRecycleLevelEnum.RECYCLE_NORMAL.getDataValue());
            update(updateFileInfo, fileInfoLambdaQueryWrapper);
        }
    }

    /**
     * 文件重命名
     * @param fileId 文件fileId
     * @param name 文件名
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void rename(Long fileId, String name) {
        if(fileId == null){
            throw new ServiceException("操作失败");
        }
        if(StringUtils.isBlank(name)){
            throw new ServiceException("文件名不能为空");
        }
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if(fileInfo == null){
            throw new ServiceException("文件错误");
        }
        String path = fileInfo.getPath();
        String dirPath = path.substring(0, path.lastIndexOf("/"));
        fileInfo.setPath(dirPath + "/" + name);
        fileInfo.setName(name);
        String point = fileInfo.getPointPath().substring(0, fileInfo.getPointPath().lastIndexOf("/"));
        fileInfo.setPointPath(point + "/" + name);
        updateById(fileInfo);
        if(fileInfo.getDirBol()){
            renameChild(fileInfo.getFileId());
        }
        if(!DiskFileUtil.rename(path, name)){
            throw new ServiceException("文件重命名失败");
        }
    }

    /**
     * 统计文件大小
     * @param fileId
     * @return
     */
    @Override
    public DirCountFileSizeVo countDirFileSize(Long fileId, Boolean hide, Boolean recycleBol) {
        DirCountFileSizeVo countFileSizeVo = new DirCountFileSizeVo();
        FileInfo fileInfo = fileInfoMapper.selectById(fileId);
        if(fileInfo == null && fileId != 0L){
            return countFileSizeVo;
        }
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<FileInfo>();
        if(fileId != 0L){
            queryWrapper.likeRight("point_path", fileInfo.getPointPath() + "/");
        }else{
            // 统计根目录
            queryWrapper.likeRight("point_path", "/");
        }
        queryWrapper.eq("dir_bol", false);
        queryWrapper.eq("recycle_bol", recycleBol);
        queryWrapper.select("sum(file_size) as fileSize,count(file_id) as fileCount");
        List<Map<String, Object>> result = fileInfoMapper.selectMaps(queryWrapper);
        Long fileSize = 0L;
        Integer fileCount = 0;
        if (result != null && !result.isEmpty()) {
            Map<String, Object> row = result.get(0);
            if (row.containsKey("fileSize")) {
                fileSize = Long.parseLong(row.get("fileSize").toString());
                fileCount = Integer.parseInt(row.get("fileCount").toString());
            }
        }
        countFileSizeVo.setFileSize(fileSize);
        countFileSizeVo.setFileCount(fileCount);
        return countFileSizeVo;
    }

    /**
     * 创建文件夹
     * @param dirId
     * @param name
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createDir(Long dirId, String name) {
        FileInfo fileInfo = null;
        if(dirId == null || dirId == 0L){
            // 寻找一个parentId = 0的文件
            fileInfo = getFileInfoRoot();
            if(fileInfo == null){
                throw new ServiceException("找不到根目录");
            }
        }else{
            fileInfo = fileInfoMapper.selectById(dirId);
            if(fileInfo == null){
                throw new ServiceException("文件错误");
            }
        }

        String path = fileInfo.getPath();
        String pointPath = fileInfo.getPointPath();

        FileInfo newDirFile = new FileInfo();
        newDirFile.setFileSize(0L);
        newDirFile.setFileId(IdWorker.getId());
        newDirFile.setFileType(FileTypeEnum.DIR.getDataValue());
        newDirFile.setDirBol(true);
        newDirFile.setChildCount(0);
        newDirFile.setParentId(fileInfo.getFileId());
        newDirFile.setFilePointId(fileInfo.getFilePointId());
        newDirFile.setCreateTime(new Date());
        newDirFile.setName(name);
        newDirFile.setStatus(FileStatusEnum.NORMAL.getDataValue());
        newDirFile.setPath(path + "/" + name);
        newDirFile.setPointPath(pointPath + "/" + name);
        newDirFile.setPreCode(FileInfo.getNewPreCode());
        newDirFile.setFileUpdateTime(new Date());
        newDirFile.setHide(false);
        newDirFile.setUpdateTime(new Date());

        if(!name.equals(".thumbnailImg")){
            save(newDirFile);
        }

        // 创建目录
        if(!DiskFileUtil.createDirectory(newDirFile.getPath())){
            throw new ServiceException("创建目录失败");
        }
    }

    public void renameChild(Long parsentId){
        if(parsentId == null){
            throw new ServiceException("操作失败");
        }
        FileInfo fileInfo = fileInfoMapper.selectById(parsentId);
        if(fileInfo == null){
            throw new ServiceException("文件错误");
        }
        FileInfo childInfo = new FileInfo();
        childInfo.setParentId(fileInfo.getFileId());
        List<FileInfo> fileInfoList = selectFileInfoList(childInfo);
        if(fileInfoList == null || fileInfoList.size() == 0){
            return;
        }
        for(FileInfo f : fileInfoList){
            f.setPath(fileInfo.getPath() + "/" + f.getName());
            f.setPointPath(fileInfo.getPointPath() + "/" + f.getName());
        }
        updateBatchById(fileInfoList);
        for(FileInfo f : fileInfoList){
            if(f.getDirBol()){
                renameChild(f.getFileId());
            }
        }
    }


    public DiskFile fileToDiskFile(FileInfo fileInfo){
        DiskFile diskFile = new DiskFile();
        diskFile.setId(fileInfo.getFileId().toString());
        diskFile.setFileId(fileInfo.getFileId());
        diskFile.setDir(fileInfo.getDirBol());
        diskFile.setType(fileInfo.getFileType());
        diskFile.setFileSize(fileInfo.getFileSize());
        if(org.apache.commons.lang3.StringUtils.isNotBlank(fileInfo.getPath())){
            fileInfo.setPath(fileInfo.getPath().replaceAll("\\\\", "/"));
        }
        if(org.apache.commons.lang3.StringUtils.isNotBlank(fileInfo.getPointPath())){
            fileInfo.setPointPath(fileInfo.getPointPath().replaceAll("\\\\", "/"));
        }
        diskFile.setPath(fileInfo.getPath());
        diskFile.setPointPath(fileInfo.getPointPath());
        String fileSizeName = DiskFileUtil.getFileSizeName(fileInfo.getFileSize());
        diskFile.setFileSizeName(fileSizeName);
        diskFile.setName(fileInfo.getName());
        diskFile.setLastModifiedTime(fileInfo.getFileUpdateTime());
        String handleType = DiskFileUtil.diskFileUnifiedMap.get(diskFile.getType());
        diskFile.setHandleFileType(handleType);
        return diskFile;
    }

    /**
     * 获取回收站需要删除的文件
     * @param recycleTime
     * @return
     */
    @Override
    public List<FileInfo> getRecycleFileList(Date recycleTime) {
        return baseMapper.getRecycleFileList(recycleTime);
    }


    /**
     *
     * 指定扫描目录或文件，不深入扫描
     *
     * @param systemPath 父级系统目录
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addScannFile(String systemPath) {
        systemPath = systemPath.replaceAll("\\\\", "/");
        int splitEndIndex = systemPath.lastIndexOf("/");
        if(splitEndIndex == -1){
            throw new ServiceException("文件地址错误");
        }
        String parsentPath = systemPath.substring(0, splitEndIndex);
        LambdaQueryWrapper<FileInfo> parentPathWrapper = new LambdaQueryWrapper<FileInfo>()
                .eq(FileInfo::getPath, parsentPath);
        FileInfo parsentFile = fileInfoMapper.selectOne(parentPathWrapper);
        if(parsentFile == null){
            // 父目录找不到
            // 试试顶级目录
            LambdaQueryWrapper<FilePoint> pointLambdaQueryWrapper = new LambdaQueryWrapper<FilePoint>()
                    .eq(FilePoint::getSystemPath, parsentPath);
            FilePoint filePoint = filePointService.getOne(pointLambdaQueryWrapper);
            if(filePoint == null){
                throw new ServiceException("父目录找不到");
            }else{
                parsentFile = new FileInfo();
                parsentFile.setFilePointId(filePoint.getPointId());
                parsentFile.setFileId(0L);
                parsentFile.setPointPath(filePoint.getPointPath());
            }
        }
        File file = new File(systemPath);
        Integer fileType = DiskFileUtil.getFileType(file.getName());

        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId(IdWorker.getId());
        fileInfo.setFileType(fileType);
        fileInfo.setPreCode(FileInfo.getNewPreCode());
        fileInfo.setHide(false);
        fileInfo.setFilePointId(parsentFile.getFilePointId());
        fileInfo.setParentId(parsentFile.getFileId());
        fileInfo.setName(file.getName());
        fileInfo.setPointPath(parsentFile.getPointPath() + "/" + file.getName());
        fileInfo.setPath(systemPath);
        fileInfo.setDirBol(file.isDirectory());

        if(!fileInfo.getDirBol()){
            fileInfo.setFileSize(file.length());
        }
        fileInfo.setChildCount(0);
        fileInfo.setStatus(FileStatusEnum.NORMAL.getDataValue());
        fileInfo.setShareEncryType(ShareEncryTypeEnum.SHARE_NOT_ENCRY.getDataValue());
        fileInfo.setShareStatus(ShareStatusEnum.NOT_SHARE.getDataValue());
        fileInfo.setVisitNum(0);
        fileInfo.setThumbnail(false);
        fileInfo.setFileUpdateTime(new Date());
        fileInfo.setRecycleBol(false);
        fileInfo.setCreateTime(new Date());
        fileInfo.setUpdateTime(new Date());
        insertFileInfo(fileInfo);
    }

    /**
     * 文件解压
     * @param fileId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unzipFile(Long fileId) {
        FileInfo fileInfo = selectFileInfoByFileId(fileId);
        if(fileInfo == null){
            throw new ServiceException("");
        }
        if(fileInfo.getFileType() != FileTypeEnum.ZIP.getDataValue()){
            throw new ServiceException("不是压缩包无法解压");
        }
        String path = fileInfo.getPath().substring(0, fileInfo.getPath().lastIndexOf("/"));
        String name = fileInfo.getName().substring(0, fileInfo.getName().lastIndexOf("."));
        try {
            unzip(fileInfo.getPath(), path);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("解压失败");
        }
    }

    /**
     * 获取根目录
     * @return
     */
    @Override
    public FileInfo getFileInfoRoot() {
        LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<FileInfo>()
                .eq(FileInfo::getParentId, 0).last("limit 1");
        FileInfo fileInfo = fileInfoMapper.selectOne(queryWrapper);
        if(fileInfo != null){
            return fileInfo;
        }
        LambdaQueryWrapper<FilePoint> filePointLambdaQueryWrapper = new LambdaQueryWrapper<FilePoint>()
                .eq(FilePoint::getPointPath, "/").last("limit 1");
        FilePoint filePoint = filePointService.getOne(filePointLambdaQueryWrapper);
        if(filePoint != null){
            fileInfo = new FileInfo();
            fileInfo.setFileId(0L);
            fileInfo.setPath(filePoint.getSystemPath());
            fileInfo.setPointPath(filePoint.getPointPath());
            fileInfo.setFilePointId(filePoint.getPointId());
            return fileInfo;
        }
        return null;
    }

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
            addScannFile(destDirectory);
        }

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry!= null) {
                String filePath = destDirectory + "/" + entry.getName();
                if (!entry.isDirectory()) {
                    // 如果是文件，提取文件
                    extractFile(zipIn, filePath, StandardCharsets.UTF_8);
                    addScannFile(filePath);
                } else {
                    // 如果是目录，创建目录
                    if(filePath.lastIndexOf("/") == filePath.length() - 1){
                        filePath = filePath.substring(0, filePath.lastIndexOf("/"));
                    }
                    File dir = new File(filePath);
                    dir.mkdir();
                    addScannFile(filePath);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void extractFile(ZipInputStream zipIn, String filePath, Charset charset) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = zipIn.read(buffer)) > 0) {
                String str = new String(buffer, 0, len, charset); // 按照指定编码转换为字符串
                fos.write(str.getBytes(charset)); // 再将字符串按指定编码转换为字节数组
            }
        }
    }
}
