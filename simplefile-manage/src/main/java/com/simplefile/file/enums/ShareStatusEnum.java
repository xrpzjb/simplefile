package com.simplefile.file.enums;

public enum ShareStatusEnum {

    NOT_SHARE("不分享", 0),

    SHARE("分享", 1),



    ;

    private final String dataKey;

    private final int dataValue;

    public String getDataKey() {
        return dataKey;
    }

    public int getDataValue() {
        return dataValue;
    }

    private ShareStatusEnum(String dataKey, int dataValue) {
        this.dataKey = dataKey;
        this.dataValue = dataValue;
    }

    // 普通方法
    public static String getName(int index) {
        for (ShareStatusEnum c : ShareStatusEnum.values()) {
            if (c.getDataValue() == index) {
                return c.dataKey;
            }
        }
        return null;
    }

}
