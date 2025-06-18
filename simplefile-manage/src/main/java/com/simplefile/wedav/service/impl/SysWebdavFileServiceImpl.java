package com.simplefile.wedav.service.impl;

import com.simplefile.wedav.domain.SysWebdavFile;
import com.simplefile.wedav.service.ISysWebdavFileService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class SysWebdavFileServiceImpl implements ISysWebdavFileService {
    @Override
    public List<SysWebdavFile> selectFileList(SysWebdavFile file) {
        return Collections.emptyList();
    }

    @Override
    public boolean checkPathExists(String filePath) {
        return false;
    }

    @Override
    public SysWebdavFile selectFileByPath(String filePath) {
        return null;
    }

    @Override
    public void downloadFile(String filePath, HttpServletResponse response) throws IOException {

    }

    @Override
    public int createFile(SysWebdavFile file) {
        return 0;
    }

    @Override
    public int createDirectory(String path, Long parentId) {
        return 0;
    }

    @Override
    public int updateFileContent(Long fileId, byte[] content) {
        return 0;
    }

    @Override
    public int deleteFile(String filePath) {
        return 0;
    }

    @Override
    public int moveFile(String sourcePath, String targetPath) {
        return 0;
    }

    @Override
    public int copyFile(String sourcePath, String targetPath) {
        return 0;
    }
}
