import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipUtilityUtil {

    public static void unzip(String zipFilePath, String destDir) throws IOException {
        FileInputStream fis = new FileInputStream(zipFilePath);
        ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry ze = zis.getNextEntry();

        byte[] buffer = new byte[1024];
        int length;

        while (ze != null) {
            String fileName = ze.getName();
            FileOutputStream fos = new FileOutputStream(destDir + File.separator + fileName);
            while ((length = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.close();
            zis.closeEntry();
            ze = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
        fis.close();
    }

    public static void main(String[] args) {
        try {
            unzip("/path/to/your/zipfile.zip", "/path/to/destination");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
