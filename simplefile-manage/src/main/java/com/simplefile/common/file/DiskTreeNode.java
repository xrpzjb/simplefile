package com.simplefile.common.file;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DiskTreeNode {

    String label;
    boolean isDirectory;
    List<DiskTreeNode> children;

    DiskTreeNode(String label, boolean isDirectory) {
        this.label = label;
        this.isDirectory = isDirectory;
        this.children = new ArrayList<>();
    }

}
