package disk;

import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

import static design.Brush.purple;
import static design.Brush.red;
import static design.Brush.yellow;


public class Zipper {

    public static final String TEMP_FOLDER = "./data/temp";
    public static final String RESULT_FOLDER = "./data/results";
    public static final String BUFFER_PATH = "./data/buffer";
    public static final String MISSING_PACKAGES_TXT = BUFFER_PATH+ "/missing-packages.txt";



    static List<String> archExt = Arrays.asList("jar", "war", "zip", "rar");
    static Set<Path> trace = new HashSet<>();
    static Set<Path> writtenAlreadyToResults = new HashSet<>();
    static Set<String> remains = new HashSet<>();

    private static List<String> split;

    public static String colorFound(String entryName) {
        String result = entryName;
        for (String s : split) {
            if (result.contains(s)) {
                remains.remove(s);
            }
            result = result.replace(s, yellow(s));
        }
        return result;
    }

    @SneakyThrows
    public void parse(String rootFolder) {
        Paths.get(TEMP_FOLDER).toFile().mkdirs();
        Paths.get(RESULT_FOLDER).toFile().delete();
        split = Files.lines(Paths.get(MISSING_PACKAGES_TXT)).map(i -> i.replace(".", "/")).collect(Collectors.toList());
        remains = new HashSet<>(split);
        Files.walk(Paths.get(rootFolder)).forEach(this::lookInto);
        String result = remains.isEmpty() ? "FOUND ALL" : "Remains:";
        System.out.println(red("\n\n" + result + "\n\n"));
        remains.forEach(System.out::println);
        System.out.println(red("\n\nZipper finished!\n\n"));
    }

    @SneakyThrows
    private void lookInto(Path path) {
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
                if (!writtenAlreadyToResults.contains(path1)) {
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

    @SneakyThrows
    private Path extractToTemp(ZipInputStream zin, String entryName) {
        return extractTo(zin, entryName, TEMP_FOLDER);
    }

    @SneakyThrows
    private Path extractToResult(ZipInputStream zin, String entryName) {
        return extractTo(zin, entryName, RESULT_FOLDER);
    }

    @SneakyThrows
    private Path extractTo(ZipInputStream zin, String entryName, String folder) {
        File file = Paths.get(folder, entryName).toFile();
        file.getParentFile().mkdirs();
        if (file.exists()) return file.toPath();
        @Cleanup OutputStream out = new FileOutputStream(folder + "/" + entryName);
        byte[] buffer = new byte[9000];
        int len;
        while ((len = zin.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.close();
        return file.toPath();
    }
}
