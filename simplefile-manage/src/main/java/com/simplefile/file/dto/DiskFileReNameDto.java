package com.simplefile.file.dto;

import lombok.Data;

/**
 * 文件重命名
 */
@Data
public class DiskFileReNameDto {

    private Long fileId;

    private String newName;

}
