package com.simplefile.wedav.domain;

import com.simplefile.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * WebDAV文件对象 sys_webdav_file
 *
 * @author doubao
 */
@Data
public class SysWebdavFile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 文件ID */
    private Long fileId;

    /** 父文件ID */
    private Long parentId;

    /** 文件路径 */
    private String filePath;

    /** 文件名 */
    private String fileName;

    /** 是否为目录 */
    private Boolean isDirectory;

    /** 文件大小 */
    private Long fileSize;

    /** 文件类型 */
    private String fileType;

    /** 存储路径 */
    private String storagePath;

    /** 所有者 */
    private String owner;


}
