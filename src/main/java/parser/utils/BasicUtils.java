package parser.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BasicUtils {
    public static List<String> getInputInfo() {
        List<String> result = new ArrayList<>();
        String fileName = "src\\main\\resources\\input.txt";
        try {
            result = Files.readAllLines(Paths.get(fileName),
                    Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void saveTextToFile(String text, String fName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fName));
        writer.write(text);
        writer.close();
    }
}
