package com.simplefile.file.manager.factory;

import com.simplefile.common.utils.spring.SpringUtils;
import com.simplefile.file.service.IFileInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异步工厂（产生任务用）
 *
 * @author ruoyi
 */
public class FileAsyncFactory
{

    /**
     * 正在扫描文件任务
     */
    public static ConcurrentHashMap<String, Boolean> scanTaskMap = new ConcurrentHashMap<>(2);

    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");


    /**
     * 文件目录扫描
     *
     * @return 任务task
     */
    public static TimerTask scannFile(Long pointId, String systemPath, String path, Integer type ,Long pid)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                if(scanTaskMap.get(systemPath) != null){
                    return;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (new String("scanTaskFile").intern()){
                    if(scanTaskMap.get(systemPath) != null){
                        return;
                    }
                    try{
                        scanTaskMap.put(systemPath, true);
                        SpringUtils.getBean(IFileInfoService.class).scannFile(pointId, systemPath, path, type, pid);
                    }catch (Exception e){

                    }finally {
                        scanTaskMap.remove(systemPath);
                    }
                }
            }
        };
    }
}
