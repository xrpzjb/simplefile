package com.simplefile.file.enums;

public enum FileUnifiedTypeEnum {

    TXT("文本", "txt"),
    PIC("图片", "pic"),
    AUDIO("音频", "audio"),
    VIDEO("视频", "video"),
    DOC("word文档", "doc"),
    PDF("pdf文档", "pdf"),
    EXCEL("excel文档", "excel"),
    PPT("ppt文档", "ppt"),
    ZIP("压缩包", "zip"),
    OTHER("其他", "other")
    ;

    private final String dataKey;

    private final String dataValue;


    public String getDataKey() {
        return dataKey;
    }

    public String getDataValue() {
        return dataValue;
    }

    private FileUnifiedTypeEnum(String dataKey, String dataValue) {
        this.dataKey = dataKey;
        this.dataValue = dataValue;
    }

    // 普通方法
    public static String getName(String index) {
        for (FileUnifiedTypeEnum c : FileUnifiedTypeEnum.values()) {
            if (c.getDataValue().equals(index)) {
                return c.dataKey;
            }
        }
        return null;
    }

}
