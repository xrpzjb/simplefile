package com.simplefile.file.enums;

public enum FileTypeEnum {

    DIR("文件夹", 0),

    TEXT("文本", 1),

    PIC("图片", 2),

    DOC("word", 3),

    EXCEL("excel", 4),

    PDF("pdf", 5),

    ZIP("zip", 6),

    EXE("exe", 7),

    PPT("ppt", 8),

    JAR("jar", 9),

    BAT("bat", 10),

    HTML("html", 11),

    SQL("sql", 12),

    CONF("conf", 13),

    VIDEO("video", 14),

    AUDIO("音频", 15),

    INI("ini", 16),


    OTHER("其他", 999),


    ;

    private final String dataKey;

    private final int dataValue;

    public String getDataKey() {
        return dataKey;
    }

    public int getDataValue() {
        return dataValue;
    }

    private FileTypeEnum(String dataKey, int dataValue) {
        this.dataKey = dataKey;
        this.dataValue = dataValue;
    }

    // 普通方法
    public static String getName(int index) {
        for (FileTypeEnum c : FileTypeEnum.values()) {
            if (c.getDataValue() == index) {
                return c.dataKey;
            }
        }
        return null;
    }

}
