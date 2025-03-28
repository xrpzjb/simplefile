package com.simplefile.file.enums;

public enum DeletedEnum {

    NORMAL("正常", "0"),

    DEL("已删除", "2"),


    ;

    private final String dataKey;

    private final String dataValue;

    public String getDataKey() {
        return dataKey;
    }

    public String getDataValue() {
        return dataValue;
    }

    private DeletedEnum(String dataKey, String dataValue) {
        this.dataKey = dataKey;
        this.dataValue = dataValue;
    }

    // 普通方法
    public static String getName(String index) {
        for (DeletedEnum c : DeletedEnum.values()) {
            if (c.getDataValue().equals(index)) {
                return c.dataKey;
            }
        }
        return null;
    }

}
