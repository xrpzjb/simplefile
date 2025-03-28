package com.simplefile.quartz.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.simplefile.common.file.DiskFileUtil;
import com.simplefile.common.utils.sign.Md5Utils;
import com.simplefile.file.domain.FileInfo;
import com.simplefile.file.domain.FilePoint;
import com.simplefile.file.enums.FileTypeEnum;
import com.simplefile.file.service.IFileInfoService;
import com.simplefile.file.service.IFilePointService;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 缩略图任务
 * 1.文件类型为图片的，将生成缩略图
 * 2.缩略图为两种：小、中、大
 *
 * 缩略图存储
 *
 *
 */
@Component("thumbnailTask")
@Service
public class ThumbnailTask {

    @Autowired
    private IFileInfoService fileInfoService;

    @Autowired
    private IFilePointService filePointService;

    public void run() {
        // 寻找根目录
        LambdaQueryWrapper<FilePoint> dirQueryWrapper = new QueryWrapper<FilePoint>().lambda();
        dirQueryWrapper.eq(FilePoint::getPointPath, "/");
        dirQueryWrapper.last("limit 1");
        FilePoint filePoint = filePointService.getOne(dirQueryWrapper);
        if(filePoint == null){
            // ERROR:未找到根目录，无法在根目录获取到缩略图存储位置
            return;
        }
        // 查出所有图片文件
        LambdaQueryWrapper<FileInfo> lambdaQueryWrapper = new QueryWrapper<FileInfo>().lambda();
        lambdaQueryWrapper.eq(FileInfo::getFileType, FileTypeEnum.PIC.getDataValue());
        lambdaQueryWrapper.eq(FileInfo::getThumbnail, false);
        lambdaQueryWrapper.eq(FileInfo::getHide, false);
        List<FileInfo> fileInfoList = fileInfoService.list(lambdaQueryWrapper);
        if(fileInfoList == null || fileInfoList.size() == 0){
            return;
        }
        // 获取生成缩略图的文件路径
        String dirThumbanailName = ".thumbnailImg";
        String thumbanailPath = filePoint.getSystemPath() + "/" + dirThumbanailName;
        File thumbanailFile = new File(thumbanailPath);
        if(!thumbanailFile.exists()){
            // 未找到缩略图存储目录，生成缩略图文件夹
            fileInfoService.createDir(null, dirThumbanailName);
        }

        String xs = "xs.jpg";
        String m = "m.jpg";
        String l = "l.jpg";
        // 处理
        fileInfoList.stream().forEach(f->{
            String picPath = f.getPath();
            String thumbnailPath = DiskFileUtil.getThumbnailPathMd5(f.getPointPath());
            String systemThumbnailPath = filePoint.getSystemPath() + "/" + dirThumbanailName + "/" + thumbnailPath;
            try {
                File dicFile = new File(systemThumbnailPath);
                if(!dicFile.exists()){
                    dicFile.mkdirs();
                }
                if(!new File(systemThumbnailPath + "/" + l).exists()){
                    // 大图
                    Thumbnails.of(picPath)
                            .size(800, 800)
                            .toFile(systemThumbnailPath + "/" + l);
                }
                if(!new File(systemThumbnailPath + "/" + m).exists()){
                    // 中图
                    Thumbnails.of(picPath)
                            .size(300, 300)
                            .toFile(systemThumbnailPath + "/" + m);
                }
                if(!new File(systemThumbnailPath + "/" + xs).exists()){
                    // 小图
                    Thumbnails.of(picPath)
                            .size(100, 100)
                            .toFile(systemThumbnailPath + "/" + xs);
                }
                f.setThumbnail(true);
                f.setThumbnailPath("/" + thumbnailPath + "/" + f.getFileId());
                fileInfoService.updateById(f);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
