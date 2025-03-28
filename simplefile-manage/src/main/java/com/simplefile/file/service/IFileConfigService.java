package com.simplefile.file.service;

import com.simplefile.file.vo.DiskOtherConfigVo;

/**
 * 文件配置
 */
public interface IFileConfigService {

    /**
     * 获取其他配置
     * @return
     */
    DiskOtherConfigVo getDiskOtherConfig();

    /**
     * 其他配置更新
     * @param diskOtherConfigVo
     */
    void setDiskOtherConfig(DiskOtherConfigVo diskOtherConfigVo);

}
