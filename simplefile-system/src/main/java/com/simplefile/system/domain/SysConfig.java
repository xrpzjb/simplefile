package com.simplefile.system.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.simplefile.common.annotation.Excel;
import com.simplefile.common.annotation.Excel.ColumnType;
import com.simplefile.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 参数配置表 sys_config
 *
 * @author ruoyi
 */
public class SysConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    // 回收站文件保留天数
    public final static String CONFIG_RECYCLE_FILE_DAY = "file.recycle.day";
    // 网站的地址
    public final static String CONFIG_SITE_ADDRES = "site.address";

    public SysConfig(){

    }

    public SysConfig(String configName,String configKey, String configValue){
        this.setConfigName(configName);
        this.setConfigId(IdWorker.getId());
        this.setConfigKey(configKey);
        this.setConfigValue("Y");
        this.setCreateTime(new Date());
        this.setUpdateTime(new Date());
        this.setConfigValue(configValue);
    }

    /** 参数主键 */
    @Excel(name = "参数主键", cellType = ColumnType.NUMERIC)
    private Long configId;

    /** 参数名称 */
    @Excel(name = "参数名称")
    private String configName;

    /** 参数键名 */
    @Excel(name = "参数键名")
    private String configKey;

    /** 参数键值 */
    @Excel(name = "参数键值")
    private String configValue;

    /** 系统内置（Y是 N否） */
    @Excel(name = "系统内置", readConverterExp = "Y=是,N=否")
    private String configType;

    public Long getConfigId()
    {
        return configId;
    }

    public void setConfigId(Long configId)
    {
        this.configId = configId;
    }

    @NotBlank(message = "参数名称不能为空")
    @Size(min = 0, max = 100, message = "参数名称不能超过100个字符")
    public String getConfigName()
    {
        return configName;
    }

    public void setConfigName(String configName)
    {
        this.configName = configName;
    }

    @NotBlank(message = "参数键名长度不能为空")
    @Size(min = 0, max = 100, message = "参数键名长度不能超过100个字符")
    public String getConfigKey()
    {
        return configKey;
    }

    public void setConfigKey(String configKey)
    {
        this.configKey = configKey;
    }

    @NotBlank(message = "参数键值不能为空")
    @Size(min = 0, max = 500, message = "参数键值长度不能超过500个字符")
    public String getConfigValue()
    {
        return configValue;
    }

    public void setConfigValue(String configValue)
    {
        this.configValue = configValue;
    }

    public String getConfigType()
    {
        return configType;
    }

    public void setConfigType(String configType)
    {
        this.configType = configType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("configId", getConfigId())
            .append("configName", getConfigName())
            .append("configKey", getConfigKey())
            .append("configValue", getConfigValue())
            .append("configType", getConfigType())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
