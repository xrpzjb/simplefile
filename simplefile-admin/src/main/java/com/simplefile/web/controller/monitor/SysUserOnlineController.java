package com.simplefile.web.controller.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import com.google.common.cache.Cache;
import com.simplefile.common.cache.GuavaCommonLocalCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.simplefile.common.annotation.Log;
import com.simplefile.common.constant.CacheConstants;
import com.simplefile.common.core.controller.BaseController;
import com.simplefile.common.core.domain.AjaxResult;
import com.simplefile.common.core.domain.model.LoginUser;
import com.simplefile.common.core.page.TableDataInfo;
import com.simplefile.common.enums.BusinessType;
import com.simplefile.common.utils.StringUtils;
import com.simplefile.system.domain.SysUserOnline;
import com.simplefile.system.service.ISysUserOnlineService;

/**
 * 在线用户监控
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends BaseController
{
    @Autowired
    private ISysUserOnlineService userOnlineService;

    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    public TableDataInfo list(String ipaddr, String userName)
    {
        Cache<String, Object> cacheObject = GuavaCommonLocalCache.getCacheObject(GuavaCommonLocalCache.KEY_LOGIN_TOKEN);
        ConcurrentMap<String, Object> cacheObjectMap = cacheObject.asMap();
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : cacheObjectMap.keySet())
        {
            LoginUser user = GuavaCommonLocalCache.getCacheObject(GuavaCommonLocalCache.KEY_LOGIN_TOKEN, key);
            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName))
            {
                userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
            }
            else if (StringUtils.isNotEmpty(ipaddr))
            {
                userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
            }
            else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(user.getUser()))
            {
                userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
            }
            else
            {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return getDataTable(userOnlineList);
    }

    /**
     * 强退用户
     */
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public AjaxResult forceLogout(@PathVariable String tokenId)
    {
        GuavaCommonLocalCache.deleteObject(GuavaCommonLocalCache.KEY_LOGIN_TOKEN, CacheConstants.LOGIN_TOKEN_KEY + tokenId);
        return success();
    }
}
