package com.example.bankfake.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class NameService {
    private static final String BTN_URL =
        "https://www.behindthename.com/api/random.php"
      + "?key=%s"
      + "&usage=ara-eg"      // Arabic (Egypt)
      + "&number=1"          // one name at a time
      + "&format=json";      // JSON output

    private final String apiKey;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public NameService(@Value("${behindthename.key}") String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Returns a single random Egyptian‐Arabic name
     * falling back to a Faker Arabic name if something goes wrong.
     */
    public String randomEgyptianName() {
        String url = String.format(BTN_URL, apiKey);
        try {
            HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
            HttpResponse<String> resp = client.send(
                req,
                HttpResponse.BodyHandlers.ofString()
            );
            JsonNode root = mapper.readTree(resp.body());
            JsonNode names = root.path("random").path("names");
            if (names.isArray() && names.size() > 0) {
                return names.get(0).asText();
            }
        } catch (Exception e) {
            // log.warn("BTN lookup failed, falling back", e);
        }
        // fallback—you could also inject Faker here if you like
        return "اسمي غير معروف";
    }
}
