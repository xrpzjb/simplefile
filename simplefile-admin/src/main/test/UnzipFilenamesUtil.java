import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class UnzipFilenamesUtil {

    public static void main(String[] args) {
        String zipFilePath = "C:\\Users\\nmwork\\Desktop\\ruoyi1212121.zip"; // 替换为你的zip文件路径
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                System.out.println(entry.getName());
            }
        } catch (ZipException e) {
            System.err.println("这不是一个有效的 ZIP 文件");
        } catch (IOException e) {
            System.err.println("无法读取 ZIP 文件");
        }
    }

}
