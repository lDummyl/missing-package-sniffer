package web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.Dependency;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class OkkClient {

    private static final MediaType MEDIA_TYPE = MediaType.get("application/x-java-archive");
    private final String artifactoryHost;
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String apiKey;
    public Dependency dependency;

    @SneakyThrows
    public OkkClient(String props) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(props);
        Properties properties = new Properties();
        properties.load(resourceAsStream);
        apiKey = properties.getProperty("artifactory.apikey");
        artifactoryHost = properties.getProperty("artifactory.host");
    }

    @SneakyThrows
    public Map uploadArtifact(Path artifactPath) {
        File file = artifactPath.toFile();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(
                                file,
                                MEDIA_TYPE)).build();

        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put("X-JFrog-Art-Api", apiKey);
        Headers of = Headers.of(headersMap);
        Request request = new Request.Builder()
                .headers(of)
                .url(artifactoryHost + "artifactory/ui/artifact/upload")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String string = response.body().string();
            return (Map) mapper.readValue(string, Object.class);
        }
    }

    @SneakyThrows
    public Map customizeParameters(Map payload, Path artifactPath) {

        String groupIdTag = "groupId";
        String versionTag = "version";
        Map unitInfo = (Map) payload.get("unitInfo");
        String pomXml = (String) payload.get("unitConfigFileContent");
        String artifactId = (String) unitInfo.get("artifactId");
        String groupId = (String) unitInfo.get(groupIdTag);
        String version = (String) unitInfo.get(versionTag);

        if (groupId.equals(artifactId)) {
            String replacement = "custom.lib";
            unitInfo.put(groupIdTag, replacement);
            pomXml = getReplacedPomTags(pomXml, groupId, replacement, groupIdTag);
            groupId = replacement;
        }
        if (version.equals(artifactId)) {
            String replacement = "1.0.0";
            pomXml = getReplacedPomTags(pomXml, version, replacement, versionTag);
            unitInfo.put(versionTag, "1.0.0");
            version = replacement;
        }
        payload.put("unitConfigFileContent", pomXml);
        payload.put("action", "deploy");
        payload.put("fileName", artifactPath.toFile().getName());
        payload.put("repoKey", "Betoola-local-release");
        payload.put("publishUnitConfigFile", "true");

        String correctPath = String.format("%s/%s/%s/%s-%s.jar",
                groupId.replace(".", "/"), artifactId, version, artifactId, version);
        unitInfo.put("path", correctPath); // TODO: 3/12/20 add jar war recognition
        unitInfo.put("prettyArtifactId", String.format("%s:%s:%s:jar",
                groupId, artifactId, version));
        dependency = new Dependency(groupId,artifactId,version);
        return payload;
    }

    @NotNull
    private String getReplacedPomTags(String pomXml, String toReplace, String replacement, String tag) {
        return pomXml.replace(String.format("<" + tag + ">%s</" + tag + ">", toReplace), String.format("<" + tag + ">%s</" + tag + ">", replacement));
    }


    @SneakyThrows
    public void deployArtifact(Map payload, Path artifactPath) {

        HashMap<String, String> headersMap = new HashMap<>();
        headersMap.put("X-JFrog-Art-Api", apiKey);
        Headers of = Headers.of(headersMap);
        RequestBody requestBody = RequestBody.create(mapper.writeValueAsString(payload), MediaType.get("application/json"));
        Request request = new Request.Builder()
                .headers(of)
                .url(artifactoryHost + "artifactory/ui/artifact/deploy")
                .post(requestBody)
                .build();
        String s = request.toString();
        System.out.println("s = " + s);

        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String string = response.body().string();
            Map<String, Map> map = (Map<String, Map>) mapper.readValue(string, Object.class);
            System.out.println("map = " + map);
        }
    }
}

