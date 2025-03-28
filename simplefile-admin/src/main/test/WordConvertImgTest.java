import com.aspose.words.Document;
import com.aspose.words.SaveFormat;

import java.io.FileInputStream;

public class WordConvertImgTest {

    public static void main(String [] args){
        try(
                FileInputStream fileInputStream = new FileInputStream("C:\\Users\\nmwork\\Downloads\\区县平台监管端数据对接-v1.00-20241012.docx");
        ) {
            Document document = new Document(fileInputStream);
            // 只转第一页，如果有多页，这里得循环一下
            Document page = document.extractPages(4, 1);
            page.save("C:\\Users\\nmwork\\Downloads\\12.png", SaveFormat.PNG);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
