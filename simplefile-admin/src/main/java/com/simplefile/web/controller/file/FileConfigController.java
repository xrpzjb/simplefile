package com.simplefile.web.controller.file;

import com.simplefile.common.core.domain.AjaxResult;
import com.simplefile.file.service.IFileConfigService;
import com.simplefile.file.vo.DiskOtherConfigVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/simplefile/config")
public class FileConfigController {

    @Resource
    private IFileConfigService fileConfigService;

    /**
     * 获取其他设置
     */
    // @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/otherConfig")
    public AjaxResult getOtherConfig()
    {
        DiskOtherConfigVo diskOtherConfig = fileConfigService.getDiskOtherConfig();
        return AjaxResult.success(diskOtherConfig);
    }

    /**
     * 其他设置
     */
    // @PreAuthorize("@ss.hasPermi('system:config:list')")
    @PostMapping("/otherConfig")
    public AjaxResult setOtherConfig(@Validated @RequestBody DiskOtherConfigVo diskOtherConfigVo)
    {
        fileConfigService.setDiskOtherConfig(diskOtherConfigVo);
        return AjaxResult.success();
    }
}
