package com.simplefile.file.vo;

import lombok.Data;

/**
 * 分享加密
 */
@Data
public class ShareFileIdEncryVo {

    /** 文件id加密后id */
    private String shareId;

    /** 文件夹真实ID */
    private String dirId;

    /** 所属分享code */
    private String shareCode;

    public ShareFileIdEncryVo(String shareId, String dirId, String shareCode) {
        this.shareId = shareId;
        this.dirId = dirId;
        this.shareCode = shareCode;
    }

}
