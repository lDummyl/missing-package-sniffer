import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zipper {

    public static final String ROOT_FOLDER = "/home/ludovd/Downloads/scrignoadmin";
    public static final String TEMP_FOLDER = "./src/main/resources/temp";
    public static final String RESULT_FOLDER = "./src/main/resources/results";
    public static final String BUFFER_PATH = "./src/main/resources/buffer/missing-packages.txt";

    public static final String PURP = "\u001B[35m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RESET = "\u001B[0m";
    static List<String> archExt = Arrays.asList("jar", "war", "zip", "rar");
    static Set<Path> trace = new HashSet<>();
    static Set<Path> writtenAlreadyToResults = new HashSet<>();
    static Set<String> remains = new HashSet<>();

    private static List<String> split;


    public static void main(String[] args) throws IOException {
        parse();
    }
    public static void parse() throws IOException {
        Paths.get(TEMP_FOLDER).toFile().mkdirs();
        Paths.get(RESULT_FOLDER).toFile().delete();
        split = Files.lines(Paths.get(BUFFER_PATH)).map(i -> i.replace(".", "/")).collect(Collectors.toList());
        remains = new HashSet<>(split);
        Files.walk(Paths.get(ROOT_FOLDER)).forEach(Zipper::lookInto);

        String result = remains.isEmpty() ? "FOUND ALL" : "Remains:";
        System.out.println(red("\n\n" + result + "\n\n"));
        remains.forEach(System.out::println);
    }

    @SneakyThrows
    private static void lookInto(Path path) {
        if (path.toFile().isDirectory()) return;

        FileInputStream inputStream = new FileInputStream(String.valueOf(path.toAbsolutePath()));
        ZipInputStream zis = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            final String entryName = zipEntry.getName();
//            System.out.println("entryName = " + entryName);
            if (split.stream().anyMatch(entryName::contains)) {
//            if (entryName.contains(SEARCH)) {
                trace.forEach(i -> System.out.println(purple("inside " + i)));
                trace.clear();
                Path relativize = Paths.get(TEMP_FOLDER).relativize(path);
                System.out.println("   " + relativize + purple(" in filename = ") + colorFound(entryName));
                Path path1 = Paths.get(RESULT_FOLDER, relativize.getFileName().toString());
                if (!writtenAlreadyToResults.contains(path1)){
                    path1.toFile().getParentFile().mkdirs();
                    Files.copy(path, path1, StandardCopyOption.REPLACE_EXISTING);
                    writtenAlreadyToResults.add(path1);
                }
            }
            File file = Paths.get(TEMP_FOLDER, entryName).toFile();
            if (!file.isDirectory()) {
                if (archExt.stream().anyMatch(entryName::endsWith)) {
                    trace.add(path);
                    Path tempPath = extractToTemp(zis, entryName);
                    lookInto(tempPath);
                }
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
        Paths.get(TEMP_FOLDER).toFile().delete();
    }

    private static String colorFound(String entryName) {
        String result = entryName;
        for (String s : split) {
            if (result.contains(s)) {
                remains.remove(s);
            }
            result = result.replace(s, yellow(s));
        }
        return result;
    }

    private static String purple(String a) {
        return PURP + a + RESET;
    }

    private static String yellow(String a) {
        return YELLOW + a + RESET;
    }

    private static String red(String a) {
        return RED + a + RESET;
    }

    @SneakyThrows
    private static Path extractToTemp(ZipInputStream zin, String entryName) {
        return extractTo(zin, entryName, TEMP_FOLDER);
    }
    @SneakyThrows
    private static Path extractToResult(ZipInputStream zin, String entryName) {
        return extractTo(zin, entryName, RESULT_FOLDER);
    }
    @SneakyThrows
    private static Path extractTo(ZipInputStream zin, String entryName, String folder) {
        File file = Paths.get(folder, entryName).toFile();
        file.getParentFile().mkdirs();
        if (file.exists())return file.toPath();
        @Cleanup OutputStream out = new FileOutputStream(folder + "/" + entryName);
        byte[] buffer = new byte[9000];
        int len;
        while (( len = zin.read(buffer)) != -1) {
               out.write(buffer, 0, len);
        }
        out.close();
        return file.toPath();
    }
}
