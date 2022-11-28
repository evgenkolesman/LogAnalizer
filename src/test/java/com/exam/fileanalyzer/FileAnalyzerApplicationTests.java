package com.exam.fileanalyzer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class FileAnalyzerApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testFileParse() throws IOException {
        var searchQuery = "/blog/tags/puppet?flav=rss20";
        var resourceLocation = "classpath:logs-27_02_2018-03_03_2018.zip";
        var zipFile = ResourceUtils.getFile(resourceLocation);
        var startDate = LocalDate.of(2018, 2, 27);
        var numbersOfDays = Integer.valueOf("1");

        var result = new LogsAnalyzer().countEntriesInZipFile(searchQuery, zipFile,
                startDate, numbersOfDays);
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put("logs_2018-02-27-access.log", 2);
        stringIntegerHashMap.put("logs_2018-02-28-access.log", 0);

        assertThat(result).isEqualTo(stringIntegerHashMap);

        for (var fileName : result.keySet()) {
            System.out.println("fileName: " + fileName + " count: " + result.get(fileName));
        }

    }
}
