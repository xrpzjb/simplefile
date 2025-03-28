package com.simplefile.file.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.simplefile.common.core.domain.BaseEntity;
import com.simplefile.common.utils.StringUtils;
import com.simplefile.common.utils.uuid.UUID;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.simplefile.common.annotation.Excel;

/**
 * 文件分享对象 file_share
 *
 * @author zhujiabao
 * @date 2024-09-03
 */
public class FileShare extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分享id */
    private Long shareId;

    /** 分享code */
    private String shareCode;

    /** 文件id */
    private Long fileId;

    /** 分享加密类型 */
    @Excel(name = "分享加密类型")
    private Integer shareEncryType;

    /** 分享加密code */
    @Excel(name = "分享加密code")
    private String shareEncryCode;

    /** 分享加密到期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "分享加密到期时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date shareTime;

    /** 访问次数 */
    @Excel(name = "访问次数")
    private Long visitNum;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    @TableField(exist = false)
    private String link;

    public static String getNewShareEncryCode(){
        String code = UUID.fastUUID().toString().substring(0, 4);
        return code;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    public static String newShareCode(){
        return StringUtils._10_to_62(System.currentTimeMillis(), 0);
    }

    public void setShareId(Long shareId)
    {
        this.shareId = shareId;
    }

    public Long getShareId()
    {
        return shareId;
    }
    public void setShareEncryType(Integer shareEncryType)
    {
        this.shareEncryType = shareEncryType;
    }

    public Integer getShareEncryType()
    {
        return shareEncryType;
    }
    public void setShareEncryCode(String shareEncryCode)
    {
        this.shareEncryCode = shareEncryCode;
    }

    public String getShareEncryCode()
    {
        return shareEncryCode;
    }
    public void setShareTime(Date shareTime)
    {
        this.shareTime = shareTime;
    }

    public Date getShareTime()
    {
        return shareTime;
    }
    public void setVisitNum(Long visitNum)
    {
        this.visitNum = visitNum;
    }

    public Long getVisitNum()
    {
        return visitNum;
    }
    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("shareId", getShareId())
            .append("shareEncryType", getShareEncryType())
            .append("shareEncryCode", getShareEncryCode())
            .append("shareTime", getShareTime())
            .append("visitNum", getVisitNum())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
