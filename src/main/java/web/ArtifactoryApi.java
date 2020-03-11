package web;

import feign.Body;
import feign.Headers;
import feign.Param;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "articlient", url = "https://toolchain.imolinfo.it/artifactory" , configuration = Conf.class)
public interface ArtifactoryApi {

    @RequestMapping(method = RequestMethod.POST, value = "/ui/artifact/upload")
//    @Headers({"Content-Type: multipart/form-data",
//            "X-JFrog-Art-API: AKCp5ekT4Ba1jS4BNxcFW873HLMnb4ZfofhubrTBmnSDQKnybffUb8HC2JjKuZjT3FEfT"})
    @Headers({"Accept: */*",
            "Accept-Encoding: gzip, deflate, br",
            "Accept-Language: ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7",
            "Connection: keep-alive",
            "Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryfrQVtQsTwkZ5zTB9",
//            "X-JFrog-Art-API: AKCp5ekT4Ba1jS4BNxcFW873HLMnb4ZfofhubrTBmnSDQKnybffUb8HC2JjKuZjT3FEfT",
//            "Cookie: SESSION=423a5fee-0611-4391-b8ae-1e739542f403; experimentation_subject_id=IjFhMzliODJkLWJjOWEtNGRhZC05YWY5LWU4NWExNTcyOTM5OSI%3D--c8f647ba2082e125969e52d1d95bc0de66d681b9\n" +
            "Host: toolchain.imolinfo.it",
            "Origin: https://toolchain.imolinfo.it",
            "Referer: https://toolchain.imolinfo.it/artifactory/webapp/",
            "Sec-Fetch-Mode: cors",
            "Sec-Fetch-Site: same-origin",
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36",
            "X-ARTIFACTORY-REPOTYPE: Maven",
            "X-Requested-With: artUI"})
    String sendArtifact(@Param("file") byte[] file);

}