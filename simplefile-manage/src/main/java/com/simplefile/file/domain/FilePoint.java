package com.simplefile.file.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.simplefile.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.simplefile.common.annotation.Excel;

/**
 * 文件映射对象 file_point
 *
 * @author zhujiabao
 * @date 2024-09-03
 */
public class FilePoint extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long pointId;

    /** 系统路径 */
    @Excel(name = "系统路径")
    private String systemPath;

    /** 映射路径 */
    @Excel(name = "映射路径")
    private String pointPath;

    /** 扫描时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "扫描时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date scannTime;

        /** 扫描状态（0代表未扫描 1代表扫描中 2代表扫描完成） */
    private Integer scannStatus;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setPointId(Long pointId)
    {
        this.pointId = pointId;
    }

    public Long getPointId()
    {
        return pointId;
    }
    public void setSystemPath(String systemPath)
    {
        this.systemPath = systemPath;
    }

    public String getSystemPath()
    {
        return systemPath;
    }
    public void setPointPath(String pointPath)
    {
        this.pointPath = pointPath;
    }

    public String getPointPath()
    {
        return pointPath;
    }
    public void setScannTime(Date scannTime)
    {
        this.scannTime = scannTime;
    }

    public Date getScannTime()
    {
        return scannTime;
    }
    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public Integer getScannStatus() {
        return scannStatus;
    }

    public void setScannStatus(Integer scannStatus) {
        this.scannStatus = scannStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("pointId", getPointId())
            .append("systemPath", getSystemPath())
            .append("pointPath", getPointPath())
            .append("scannTime", getScannTime())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
