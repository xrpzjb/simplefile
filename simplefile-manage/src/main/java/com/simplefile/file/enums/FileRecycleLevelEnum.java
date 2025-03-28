package com.simplefile.file.enums;


public enum FileRecycleLevelEnum {

    RECYCLE_NORMAL("正常", 0),
    RECYCLE_ONE("删除的顶级目录", 1),

    ;

    private final String dataKey;

    private final int dataValue;

    public String getDataKey() {
        return dataKey;
    }

    public int getDataValue() {
        return dataValue;
    }

    private FileRecycleLevelEnum(String dataKey, int dataValue) {
        this.dataKey = dataKey;
        this.dataValue = dataValue;
    }

    // 普通方法
    public static String getName(int index) {
        for (FileRecycleLevelEnum c : FileRecycleLevelEnum.values()) {
            if (c.getDataValue() == index) {
                return c.dataKey;
            }
        }
        return null;
    }

}
