package com.simplefile.file.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 其他设置vo
 */
@Data
public class DiskOtherConfigVo {

    @NotNull(message = "回收站天数不能为空")
    private Integer recycleDay;

    @NotNull(message = "网站地址")
    private String siteAddress;


}
