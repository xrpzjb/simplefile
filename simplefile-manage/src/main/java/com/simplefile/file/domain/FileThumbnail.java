package com.simplefile.file.domain;

import com.simplefile.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.simplefile.common.annotation.Excel;

/**
 * 缩略图对象 file_thumbnail
 *
 * @author zhujiabao
 * @date 2024-09-03
 */
public class FileThumbnail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 缩略图 */
    private Long thumbnailId;

    /** 文件id */
    @Excel(name = "文件id")
    private Long fileId;

    /** 文件大小 */
    @Excel(name = "文件大小")
    private String fileSize;

    /** 系统路径 */
    @Excel(name = "系统路径")
    private String systemPath;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setThumbnailId(Long thumbnailId)
    {
        this.thumbnailId = thumbnailId;
    }

    public Long getThumbnailId()
    {
        return thumbnailId;
    }
    public void setFileId(Long fileId)
    {
        this.fileId = fileId;
    }

    public Long getFileId()
    {
        return fileId;
    }
    public void setFileSize(String fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getFileSize()
    {
        return fileSize;
    }
    public void setSystemPath(String systemPath)
    {
        this.systemPath = systemPath;
    }

    public String getSystemPath()
    {
        return systemPath;
    }
    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("thumbnailId", getThumbnailId())
            .append("fileId", getFileId())
            .append("fileSize", getFileSize())
            .append("systemPath", getSystemPath())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
