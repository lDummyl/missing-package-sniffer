import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MavenLogReader {

    static String absolutePathToLogFile = "/home/ludovd/win/Documents and Settings/mi/Desktop/WORK/Imola Informatica/scrigno-admin-maven/loggy.txt";
    static String resultFile = "./src/main/resources/buffer/missing-packages.txt";


    @SneakyThrows
    public static void main(String[] args)  {

//        Runtime runtime = Runtime.getRuntime();
//        Process exec = runtime.exec("mvn clean package -f \"/home/ludovd/win/Documents and Settings/mi/Desktop/WORK/Imola Informatica/scrigno-admin-maven/pom.xml");
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
//        int exit = exec.waitFor();
//        System.out.println(exit);
//        bufferedReader.lines().forEach(System.out::println);

        File resultDump = Paths.get(resultFile).toFile();
        resultDump.getParentFile().mkdirs();
        @Cleanup FileWriter fileWriter = new FileWriter(resultDump);

        List<String> strings = Files.readAllLines(Paths.get(absolutePathToLogFile));
        List<String> filtred = strings.stream().filter(s -> s.contains("ERROR")).filter(s -> s.contains(" does not exist")).collect(Collectors.toList());
        Pattern pattern = Pattern.compile("package (.*) does not exist");

        Map<String, Long> occurances = filtred.stream().map(s -> {
            Matcher matcher = pattern.matcher(s);
            return matcher.find() ? matcher.group(1) : null;
        }).filter(Objects::nonNull).collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        List<Map.Entry<String, Long>> toSort = new ArrayList<>();
        for (Map.Entry<String, Long> e : occurances.entrySet()) {
            toSort.add(e);
        }
        toSort.sort(Map.Entry.comparingByKey());
        for (Map.Entry<String, Long> e : toSort) {
//            System.out.println(e.getKey());
            fileWriter.write(e.getKey() + "\n");
            System.out.println(formatted(e));
        }
        fileWriter.flush();
        long sum = occurances.values().stream().mapToLong(i -> i).sum();
        System.out.print("sum = \u001B[35m " + sum);
        Zipper.parse();
    }

    private static String formatted(Map.Entry<String, Long> e) {
        return e.getKey() + spaces(e.getKey().length()) + " = " + e.getValue();
    }

    private static String spaces(int length) {
        return IntStream.range(0, 60 - length).mapToObj(i -> " ").collect(Collectors.joining());
    }
}
