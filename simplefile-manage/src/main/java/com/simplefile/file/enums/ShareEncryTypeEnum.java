package com.simplefile.file.enums;

public enum ShareEncryTypeEnum {

    SHARE_NOT_ENCRY("分享不加密", 0),

    SHARE_ENCRY("分享加密", 1),



    ;

    private final String dataKey;

    private final int dataValue;

    public String getDataKey() {
        return dataKey;
    }

    public int getDataValue() {
        return dataValue;
    }

    private ShareEncryTypeEnum(String dataKey, int dataValue) {
        this.dataKey = dataKey;
        this.dataValue = dataValue;
    }

    // 普通方法
    public static String getName(int index) {
        for (ShareEncryTypeEnum c : ShareEncryTypeEnum.values()) {
            if (c.getDataValue() == index) {
                return c.dataKey;
            }
        }
        return null;
    }

}
