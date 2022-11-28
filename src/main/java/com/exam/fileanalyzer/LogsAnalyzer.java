package com.exam.fileanalyzer;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class LogsAnalyzer {

    private final static String regex = "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})] \"(\\S+) (\\S+)\\s*(\\S+)?\\s*\" (\\d{3}) (\\S+)";
    private final static String date = "dd/MM/yyyy:HH:mm:ss xxxx";

    /**
     * Given a zip file, a search query, and a date range, count the number of
     * occurrences of the search query in each file in the zip file
     *
     * @param searchQuery  The string to search for in the file.
     * @param zipFile      The zip file to search in.
     * @param startDate    The start date of the search.
     * @param numberOfDays The number of days to search for.
     * @return A map of file names and the number of occurrences of the search
     * query in the file.
     * @throws java.io.IOException if will be problems
     */

    public Map<String, Integer> countEntriesInZipFile(String searchQuery,
                                                      File zipFile,
                                                      LocalDate startDate,
                                                      Integer numberOfDays)
            throws IOException {
        // Creating a regular expression for the records
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        ZipFile extractedZipFile = new ZipFile(zipFile.getAbsolutePath());
        return extractedZipFile.stream()
                .map(next -> aggregateMap(searchQuery,
                        startDate,
                        numberOfDays,
                        pattern,
                        extractedZipFile,
                        next))
                .filter(e -> e.size() > 0)
                .collect(Collectors.toMap(
                        stringIntegerMap -> new ArrayList<>(stringIntegerMap.keySet()).get(0),
                        stringIntegerMap -> new ArrayList<>(stringIntegerMap.values()).get(0)));

    }


    @SneakyThrows(IOException.class)
    private Map<String, Integer> aggregateMap(String searchQuery,
                                              LocalDate startDate,
                                              Integer numberOfDays,
                                              Pattern pattern,
                                              ZipFile extractedZipFile,
                                              ZipEntry next) {
        Map<String, Integer> result = new HashMap<>();
        Matcher matcher = pattern.matcher(
                new String(
                        extractedZipFile
                                .getInputStream(next)
                                .readAllBytes()));
        int c = 0;
        while (matcher.find()) {
            LocalDate currentDate = LocalDate
                    .parse(matcher.group(4),
                            DateTimeFormatter.ofPattern(date));

            if ((currentDate.isAfter(startDate) || currentDate.isEqual(startDate))
                    && (currentDate.isBefore(startDate.plusDays(numberOfDays)) || currentDate.isEqual(startDate.plusDays(numberOfDays)))
            ) {
                if (matcher.group(6).equals(searchQuery)) {
                    c++;
                }
                result.put(next.getName(), c);
            }
        }
        return result;
    }
}
