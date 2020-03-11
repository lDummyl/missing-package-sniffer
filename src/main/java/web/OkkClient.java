package web;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

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
                .addFormDataPart("file", "cxf-stub-condizioniere-1.1.0.0.jar",
                        RequestBody.create(
                                Paths.get(path).toFile(),
                                MEDIA_TYPE)).build();

        HashMap<String, String> headersMap = new HashMap<>();
                headersMap.put("Host"," toolchain.imolinfo.it");
                headersMap.put("Connection"," keep-alive");
                headersMap.put("Accept"," application/json, text/plain, */*");
                headersMap.put("X-Requested-With"," artUI");
                headersMap.put("serial"," 87");
                headersMap.put("Request-Agent"," artifactoryUI");
                headersMap.put(   "X-JFrog-Art-Api", " AKCp5ekTCjaFCJEcMqvgmN7jVhTGJqsnMKWTDjBUysotUhvXFnYDBzzVCU44ywrm9cSg2JvFC");
                headersMap.put("User-Agent"," Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
                headersMap.put("Sec-Fetch-Site"," same-origin");
                headersMap.put("Sec-Fetch-Mode"," cors");
                headersMap.put("Referer"," https://toolchain.imolinfo.it/artifactory/webapp/");
                headersMap.put("Accept-Encoding"," gzip, deflate, br");
                headersMap.put("Accept-Language"," ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7");
        Headers of = Headers.of(headersMap);
        Request request = new Request.Builder()
                .headers(of)
                .url("https://toolchain.imolinfo.it/artifactory/ui/artifact/upload")
                .post(requestBody)
                .build();
        String s = request.toString();
        System.out.println("s = " + s);

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            System.out.println(response.body().string());
        }
    }
}