package com.simplefile.common.file;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.simplefile.common.exception.ServiceException;
import com.simplefile.common.utils.sign.Md5Utils;
import com.simplefile.common.utils.uuid.IdUtils;
import com.simplefile.file.enums.FileTypeEnum;
import com.simplefile.file.enums.FileUnifiedTypeEnum;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 磁盘文件操作
 * @author zhujiabao
 *
 */
public class DiskFileUtil {

    // 文件的type
    public static HashMap<String, Integer> diskFileTypeMap = new HashMap<String, Integer>();

    // 文件的内容类型
    public static HashMap<String, String> diskFileContentTypeMap = new HashMap<String, String>();

    // 文件统一解析标准类型
    public static HashMap<Integer, String> diskFileUnifiedMap = new HashMap<Integer, String>();

    static {
        diskFileTypeMap.put("txt", FileTypeEnum.TEXT.getDataValue());
        diskFileTypeMap.put("xml", FileTypeEnum.TEXT.getDataValue());
        diskFileTypeMap.put("yml", FileTypeEnum.TEXT.getDataValue());
        diskFileTypeMap.put("ini", FileTypeEnum.TEXT.getDataValue());

        diskFileTypeMap.put("jpg", FileTypeEnum.PIC.getDataValue());
        diskFileTypeMap.put("jpeg", FileTypeEnum.PIC.getDataValue());
        diskFileTypeMap.put("png", FileTypeEnum.PIC.getDataValue());
        diskFileTypeMap.put("gif", FileTypeEnum.PIC.getDataValue());

        diskFileTypeMap.put("doc", FileTypeEnum.DOC.getDataValue());
        diskFileTypeMap.put("docx", FileTypeEnum.DOC.getDataValue());

        diskFileTypeMap.put("xls", FileTypeEnum.EXCEL.getDataValue());
        diskFileTypeMap.put("xlsx", FileTypeEnum.EXCEL.getDataValue());

        diskFileTypeMap.put("pdf", FileTypeEnum.PDF.getDataValue());

        diskFileTypeMap.put("zip", FileTypeEnum.ZIP.getDataValue());
        diskFileTypeMap.put("rar", FileTypeEnum.ZIP.getDataValue());
        diskFileTypeMap.put("7z", FileTypeEnum.ZIP.getDataValue());

        diskFileTypeMap.put("exe", FileTypeEnum.EXE.getDataValue());

        diskFileTypeMap.put("ppt", FileTypeEnum.PPT.getDataValue());
        diskFileTypeMap.put("pptx", FileTypeEnum.EXE.getDataValue());

        diskFileTypeMap.put("bat", FileTypeEnum.BAT.getDataValue());
        diskFileTypeMap.put("jar", FileTypeEnum.JAR.getDataValue());
        diskFileTypeMap.put("html", FileTypeEnum.HTML.getDataValue());
        diskFileTypeMap.put("htm", FileTypeEnum.HTML.getDataValue());
        diskFileTypeMap.put("sql", FileTypeEnum.SQL.getDataValue());
        diskFileTypeMap.put("conf", FileTypeEnum.CONF.getDataValue());

        diskFileTypeMap.put("mp4", FileTypeEnum.VIDEO.getDataValue());
        diskFileTypeMap.put("avi", FileTypeEnum.VIDEO.getDataValue());
        diskFileTypeMap.put("flv", FileTypeEnum.VIDEO.getDataValue());
        diskFileTypeMap.put("mov", FileTypeEnum.VIDEO.getDataValue());
        diskFileTypeMap.put("wmv", FileTypeEnum.VIDEO.getDataValue());
        diskFileTypeMap.put("rmvb", FileTypeEnum.VIDEO.getDataValue());
        diskFileTypeMap.put("rm", FileTypeEnum.VIDEO.getDataValue());

        diskFileTypeMap.put("mp3", FileTypeEnum.AUDIO.getDataValue());
        diskFileTypeMap.put("aac", FileTypeEnum.AUDIO.getDataValue());
        diskFileTypeMap.put("flac", FileTypeEnum.AUDIO.getDataValue());
        diskFileTypeMap.put("wav", FileTypeEnum.AUDIO.getDataValue());
        diskFileTypeMap.put("aiff", FileTypeEnum.AUDIO.getDataValue());

        // 文件内容类型
        diskFileContentTypeMap.put("txt", "text/plain");
        diskFileContentTypeMap.put("xml", "text/plain");
        diskFileContentTypeMap.put("yml", "text/plain");
        diskFileContentTypeMap.put("ini", "text/plain");

        diskFileContentTypeMap.put("jpg", "image/jpeg");
        diskFileContentTypeMap.put("jpeg", "image/jpeg");
        diskFileContentTypeMap.put("png", "image/png");
        diskFileContentTypeMap.put("gif", "image/gif");

        diskFileContentTypeMap.put("doc", "application/vnd.ms-word");
        diskFileContentTypeMap.put("docx", "application/vnd.ms-word");

        diskFileContentTypeMap.put("xls", "application/vnd.ms-excel");
        diskFileContentTypeMap.put("xlsx", "application/vnd.ms-excel");

        diskFileContentTypeMap.put("pdf", "application/pdf");

        diskFileContentTypeMap.put("zip", "application/zip");
        diskFileContentTypeMap.put("rar", "");
        diskFileContentTypeMap.put("7z", "");

        diskFileContentTypeMap.put("exe", "application/octet-stream");

        diskFileContentTypeMap.put("ppt", "application/vnd.ms-powerpoint");
        diskFileContentTypeMap.put("pptx", "application/vnd.ms-powerpoint");

        diskFileContentTypeMap.put("bat", "text/plain");
        diskFileContentTypeMap.put("jar", "");
        diskFileContentTypeMap.put("html", "text/html");
        diskFileContentTypeMap.put("htm", "text/html");
        diskFileContentTypeMap.put("sql", "text/plain");
        diskFileContentTypeMap.put("conf", "text/plain");

        diskFileContentTypeMap.put("mp4", "video/mp4");
        diskFileContentTypeMap.put("avi", "video/x-msvideo");
        diskFileContentTypeMap.put("flv", "");
        diskFileContentTypeMap.put("mov", "");
        diskFileContentTypeMap.put("wmv", "");
        diskFileContentTypeMap.put("rmvb", "");
        diskFileContentTypeMap.put("rm", "");

        diskFileContentTypeMap.put("mp3", "audio/mpeg");
        diskFileContentTypeMap.put("aac", "");
        diskFileContentTypeMap.put("flac", "");
        diskFileContentTypeMap.put("wav", "audio/wav");
        diskFileContentTypeMap.put("aiff", "");

        // 解析类型
        // 文本
        diskFileUnifiedMap.put(FileTypeEnum.TEXT.getDataValue(), FileUnifiedTypeEnum.TXT.getDataValue());
        diskFileUnifiedMap.put(FileTypeEnum.SQL.getDataValue(), FileUnifiedTypeEnum.TXT.getDataValue());
        diskFileUnifiedMap.put(FileTypeEnum.HTML.getDataValue(), FileUnifiedTypeEnum.TXT.getDataValue());
        diskFileUnifiedMap.put(FileTypeEnum.BAT.getDataValue(), FileUnifiedTypeEnum.TXT.getDataValue());
        diskFileUnifiedMap.put(FileTypeEnum.CONF.getDataValue(), FileUnifiedTypeEnum.TXT.getDataValue());
        diskFileUnifiedMap.put(FileTypeEnum.INI.getDataValue(), FileUnifiedTypeEnum.TXT.getDataValue());
        // 图片
        diskFileUnifiedMap.put(FileTypeEnum.PIC.getDataValue(), FileUnifiedTypeEnum.PIC.getDataValue());
        // 音频
        diskFileUnifiedMap.put(FileTypeEnum.AUDIO.getDataValue(), FileUnifiedTypeEnum.AUDIO.getDataValue());
        // 视频
        diskFileUnifiedMap.put(FileTypeEnum.VIDEO.getDataValue(), FileUnifiedTypeEnum.VIDEO.getDataValue());
        // doc
        diskFileUnifiedMap.put(FileTypeEnum.DOC.getDataValue(), FileUnifiedTypeEnum.DOC.getDataValue());
        // excel
        diskFileUnifiedMap.put(FileTypeEnum.EXCEL.getDataValue(), FileUnifiedTypeEnum.EXCEL.getDataValue());
        // pdf
        diskFileUnifiedMap.put(FileTypeEnum.PDF.getDataValue(), FileUnifiedTypeEnum.PDF.getDataValue());
        // ppt
        diskFileUnifiedMap.put(FileTypeEnum.PPT.getDataValue(), FileUnifiedTypeEnum.PPT.getDataValue());
        // 压缩包
        diskFileUnifiedMap.put(FileTypeEnum.ZIP.getDataValue(), FileUnifiedTypeEnum.ZIP.getDataValue());

    }

    /**
     * 递归读取目录以及下级目录
     * @param dirPath 目录
     * @return
     */
    public static List<DiskFile> xlist(String dirPath){
        List<DiskFile> list = list(dirPath);
        list.stream().forEach(f->{
            if(!f.isDir()){
                return;
            }
            List<DiskFile> xlist = xlist(f.getPath());
            f.setChildFileList(xlist);
        });
        return list;
    }

    /**
     * 获取当前目录下的文件列表
     * 1.不获取下级目录
     * @param dirPath 当前目录
     * @return
     */
    public static List<DiskFile> list(String dirPath){
        if(StringUtils.isBlank(dirPath)){
            return Collections.emptyList();
        }
        File currentDir = new File(dirPath);
        // 获取当前目录下的文件和文件夹列表
        File[] files = currentDir.listFiles();
        List<DiskFile> diskFileList = new ArrayList<DiskFile>();
        if(files != null && files.length > 0){
            // 遍历文件列表
            for(File file : files){
                // 获取文件信息
                DiskFile diskFile = new DiskFile();
                diskFile.setId(IdUtils.randomUUID());
                diskFile.setName(file.getName());
                diskFile.setPath(file.getPath().replaceAll("\\\\", "/"));
                diskFile.setFileSize(file.length());
                diskFile.setDir(file.isDirectory());
                Integer type = DiskFileUtil.getFileType(file.getName());
                // 获取文件的最后修改时间
                long lastModifiedTime = file.lastModified();
                Date lastModifiedDate = new Date(lastModifiedTime);
                diskFile.setLastModifiedTime(lastModifiedDate);
                diskFile.setType(type);
                if(diskFile.isDir()){
                    diskFile.setType(FileTypeEnum.DIR.getDataValue());
                }else{
                    diskFile.setFileSizeName(DiskFileUtil.getFileSizeName(diskFile.getFileSize()));
                }
                if(diskFile.getPath().contains(".thumbnailImg")){
                    continue;
                }
                // 添加到结果列表
                diskFileList.add(diskFile);
            }
        }
        return diskFileList;
    }

    /**
     * 获取文件类型
     * @param fileName 文件名称
     * @return
     */
    public static Integer getFileType(String fileName){
        int index = fileName.lastIndexOf(".");
        if(index == -1){
            return FileTypeEnum.OTHER.getDataValue();
        }
        String substring = fileName.substring(index + 1, fileName.length());
        Integer type = diskFileTypeMap.get(substring);
        if(type == null){
            type = FileTypeEnum.OTHER.getDataValue();
        }
        return type;
    }

    /**
     * 获取文件后缀
     * @return
     */
    public static String getFileSuffix(String fileName){
        int index = fileName.lastIndexOf(".");
        if(index == -1){
            return "";
        }
        String substring = fileName.substring(index + 1, fileName.length());
        return substring;
    }

    /**
     * 获取文件大小名称
     * @param fileSize
     * @return
     */
    public static String getFileSizeName(Long fileSize){
        if(fileSize == null || fileSize == 0){
            return "";
        }
        String s = FileUtils.byteCountToDisplaySize(fileSize);
        return s;
    }

    /**
     *  解析目录成list
     * @param path
     */
    public static List<DiskFileDir> handleFilePath(String path){
        String[] split = path.split("\\\\");
        if(split.length == 1 && path.split("/").length > 1){
            split = path.split("/");
        }
        List<DiskFileDir> diskFileDirList = new ArrayList<DiskFileDir>();
        Map<String, Long> parsentMap = new HashMap<String, Long>();
        for(int i = 0;i<split.length;i++){
            DiskFileDir fileDir = new DiskFileDir();
            fileDir.setName(split[i]);
            String filePath = "";
            for(int j = 0;j<i+1;j++){
                if(j == 0){
                    filePath = split[j];
                }else{
                    filePath = filePath + "/" + split[j];
                }
            }
            fileDir.setFilePath(filePath);
            parsentMap.put(filePath, IdWorker.getId());
            if(i > 0){
                String parsentPath = "";
                for(int j = 0;j<i;j++){
                    if(j == 0){
                        parsentPath = split[j];
                    }else{
                        parsentPath = parsentPath + "/" + split[j];
                    }
                }
                fileDir.setParsentPath(parsentPath);
                fileDir.setParsentName(split[i-1]);
            }
            if(StringUtils.isBlank(fileDir.getName())){
                continue;
            }
            diskFileDirList.add(fileDir);
        }
        return diskFileDirList;
    }

    /**
     * 文件移动
     * @param oldFilePath
     * @param newFilePath
     */
    public static boolean mv(String oldFilePath, String newFilePath){
        // 源文件路径
        Path sourcePath = Paths.get(oldFilePath);

        // 目标文件路径
        Path targetPath = Paths.get(newFilePath);

        try {
            // 移动文件
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            System.out.println("文件移动失败: " + e.getMessage());
        }
        return false;
    }

    /**
     * 文件复制
     * @param oldFilePath
     * @param newFilePath
     * @return
     */
    public static boolean copy(String oldFilePath, String newFilePath){
        Path sourcePath = Paths.get(oldFilePath);
        Path destinationPath = Paths.get(newFilePath);
        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
        }
        return false;
    }

    /**
     * 删除文件包含文件夹下的文件
     * @param oldFilePath
     * @return
     */
    public static boolean del(String oldFilePath){
        File folder = new File(oldFilePath);
        try {
            FileUtils.forceDelete(folder);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建文件夹
     * @param path
     * @return
     */
    public static boolean createDirectory(String path){
        File directory = new File(path);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new ServiceException("文件夹已存在");
        }
    }

    public static boolean rename(String systemPath, String newFileName){
        // 创建File对象指向旧文件
        File oldFile = new File(systemPath);
        // 创建File对象指向新文件名
        String dirPath = systemPath.substring(0, systemPath.lastIndexOf("/"));
        File newFile = new File(dirPath + "/" + newFileName);
        // 使用renameTo方法进行重命名
        boolean renamed = oldFile.renameTo(newFile);
        // 输出结果
        if (renamed) {
            System.out.println("文件重命名成功！");
            return true;
        } else {
            System.out.println("文件重命名失败，确保文件未被打开且路径正确。");
            return false;
        }
    }

    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir(); // 如果目标目录不存在，创建它
        }

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry!= null) {
                String filePath = destDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // 如果是文件，提取文件
                    extractFile(zipIn, filePath, StandardCharsets.UTF_8); // 明确指定
                    System.out.println("文 件：" + filePath);// 编码
                } else {
                    // 如果是目录，创建目录
                    File dir = new File(filePath);
                    dir.mkdir();
                    System.out.println("文件夹：" + filePath);
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

    /**
     * 获取图片缩略图的路径
     * @return
     */
    public static String getThumbnailPath(String picPath){
        Random random = new Random();
        int randomDigit = random.nextInt(20);
        int randomTwoDigit = random.nextInt(30);
        return randomDigit + "/" + randomTwoDigit;
    }

    /**
     * 获取图片缩略图的路径
     * @return
     */
    public static String getThumbnailPathMd5(String picPath){
        int i = picPath.lastIndexOf("/");
        if(i == 0){
            return Md5Utils.hash(picPath);
        }
        return picPath.substring(1, i) + "/"
                + Md5Utils.hash(picPath);
    }


    /**
     * 解析获取压缩包的文件列表
     * @param zipFilePath
     * @return
     */
    public static List<DiskFile> getZipFileList(String zipFilePath){
        List<DiskFile> fileList = new ArrayList<DiskFile>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                DiskFile file = new DiskFile();
                file.setName(entry.getName());
                file.setFileSize(entry.getSize());
                fileList.add(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileList;
    }

    public static DiskTreeNode buildTree(String zipFilePath) {
        DiskTreeNode root = new DiskTreeNode("根目录", true);
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                String[] parts = entry.getName().split("/");
                DiskTreeNode current = root;
                for (int i = 0; i < parts.length; i++) {
                    boolean isDir = entry.isDirectory() || (i < parts.length - 1);
                    DiskTreeNode child = findChild(current, parts[i]);
                    if (child == null) {
                        child = new DiskTreeNode(parts[i], isDir);
                        current.children.add(child);
                    }
                    current = child;
                }
                zipIn.closeEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }

    private static DiskTreeNode findChild(DiskTreeNode parent, String name) {
        for (DiskTreeNode child : parent.children) {
            if (child.label.equals(name)) {
                return child;
            }
        }
        return null;
    }

    public static void main(String[] args) {
//        String dirPath = "C:/Users/Administrator/AppData/Roaming/npm/node_modules/yarn/bin";
//        handleFilePath(dirPath);
//        List<DiskFile> diskFileList = DiskFileUtil.xlist(dirPath);
//        System.out.println(JSON.toJSONString(diskFileList));
        // rename("C:/Users/流年/Downloads/测试.png", "测试2.png");
//        String zipFilePath = "C:\\Users\\nmwork\\Desktop\\ruoyi1212121.zip";
//        try{
//            List<DiskFile> zipFileList = getZipFileList(zipFilePath);
//            System.out.println(JSON.toJSONString(zipFileList));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        String zipFilePath = "C:\\Users\\nmwork\\Downloads\\zfile-main.zip";
        try {
            unzip(zipFilePath, "C:\\Users\\nmwork\\Downloads\\zfile-main");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
