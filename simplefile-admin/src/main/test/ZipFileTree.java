import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFileTree {

    public static TreeNode buildTree(String zipFilePath) {
        TreeNode root = new TreeNode("根目录", true);
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                String[] parts = entry.getName().split("/");
                TreeNode current = root;
                for (int i = 0; i < parts.length; i++) {
                    boolean isDir = entry.isDirectory() || (i < parts.length - 1);
                    TreeNode child = findChild(current, parts[i]);
                    if (child == null) {
                        child = new TreeNode(parts[i], isDir);
                        current.children.add(child);
                    }
                    current = child;
                }
                zipIn.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return root;
    }

    private static TreeNode findChild(TreeNode parent, String name) {
        for (TreeNode child : parent.children) {
            if (child.name.equals(name)) {
                return child;
            }
        }
        return null;
    }

    public static void printTree(TreeNode node, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        sb.append(node.name);
        System.out.println(sb.toString());
        for (TreeNode child : node.children) {
            printTree(child, level + 1);
        }
    }

    public static void main(String[] args) {
        String zipFilePath = "C:\\Users\\nmwork\\Desktop\\ruoyi1212121.zip"; // 请将此路径替换为实际的 ZIP 文件路径
        TreeNode root = buildTree(zipFilePath);
        printTree(root, 0);
    }

}
