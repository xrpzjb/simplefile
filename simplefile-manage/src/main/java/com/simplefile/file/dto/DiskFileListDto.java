package com.simplefile.file.dto;

import lombok.Data;

@Data
public class DiskFileListDto {

    /**
     * 路径id
     */
    private String dirId;

    /**
     * 查当前
     */
    private String nowFileId;

    /**
     * 搜索的文件名称
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件路径（不提供前端指定）
     */
    private String filePath;

    /**
     * 是否强制刷新
     */
    private Boolean refresh;

    /**
     * 回收站是否有值
     */
    private Boolean recycleBol = false;

    private Boolean hide = false;


    /**
     * 删除等级
     */
    private Integer recycleLevel;

    /**
     * 当前页
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10000;

    /**
     * 来源类型
     * sourceType == 1 选择个人空间
     */
    private Integer sourceType = 0;

}
