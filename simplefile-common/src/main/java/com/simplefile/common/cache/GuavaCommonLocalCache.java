package com.simplefile.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;

/**
 * 本地缓存
 */
public class GuavaCommonLocalCache {

    /** 登录TOKEN */
    public static final String KEY_LOGIN_TOKEN = "LOGIN_TOKEN";
    /** 图形验证码 */
    public static final String KEY_CAPTCHA_CODE = "CAPTCHA_CODE";
    /** 系统参数 */
    public static final String KEY_SYS_CONFIG = "SYS_CONFIG";
    /** 系统字典 */
    public static final String KEY_SYS_DICT = "SYS_DICT";
    /** 重复提交 */
    public static final String KEY_REPEAT_SUBMIT = "REPEAT_SUBMIT";
    /** 限流 */
    public static final String KEY_RATE_LIMIT = "RATE_LIMIT";
    /** 登录密码错误次数 */
    public static final String KEY_LOGIN_PWD_ERR_CNT = "LOGIN_PWD_ERR_CNT";
    /** 分享文件id加密替换 */
    public static final String KEY_SHARE_ID_ENCRY = "KEY_SHARE_ID_ENCRY";
    /** 分享文件id临时替换码 */
    public static final String KEY_SHARE_ID_TOKEN = "KEY_SHARE_ID_TOKEN";


//    @Value("${token.expireTime}")
//    private int expireTime;
//
//    @Value(value = "${user.password.lockTime}")
//    private int lockTime;
//
//    private static int loginExpireTime;
//    private static int loginLockTime;
//
//    {
//        GuavaCommonLocalCache.loginExpireTime = expireTime;
//        GuavaCommonLocalCache.loginLockTime = lockTime;
//    }


    /**
     * 登录TOKEN缓存
     */
    private static Cache<String, Object> loginTokenCache = CacheBuilder.newBuilder()
            //设置缓存初始大小，应该合理设置，后续会扩容
            .initialCapacity(5)
            //最大值
            .maximumSize(100)
            //并发数设置
            .concurrencyLevel(5)
            //缓存过期时间
            .expireAfterWrite(30, TimeUnit.MINUTES)
            //统计缓存命中率
            .recordStats()
            .build();

    /**
     * 图形验证码
     */
    private static Cache<String, Object> captchaCache = CacheBuilder.newBuilder()
            //设置缓存初始大小，应该合理设置，后续会扩容
            .initialCapacity(5)
            //最大值
            .maximumSize(100)
            //并发数设置
            .concurrencyLevel(5)
            //缓存过期时间
            .expireAfterWrite(2, TimeUnit.MINUTES)
            //统计缓存命中率
            .recordStats()
            .build();
    /**
     * 系统参数、字典
     * 基本永不过期
     */
    private static Cache<String, Object> sysDataCache = CacheBuilder.newBuilder()
            //设置缓存初始大小，应该合理设置，后续会扩容
            .initialCapacity(5)
            //最大值
            .maximumSize(100)
            //并发数设置
            .concurrencyLevel(5)
            //缓存过期时间
            .expireAfterWrite(365, TimeUnit.DAYS)
            //统计缓存命中率
            .recordStats()
            .build();

    /**
     * 重复提交
     */
    private static Cache<String, Object> repeatSubmitCache = CacheBuilder.newBuilder()
            //设置缓存初始大小，应该合理设置，后续会扩容
            .initialCapacity(10)
            //最大值
            .maximumSize(100)
            //并发数设置
            .concurrencyLevel(10)
            //缓存过期时间
            .expireAfterWrite(2000, TimeUnit.MILLISECONDS)
            //统计缓存命中率
            .recordStats()
            .build();

    /**
     * 限流
     */
    private static Cache<String, Object> rateLimitCache = CacheBuilder.newBuilder()
            //设置缓存初始大小，应该合理设置，后续会扩容
            .initialCapacity(5)
            //最大值
            .maximumSize(100)
            //并发数设置
            .concurrencyLevel(5)
            //缓存过期时间
            .expireAfterWrite(10, TimeUnit.SECONDS)
            //统计缓存命中率
            .recordStats()
            .build();

    /**
     * 登录错误次数
     */
    private static Cache<String, Object> loginPwdErrCntCache = CacheBuilder.newBuilder()
            //设置缓存初始大小，应该合理设置，后续会扩容
            .initialCapacity(5)
            //最大值
            .maximumSize(100)
            //并发数设置
            .concurrencyLevel(5)
            //缓存过期时间
            .expireAfterWrite(10, TimeUnit.MINUTES)
            //统计缓存命中率
            .recordStats()
            .build();

    /**
     * 分享文件id加密替换
     */
    private static Cache<String, Object> shareIdEncryCache = CacheBuilder.newBuilder()
            //设置缓存初始大小，应该合理设置，后续会扩容
            .initialCapacity(5)
            //最大值
            .maximumSize(10000)
            //并发数设置
            .concurrencyLevel(5)
            //缓存过期时间
            .expireAfterWrite(30, TimeUnit.MINUTES)
            //统计缓存命中率
            .recordStats()
            .build();

    /**
     * 分享文件id临时替换码
     */
    private static Cache<String, Object> shareIdTokenCache = CacheBuilder.newBuilder()
            //设置缓存初始大小，应该合理设置，后续会扩容
            .initialCapacity(5)
            //最大值
            .maximumSize(10000)
            //并发数设置
            .concurrencyLevel(5)
            //缓存过期时间
            .expireAfterWrite(30, TimeUnit.MINUTES)
            //统计缓存命中率
            .recordStats()
            .build();

    public static Cache<String, Object> getCacheObject(String cacheKey){
        switch (cacheKey){
            case KEY_LOGIN_TOKEN:
                return loginTokenCache;
            case KEY_REPEAT_SUBMIT:
                return repeatSubmitCache;
            case KEY_CAPTCHA_CODE:
                return captchaCache;
            case KEY_SYS_CONFIG:
                return sysDataCache;
            case KEY_SYS_DICT:
                return sysDataCache;
            case KEY_RATE_LIMIT:
                return rateLimitCache;
            case KEY_LOGIN_PWD_ERR_CNT:
                return loginPwdErrCntCache;
            case KEY_SHARE_ID_ENCRY:
                return shareIdEncryCache;
            case KEY_SHARE_ID_TOKEN:
                return shareIdTokenCache;
            default:
                return null;
        }
    }

    /**
     * 设置缓存
     * @param cacheKey
     * @param key
     * @param value
     */
    public static <T> void setCacheObject(String cacheKey, String key, T value){
        Cache<String, Object> cacheObject = getCacheObject(cacheKey);
        cacheObject.put(key, value);
    }

    /**
     * 判断缓存中是否有对应的value
     * @param cacheKey
     * @param key
     * @return
     */
    public static Boolean hasKey(String cacheKey, String key)
    {
        Cache<String, Object> cacheObject = getCacheObject(cacheKey);
        Object ifPresent = cacheObject.getIfPresent(key);
        return ifPresent == null ? false : true;
    }

    /**
     * 获取缓存
     * @param cacheKey
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getCacheObject(String cacheKey, String key){
        Cache<String, Object> cacheObject = getCacheObject(cacheKey);
        return (T) cacheObject.getIfPresent(key);
    }

    /**
     * 删除缓存
     * @param cacheKey
     * @param key
     */
    public static void deleteObject(String cacheKey, String key){
        Cache<String, Object> cacheObject = getCacheObject(cacheKey);
        cacheObject.invalidate(key);
    }

    /**
     * 清空缓存
     * @param cacheKey
     */
    public static void clearCache(String cacheKey){
        Cache<String, Object> cacheObject = getCacheObject(cacheKey);
        cacheObject.invalidateAll();
    }

    public static void main(String[] args) {
        GuavaCommonLocalCache.loginTokenCache.put("test", "test");
        Object test = GuavaCommonLocalCache.loginTokenCache.getIfPresent("test");
        System.out.println(test.toString());
    }

}

