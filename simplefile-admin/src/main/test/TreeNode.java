import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    String name;
    boolean isDirectory;
    List<TreeNode> children;

    TreeNode(String name, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.children = new ArrayList<>();
    }

}
