package web;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.nio.file.Paths;

public class OkkClient {

    private static final MediaType MEDIA_TYPE = MediaType.get("application/x-java-archive");
    private final OkHttpClient client = new OkHttpClient();
    String path = "/home/ludovd/win/Documents and Settings/mi/Desktop/WORK/Imola Informatica/missing-package-sniffer/src/main/resources/results/cxf-stub-condizioniere-1.1.0.0.jar";


    public static void main(String... args) throws Exception {
        new OkkClient().run();
    }

    public void run() throws Exception {
        // Use the imgur image upload API as documented at https://api.imgur.com/endpoints/image
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "CensimentoAutomatico-1.0.2.jar",
                        RequestBody.create(
                                Paths.get(path).toFile(),
                                MEDIA_TYPE))
                .build();

        Headers of = Headers.of("Accept"," */*",
                "Accept-Encoding"," gzip, deflate, br",
                "Accept-Language"," ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7",
                "Connection"," keep-alive",
                "Content-Type"," multipart/form-data; boundary=----WebKitFormBoundaryfrQVtQsTwkZ5zTB9",
                "X-JFrog-Art-Api"," AKCp5ekT4Ba1jS4BNxcFW873HLMnb4ZfofhubrTBmnSDQKnybffUb8HC2JjKuZjT3FEfT",
//            "Cookie"," SESSION=423a5fee-0611-4391-b8ae-1e739542f403; experimentation_subject_id=IjFhMzliODJkLWJjOWEtNGRhZC05YWY5LWU4NWExNTcyOTM5OSI%3D--c8f647ba2082e125969e52d1d95bc0de66d681b9\n" +
                "Host"," toolchain.imolinfo.it",
                "Origin"," https://toolchain.imolinfo.it",
                "Referer"," https://toolchain.imolinfo.it/artifactory/webapp/",
                "Sec-Fetch-Mode"," cors",
                "Sec-Fetch-Site"," same-origin",
                "User-Agent"," Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36",
                "X-ARTIFACTORY-REPOTYPE"," Maven",
                "X-Requested-With"," artUI");
        Request request = new Request.Builder()
                .headers(of)
                .url("https://toolchain.imolinfo.it/artifactory/ui/artifact/upload")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            System.out.println(response.body().string());
        }
    }
}