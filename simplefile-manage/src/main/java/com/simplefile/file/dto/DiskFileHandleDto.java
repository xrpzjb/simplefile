package com.simplefile.file.dto;

import lombok.Data;

@Data
public class DiskFileHandleDto {

    private Long fileId;

    private Long dirId;

    private String newName;

}
