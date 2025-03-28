package com.simplefile.common.file;

import lombok.Data;

/**
 * 当前目录
 *
 *
 *
 */
@Data
public class DiskFileDir {

    private String id;

    /** 目录名称 */
    private String name;

    /** 目录地址 */
    private String filePath;

    /** 父目录地址 */
    private String parsentPath;

    /** 父目录名称 */
    private String parsentName;

}
