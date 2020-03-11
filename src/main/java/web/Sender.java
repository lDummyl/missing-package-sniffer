package web;

import lombok.SneakyThrows;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Service
public class Sender {

    String path1 = "/home/ludovd/win/Documents and Settings/mi/Desktop/WORK/Imola Informatica/missing-package-sniffer/src/main/resources/results/CensimentoAutomatico-1.0.2.jar";
    String path = "/home/ludovd/win/Documents and Settings/mi/Desktop/WORK/Imola Informatica/missing-package-sniffer/src/main/resources/results/cxf-stub-condizioniere-1.1.0.0.jar";


    ArtifactoryApi artifactoryApi;

    public Sender(ArtifactoryApi artifactoryApi) {
        this.artifactoryApi = artifactoryApi;
    }

    @SneakyThrows
    @PostConstruct
    public void init(){
        File file = Paths.get(path).toFile();

        DiskFileItem fileItem = (DiskFileItem) new DiskFileItemFactory().createItem("file",
                MediaType.TEXT_PLAIN_VALUE, true, file.getName());

        try (InputStream input = new FileInputStream(file); OutputStream os = fileItem.getOutputStream()) {
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }


        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String s = artifactoryApi.sendArtifact(multipartFile);
        System.out.println(s);
    }
}
