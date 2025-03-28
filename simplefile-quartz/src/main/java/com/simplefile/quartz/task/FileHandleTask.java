package com.simplefile.quartz.task;

import com.simplefile.common.utils.DateUtils;
import com.simplefile.file.domain.FileInfo;
import com.simplefile.file.service.IFileInfoService;
import com.simplefile.system.domain.SysConfig;
import com.simplefile.system.service.ISysConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 文件处理定时任务
 *
 * 1.回收站文件删除
 *
 */
@Component("fileHandleTask")
public class FileHandleTask {

    private static final Logger log = LoggerFactory.getLogger(FileHandleTask.class);

    @Autowired
    private IFileInfoService fileInfoService;

    @Autowired
    private ISysConfigService sysConfigService;

    public void deleteFile() {
        // 获取回收站保留时间
        String recycleFileDay = sysConfigService.selectConfigByKey(SysConfig.CONFIG_RECYCLE_FILE_DAY);
        if(StringUtils.isBlank(recycleFileDay)){
            recycleFileDay = "7";
        }
        Date recycleTime = DateUtils.addDays(DateUtils.getNowDate(), -Integer.parseInt(recycleFileDay));
        // 获取回收站文件
        List<FileInfo> recycleFileList = fileInfoService.getRecycleFileList(recycleTime);
        if(recycleFileList == null || recycleFileList.size() == 0){
            return;
        }
        recycleFileList.stream().forEach(s->{
            try{
                fileInfoService.delFile(s.getFileId());
            }catch (Exception e){
                log.error("删除文件失败：" + s.getFileId() +" name:" + s.getName());
            }
        });
    }

}









