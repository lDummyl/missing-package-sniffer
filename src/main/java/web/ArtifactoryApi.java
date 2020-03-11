package web;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "articlient", url = "https://toolchain.imolinfo.it/artifactory")
public interface ArtifactoryApi {

    @RequestMapping(method = RequestMethod.POST, value = "/ui/artifact/upload")
    @Headers({"Content-Type: multipart/form-data",
            "X-JFrog-Art-API: AKCp5ekT4Ba1jS4BNxcFW873HLMnb4ZfofhubrTBmnSDQKnybffUb8HC2JjKuZjT3FEfT"})
    String sendArtifact(byte[] file);

}