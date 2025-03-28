package com.simplefile.file.enums;

public enum FilePointScannStatusEnum {

    NOT("未扫描", 0),
    ING("扫描中", 1),
    COMPLETE("扫描完成", 2),




    ;

    private final String dataKey;

    private final int dataValue;

    public String getDataKey() {
        return dataKey;
    }

    public int getDataValue() {
        return dataValue;
    }

    private FilePointScannStatusEnum(String dataKey, int dataValue) {
        this.dataKey = dataKey;
        this.dataValue = dataValue;
    }

    // 普通方法
    public static String getName(int index) {
        for (FilePointScannStatusEnum c : FilePointScannStatusEnum.values()) {
            if (c.getDataValue() == index) {
                return c.dataKey;
            }
        }
        return null;
    }

}
