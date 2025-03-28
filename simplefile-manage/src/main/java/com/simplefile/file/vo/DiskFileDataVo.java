package com.simplefile.file.vo;

import com.simplefile.common.file.DiskFile;
import com.simplefile.common.file.DiskFileDir;
import lombok.Data;

import java.util.List;

/**
 * 文件列表返回数据
 */
@Data
public class DiskFileDataVo {

    /** 当前目录 */
    private DiskFile nowDisk;

    /** 当前列表 */
    private List<DiskFile> diskFileList;

    /** 上级目录 */
    private List<DiskFileDir> diskFileDirList;

    /** 根据文件后缀统计数量 */
    private List<DirCountFileSizeVo> fileTypeList;

    private Integer total;

    private Integer sumPageNum;

}
