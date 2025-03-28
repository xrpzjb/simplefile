package com.simplefile.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.simplefile.file.service.IFileConfigService;
import com.simplefile.file.vo.DiskOtherConfigVo;
import com.simplefile.system.domain.SysConfig;
import com.simplefile.system.service.ISysConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 文件配置
 */
@Service
public class FileConfigService implements IFileConfigService {

    @Resource
    private ISysConfigService sysConfigService;

    /**
     * 获取其他配置
     * @return
     */

    @Override
    public DiskOtherConfigVo getDiskOtherConfig() {
        // 回收站天数
        String recycleFileDay = sysConfigService.selectConfigByKey(SysConfig.CONFIG_RECYCLE_FILE_DAY);
        if(StringUtils.isBlank(recycleFileDay)){
            recycleFileDay = "7";
        }
        DiskOtherConfigVo otherConfigVo = new DiskOtherConfigVo();
        otherConfigVo.setRecycleDay(Integer.valueOf(recycleFileDay));

        // 网站地址
        String siteAddress = sysConfigService.selectConfigByKey(SysConfig.CONFIG_SITE_ADDRES);
        if(StringUtils.isBlank(siteAddress)){
            siteAddress = "";
        }
        otherConfigVo.setSiteAddress(siteAddress);

        return otherConfigVo;
    }

    /**
     * 配置更新
     * @param diskOtherConfigVo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void setDiskOtherConfig(DiskOtherConfigVo diskOtherConfigVo) {
        // 回收站天数
        if(diskOtherConfigVo.getRecycleDay() != null){
            SysConfig sysConfig = sysConfigService.selectConfigObjByKey(SysConfig.CONFIG_RECYCLE_FILE_DAY);
            if(sysConfig == null){
                sysConfig = new SysConfig("回收站保留天数",
                        SysConfig.CONFIG_RECYCLE_FILE_DAY,
                        diskOtherConfigVo.getRecycleDay().toString());
                sysConfigService.insertConfig(sysConfig);
            }else{
                sysConfig.setConfigValue(diskOtherConfigVo.getRecycleDay().toString());
                sysConfigService.updateConfig(sysConfig);
            }
        }
        // 网站地址
        if(StringUtils.isNotBlank(diskOtherConfigVo.getSiteAddress())){
            SysConfig sysConfig = sysConfigService.selectConfigObjByKey(SysConfig.CONFIG_SITE_ADDRES);
            if(sysConfig == null){
                sysConfig = new SysConfig("网站地址",
                        SysConfig.CONFIG_SITE_ADDRES,
                        diskOtherConfigVo.getSiteAddress().toString());
                sysConfigService.insertConfig(sysConfig);
            }else{
                sysConfig.setConfigValue(diskOtherConfigVo.getSiteAddress());
                sysConfigService.updateConfig(sysConfig);
            }
        }

    }
}
