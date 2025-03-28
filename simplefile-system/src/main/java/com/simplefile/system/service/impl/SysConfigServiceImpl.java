package com.simplefile.system.service.impl;

import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.simplefile.common.cache.GuavaCommonLocalCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.simplefile.common.annotation.DataSource;
import com.simplefile.common.constant.CacheConstants;
import com.simplefile.common.constant.UserConstants;
import com.simplefile.common.core.text.Convert;
import com.simplefile.common.enums.DataSourceType;
import com.simplefile.common.exception.ServiceException;
import com.simplefile.common.utils.StringUtils;
import com.simplefile.system.domain.SysConfig;
import com.simplefile.system.mapper.SysConfigMapper;
import com.simplefile.system.service.ISysConfigService;

/**
 * 参数配置 服务层实现
 *
 * @author ruoyi
 */
@Service
public class SysConfigServiceImpl implements ISysConfigService
{
    @Autowired
    private SysConfigMapper configMapper;

//    @Autowired
//    private RedisCache redisCache;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init()
    {
        loadingConfigCache();
    }

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    @DataSource(DataSourceType.MASTER)
    public SysConfig selectConfigById(Long configId)
    {
        SysConfig config = new SysConfig();
        config.setConfigId(configId);
        return configMapper.selectConfig(config);
    }

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey)
    {
        // String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey)));
        String cacheKey = getCacheKey(configKey);
        String configValue = Convert.toStr(GuavaCommonLocalCache.getCacheObject(GuavaCommonLocalCache.KEY_SYS_CONFIG, cacheKey));
        if (StringUtils.isNotEmpty(configValue))
        {
            return configValue;
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        SysConfig retConfig = configMapper.selectConfig(config);
        if (StringUtils.isNotNull(retConfig))
        {
            // redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            GuavaCommonLocalCache.setCacheObject(GuavaCommonLocalCache.KEY_SYS_CONFIG, cacheKey, retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public SysConfig selectConfigObjByKey(String configKey) {
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        SysConfig retConfig = configMapper.selectConfig(config);
        return retConfig;
    }

    /**
     * 获取验证码开关
     *
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaEnabled()
    {
        String captchaEnabled = selectConfigByKey("sys.account.captchaEnabled");
        if (StringUtils.isEmpty(captchaEnabled))
        {
            return true;
        }
        return Convert.toBool(captchaEnabled);
    }

    /**
     * 查询参数配置列表
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config)
    {
        return configMapper.selectConfigList(config);
    }

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config)
    {
        config.setConfigId(IdWorker.getId());
        int row = configMapper.insertConfig(config);
        if (row > 0)
        {
            // redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
            String cacheKey = getCacheKey(config.getConfigKey());
            GuavaCommonLocalCache.setCacheObject(GuavaCommonLocalCache.KEY_SYS_CONFIG, cacheKey, config.getConfigValue());
        }
        return row;
    }

    /**
     * 修改参数配置
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config)
    {
        SysConfig temp = configMapper.selectConfigById(config.getConfigId());
        if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey()))
        {
            // redisCache.deleteObject(getCacheKey(temp.getConfigKey()));
            String cacheKey = getCacheKey(temp.getConfigKey());
            GuavaCommonLocalCache.deleteObject(GuavaCommonLocalCache.KEY_SYS_CONFIG, cacheKey);
        }

        int row = configMapper.updateConfig(config);
        if (row > 0)
        {
            // redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
            String cacheKey = getCacheKey(config.getConfigKey());
            GuavaCommonLocalCache.setCacheObject(GuavaCommonLocalCache.KEY_SYS_CONFIG, cacheKey, config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(Long[] configIds)
    {
        for (Long configId : configIds)
        {
            SysConfig config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType()))
            {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            configMapper.deleteConfigById(configId);
            // redisCache.deleteObject(getCacheKey(config.getConfigKey()));
            String cacheKey = getCacheKey(config.getConfigKey());
            GuavaCommonLocalCache.deleteObject(GuavaCommonLocalCache.KEY_SYS_CONFIG, cacheKey);
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache()
    {
        List<SysConfig> configsList = configMapper.selectConfigList(new SysConfig());
        for (SysConfig config : configsList)
        {
            // redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
            String cacheKey = getCacheKey(config.getConfigKey());
            GuavaCommonLocalCache.setCacheObject(GuavaCommonLocalCache.KEY_SYS_CONFIG, cacheKey, config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache()
    {
        // Collection<String> keys = redisCache.keys(CacheConstants.SYS_CONFIG_KEY + "*");
        // redisCache.deleteObject(keys);
        GuavaCommonLocalCache.clearCache(GuavaCommonLocalCache.KEY_SYS_CONFIG);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache()
    {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfig config)
    {
        Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey)
    {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }
}
