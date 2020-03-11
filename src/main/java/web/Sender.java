package web;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class Sender {

    String path = "/home/ludovd/win/Documents and Settings/mi/Desktop/WORK/Imola Informatica/missing-package-sniffer/src/main/resources/results/CensimentoAutomatico-1.0.2.jar";


    ArtifactoryApi artifactoryApi;

    public Sender(ArtifactoryApi artifactoryApi) {
        this.artifactoryApi = artifactoryApi;
    }

    @SneakyThrows
    @PostConstruct
    public void init(){
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String s = artifactoryApi.sendArtifact(bytes);
        System.out.println(s);
    }
}
