package com.simplefile.file.dto;

import lombok.Data;

import java.util.Date;

/**
 * 分享
 */
@Data
public class DiskFileShareDto {

    private Long fileId;

    private String shareEncryCode;

    private Date shareTime;

}
