package com.simplefile.common.file;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 磁盘文件信息
 */
@Data
public class DiskFile {

    private String id;

    private Long fileId;

    private String shareFileId;

    /** 文件名称 */
    private String name;

    /** 文件路径 */
    private String path;

    private String pointPath;

    /** 文件大小(包含目录下的文件) */
    private Long fileSize;

    private String fileSizeName;

    /** 文件类型 */
    private Integer type;

    private Integer pages;

    private boolean isDir;

    @JsonFormat(pattern = "yyyy年MM月dd日 HH:mm:ss", timezone = "GMT+8")
    private Date lastModifiedTime;

    /** 子文件数量 */
    private Integer childCount;

    /** 解析文件类型 */
    private String handleFileType;

    /** 子文件 */
    private List<DiskFile> childFileList;

}
