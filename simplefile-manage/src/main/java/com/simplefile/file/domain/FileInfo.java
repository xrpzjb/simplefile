package com.simplefile.file.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.simplefile.common.core.domain.BaseEntity;
import com.simplefile.file.enums.*;
import com.simplefile.file.util.SystemStringUtil;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.simplefile.common.annotation.Excel;

/**
 * 文件信息对象 file_info
 *
 * @author zhujiabao
 * @date 2024-08-26
 */
@Data
public class FileInfo extends BaseEntity
{

    // 全部扫描
    public final static Integer FILE_SCAN_TYPE_ALL = 1;
    // 当前目录扫描
    public final static Integer FILE_SCAN_TYPE_NOWPATH = 2;

    private static final long serialVersionUID = 1L;

    public static FileInfo getNewDefault(){
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId(IdWorker.getId());
        fileInfo.setPreCode(getNewPreCode());
        fileInfo.setDirBol(true);
        fileInfo.setFileType(FileTypeEnum.DIR.getDataValue());
        fileInfo.setChildCount(0);
        fileInfo.setHide(false);
        fileInfo.setRecycleLevel(0);
        fileInfo.setRecycleBol(false);
        fileInfo.setShareEncryType(ShareEncryTypeEnum.SHARE_NOT_ENCRY.getDataValue());
        fileInfo.setShareStatus(ShareStatusEnum.NOT_SHARE.getDataValue());
        fileInfo.setStatus(FileStatusEnum.NORMAL.getDataValue());
        fileInfo.setVisitNum(0);
        fileInfo.setCreateTime(new Date());
        fileInfo.setUpdateTime(new Date());
        fileInfo.setDelFlag(DeletedEnum.NORMAL.getDataValue());
        fileInfo.setFileSize(0L);
        return fileInfo;
    }

    /** 主键id */
    @TableId
    private Long fileId;

    /** 标识码 */
    @Excel(name = "标识码")
    private String preCode;

    /** 父级目录 */
    @Excel(name = "父级目录")
    private Long parentId;

    /** 文件名称 */
    @Excel(name = "文件名称")
    private String name;

    /** 文件路径 */
    @Excel(name = "文件路径")
    private String path;

    @Excel(name = "指向的文件路径")
    private String pointPath;

    /** 文件类型 */
    @Excel(name = "文件类型")
    private Integer fileType;

    /** 是否是目录 */
    @Excel(name = "是否是目录")
    private Boolean dirBol;

    /** 文件大小 */
    @Excel(name = "文件大小")
    private Long fileSize;

    /** 子文件数量 */
    @Excel(name = "子文件数量")
    private Integer childCount;

    /** md5值 */
    @Excel(name = "md5值")
    private String md5Value;

    /** 状态 */
    @Excel(name = "状态")
    private Integer status;
//
//    /** 当前进行得状态 0 默认 1解压中 2解压完成 */
//    private Integer nowingStatus;

    /** 是否分享 */
    @Excel(name = "是否分享")
    private Integer shareStatus;

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
    private Integer visitNum;

    /** 是否隐藏 */
    private Boolean hide;

    /** 缩略图是否生成 */
    private Boolean thumbnail;

    /** 缩略图地址 */
    private String thumbnailPath;

    private Date fileUpdateTime;

    /** 文件最近使用时间 */
    private Date fileUseTime;

    /** 文件移除时间 */
    private Date recycleTime;

    /** 文件回收时间 */
    private Boolean recycleBol;

    /** 文件回收等级(显示在回收站根目录为1，其他为0无等级) */
    private Integer recycleLevel;

    /** 扫描来源 */
    private Long filePointId;

    /** 删除标志（0代表存在 2代表删除） */
//    @TableLogic(value = "0", delval = "2")
    private String delFlag = "0";

    public static String getNewPreCode(){
        return SystemStringUtil._10_to_36(System.currentTimeMillis(), 10);
    }


}
