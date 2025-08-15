package ru.practicum.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsClientTest {

    @Mock
    private RestTemplate restTemplate;

    private StatsClient statsClient;
    private final String serverUrl = "http://localhost:9090";

    @BeforeEach
    void setUp() {
        statsClient = new StatsClient(serverUrl, restTemplate);
    }

    @Test
    void addHit_ShouldSendPostRequest() {
        EndpointHit hit = new EndpointHit();
        hit.setApp("test-app");
        hit.setUri("/test");
        hit.setIp("127.0.0.1");
        hit.setTimestamp(LocalDateTime.now());

        ResponseEntity<Void> responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        when(restTemplate.exchange(
                eq(serverUrl + "/hit"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class),
                eq(Collections.emptyMap())
        )).thenReturn(responseEntity);

        statsClient.addHit(hit);

        verify(restTemplate, times(1)).exchange(
                eq(serverUrl + "/hit"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Void.class),
                eq(Collections.emptyMap())
        );
    }

    @Test
    void getStats_ShouldReturnStatsList() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now();
        List<String> uris = List.of("/test1", "/test2");
        boolean unique = true;

        ViewStats[] statsArray = {
                new ViewStats("app1", "/test1", 10L),
                new ViewStats("app2", "/test2", 5L)
        };

        ResponseEntity<ViewStats[]> responseEntity = new ResponseEntity<>(statsArray, HttpStatus.OK);
        when(restTemplate.exchange(
                startsWith(serverUrl + "/stats?start="),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(ViewStats[].class),
                anyMap()
        )).thenReturn(responseEntity);

        List<ViewStats> result = statsClient.getStats(start, end, uris, unique);

        assertEquals(2, result.size());
        assertEquals("/test1", result.get(0).getUri());
        assertEquals(10L, result.get(0).getHits());
        assertEquals("/test2", result.get(1).getUri());
        assertEquals(5L, result.get(1).getHits());
    }

    @Test
    void getStats_WithoutUris_ShouldReturnStatsList() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now();
        boolean unique = false;

        ViewStats[] statsArray = {
                new ViewStats("app1", "/test1", 15L),
                new ViewStats("app2", "/test2", 8L)
        };

        ResponseEntity<ViewStats[]> responseEntity = new ResponseEntity<>(statsArray, HttpStatus.OK);
        when(restTemplate.exchange(
                startsWith(serverUrl + "/stats?start="),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(ViewStats[].class),
                anyMap()
        )).thenReturn(responseEntity);

        List<ViewStats> result = statsClient.getStats(start, end, null, unique);

        assertEquals(2, result.size());
    }

    @Test
    void getStats_WhenErrorOccurs_ShouldReturnEmptyList() {
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now();

        HttpStatusCodeException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(ViewStats[].class),
                anyMap()
        )).thenThrow(exception);

        List<ViewStats> result = statsClient.getStats(start, end, Collections.emptyList(), false);

        assertTrue(result.isEmpty());
    }
}