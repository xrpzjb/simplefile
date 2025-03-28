package com.simplefile.file.enums;

public enum FileStatusEnum {

    NORMAL("正常", 0),




    ;

    private final String dataKey;

    private final int dataValue;

    public String getDataKey() {
        return dataKey;
    }

    public int getDataValue() {
        return dataValue;
    }

    private FileStatusEnum(String dataKey, int dataValue) {
        this.dataKey = dataKey;
        this.dataValue = dataValue;
    }

    // 普通方法
    public static String getName(int index) {
        for (FileStatusEnum c : FileStatusEnum.values()) {
            if (c.getDataValue() == index) {
                return c.dataKey;
            }
        }
        return null;
    }

}
