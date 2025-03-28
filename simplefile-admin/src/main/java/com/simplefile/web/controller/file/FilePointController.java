package com.simplefile.web.controller.file;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.simplefile.common.annotation.Log;
import com.simplefile.common.core.controller.BaseController;
import com.simplefile.common.core.domain.AjaxResult;
import com.simplefile.common.enums.BusinessType;
import com.simplefile.file.domain.FilePoint;
import com.simplefile.file.service.IFilePointService;
import com.simplefile.common.utils.poi.ExcelUtil;
import com.simplefile.common.core.page.TableDataInfo;

/**
 * 文件映射Controller
 *
 * @author zhujiabao
 * @date 2024-11-07
 */
@RestController
@RequestMapping("/set/point")
public class FilePointController extends BaseController
{
    @Autowired
    private IFilePointService filePointService;

    /**
     * 查询文件映射列表
     */
//    @PreAuthorize("@ss.hasPermi('set:point:list')")
    @GetMapping("/list")
    public TableDataInfo list(FilePoint filePoint)
    {
        // startPage();
        List<FilePoint> list = filePointService.selectFilePointList(filePoint);
        return getDataTable(list);
    }
//
//    /**
//     * 导出文件映射列表
//     */
//    @PreAuthorize("@ss.hasPermi('set:point:export')")
//    @Log(title = "文件映射", businessType = BusinessType.EXPORT)
//    @PostMapping("/export")
//    public void export(HttpServletResponse response, FilePoint filePoint)
//    {
//        List<FilePoint> list = filePointService.selectFilePointList(filePoint);
//        ExcelUtil<FilePoint> util = new ExcelUtil<FilePoint>(FilePoint.class);
//        util.exportExcel(response, list, "文件映射数据");
//    }

    /**
     * 获取文件映射详细信息
     */
//    @PreAuthorize("@ss.hasPermi('set:point:query')")
    @GetMapping(value = "/{pointId}")
    public AjaxResult getInfo(@PathVariable("pointId") Long pointId)
    {
        return success(filePointService.selectFilePointByPointId(pointId));
    }

    /**
     * 新增文件映射
     */
//    @PreAuthorize("@ss.hasPermi('set:point:add')")
    @Log(title = "文件映射", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FilePoint filePoint)
    {
        if(StringUtils.isBlank(filePoint.getPointPath()) || StringUtils.isBlank(filePoint.getSystemPath())){
            return AjaxResult.error("路径不能为空");
        }
        if(filePoint.getPointPath().indexOf("/") != 0){
            filePoint.setPointPath("/" + filePoint.getPointPath());
        }
        filePoint.setPointPath(filePoint.getPointPath().replaceAll("\\\\", "/"));
        filePoint.setSystemPath(filePoint.getSystemPath().replaceAll("\\\\", "/"));
        filePoint.setPointId(IdWorker.getId());
        return toAjax(filePointService.insertFilePoint(filePoint));
    }

    /**
     * 修改文件映射
     */
//    @PreAuthorize("@ss.hasPermi('set:point:edit')")
//    @Log(title = "文件映射", businessType = BusinessType.UPDATE)
//    @PutMapping
//    public AjaxResult edit(@RequestBody FilePoint filePoint)
//    {
//        if(filePoint.getPointId() == null){
//            return AjaxResult.error("请求错误");
//        }
//        if(StringUtils.isBlank(filePoint.getPointPath()) || StringUtils.isBlank(filePoint.getSystemPath())){
//            return AjaxResult.error("路径不能为空");
//        }
//        if(filePoint.getPointPath().indexOf("/") != 0){
//            filePoint.setPointPath("/" + filePoint.getPointPath());
//        }
//        filePoint.setPointPath(filePoint.getPointPath().replaceAll("\\\\", "/"));
//        filePoint.setSystemPath(filePoint.getSystemPath().replaceAll("\\\\", "/"));
//        return toAjax(filePointService.updateFilePoint(filePoint));
//    }

    /**
     * 删除文件映射
     */
//    @PreAuthorize("@ss.hasPermi('set:point:remove')")
    @Log(title = "文件映射", businessType = BusinessType.DELETE)
	@DeleteMapping("/{pointIds}")
    public AjaxResult remove(@PathVariable Long[] pointIds)
    {
        return toAjax(filePointService.deleteFilePointByPointIds(pointIds));
    }

    /**
     * 文件路径扫描
     * 全量扫描
     */
    // @PreAuthorize("@ss.hasPermi('set:point:query')")
    @GetMapping(value = "/scannAll/{pointId}")
    public AjaxResult scannAll(@PathVariable("pointId") Long pointId)
    {
        filePointService.filepointScannAll(pointId);
        return success();
    }

    /**
     * 文件路径扫描
     * 增量扫描
     */
    // @PreAuthorize("@ss.hasPermi('set:point:query')")
    @GetMapping(value = "/scannUpdate/{pointId}")
    public AjaxResult scannUpdate(@PathVariable("pointId") Long pointId)
    {
        filePointService.filepointScannUpdate(pointId);
        return success();
    }
}
