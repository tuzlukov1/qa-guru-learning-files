package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import domain.Student;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.assertj.core.api.Assertions.assertThat;

public class MyFileParsingTest {

    @Test
    void parseZipTest() throws Exception {
        File file = new File("src/test/resources/files/files.zip");
        ZipFile zipFile = new ZipFile(file.getAbsolutePath());

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();

            switch (entry.getName()) {
                case "students.csv":
                    CSVReader reader = new CSVReader(new InputStreamReader(zipFile.getInputStream(entry)));
                {
                    List<String[]> content = reader.readAll();
                    assertThat(content.get(0)).contains(
                            "id",
                            "name",
                            "subname",
                            "age",
                            "gender",
                            "nationality");
                }
                break;

                case "file.xls": {
                    InputStream is = zipFile.getInputStream(entry);
                    XLS xls = new XLS(is);
                    assertThat(xls.excel
                            .getSheetAt(0)
                            .getRow(0)
                            .getCell(1)
                            .getStringCellValue()).contains("name");
                    break;
                }

                case "selenium-webdriver.pdf": {
                    InputStream is = zipFile.getInputStream(entry);
                    PDF pdf = new PDF(is);
                    assertThat(pdf.numberOfPages).isEqualTo(117);
                    break;
                }

                case "file.json": {
                    InputStream is = zipFile.getInputStream(entry);
                    ObjectMapper mapper = new ObjectMapper();
                    Student student = mapper.readValue(new InputStreamReader(is), Student.class);
                    assertThat(student.name).isEqualTo("Yan");
                    break;
                }
            }
        }
    }
}
