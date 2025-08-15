package ru.practicum.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StatsClient {
    private final RestTemplate rest;

    private final String serverUrl;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClient(String serverUrl, RestTemplate restTemplate) {
        this.serverUrl = serverUrl;
        this.rest = restTemplate;
    }

    public StatsClient(String serverUrl) {
        this(serverUrl, new RestTemplate());
    }

    public void addHit(EndpointHit hit) {
        post("/hit", hit);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, @Nullable List<String> uris,
                                    boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(FORMATTER));
        parameters.put("end", end.format(FORMATTER));
        parameters.put("unique", unique);

        String path = "/stats?start={start}&end={end}&unique={unique}";

        if (uris != null && !uris.isEmpty()) {
            parameters.put("uris", String.join(",", uris));
            path += "&uris={uris}";
        }

        ResponseEntity<ViewStats[]> response = get(path, parameters);
        return response.getStatusCode().is2xxSuccessful() ? Arrays.
                asList(Objects.requireNonNull(response.getBody())) : Collections.emptyList();
    }

    private ResponseEntity<ViewStats[]> get(String path, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    private void post(String path, EndpointHit body) {
        makeAndSendRequest(HttpMethod.POST, path, Collections.emptyMap(), body);
    }

    private <T> ResponseEntity<T> makeAndSendRequest(HttpMethod method, String path, Map<String, Object> parameters,
                                                     @Nullable EndpointHit body) {
        HttpEntity<EndpointHit> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<T> response;
        try {
            response = rest.exchange(serverUrl + path, method, requestEntity,
                    (Class<T>) (body == null ? ViewStats[].class : Void.class), parameters);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
        return prepareResponse(response);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static <T> ResponseEntity<T> prepareResponse(ResponseEntity<T> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }
        return ResponseEntity.status(response.getStatusCode()).build();
    }
}
