import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import disk.MavenLogReader;
import disk.Zipper;
import dto.Dependency;
import lombok.Cleanup;
import lombok.SneakyThrows;
import web.OkkClient;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static design.Brush.yellow;


public class Main {

    public static final String ROOT_FOLDER = "/home/ludovd/Downloads/scrignoadmin";
    public static final String ABSOLUTE_PATH_TO_PROJECT = "/home/ludovd/win/Documents and Settings/mi/Desktop/WORK/Imola Informatica/scrigno-admin-maven/";
    private static boolean startMaven = false;
    public static final List<Dependency> dependencies = new ArrayList<>();

    @SneakyThrows
    public static void main(String[] args) {
//        buildCycle(ABSOLUTE_PATH_TO_PROJECT);
        runDeployment(Paths.get("/home/ludovd/Downloads/lib (copy)"));
        writeDependencies();
    }

    private static void writeDependencies() throws IOException {
        XmlMapper mapper = new XmlMapper();
        @Cleanup FileWriter fileWriter = new FileWriter(Paths.get(Zipper.BUFFER_PATH, "forPom.xml").toFile());
        for (Dependency dependency : dependencies) {
            fileWriter.append(mapper.writeValueAsString(dependency)+"\n");
            fileWriter.flush();
        }
    }

    @SneakyThrows
    public static void buildCycle(String pathToProject) {
        MavenLogReader mavenLogReader = new MavenLogReader();
        mavenLogReader.launch(startMaven, pathToProject);
        Zipper zipper = new Zipper();
        zipper.parse(ROOT_FOLDER);
        runDeployment(Paths.get(Zipper.RESULT_FOLDER));
    }

    @SneakyThrows
    public static void runDeployment(Path artifactsRoot) {
        OkkClient okkClient = new OkkClient("sniffer.properties");
        List<Path> artifacts = Files.walk(artifactsRoot).filter(p -> p.toString().endsWith(".jar")).collect(Collectors.toList());
        System.out.println("Found following artifacts:\n");
        artifacts.forEach(i -> System.out.println(yellow(i.toFile().getName())));
        System.out.println("\nStart deploying?(y/n)");
        String ans = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if (ans.equalsIgnoreCase("y")) {
            for (Path path : artifacts) {
                Map response = okkClient.uploadArtifact(path);
                Map customizedPayload = okkClient.customizeParameters(response, path);
                dependencies.add(okkClient.dependency);
                okkClient.deployArtifact(customizedPayload, path);
            }
        }
    }
}
