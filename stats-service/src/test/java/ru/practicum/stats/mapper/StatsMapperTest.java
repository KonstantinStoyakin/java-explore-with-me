package ru.practicum.stats.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.dto.EndpointHit;
import ru.practicum.stats.model.Stats;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatsMapperTest {

    private final StatsMapper mapper = new StatsMapper();

    @Test
    void toStats_shouldMapCorrectly() {
        EndpointHit hit = new EndpointHit(1L, "app", "/uri", "192.168.1.1", LocalDateTime.now());

        Stats stats = mapper.toStats(hit);

        assertEquals(hit.getApp(), stats.getApp());
        assertEquals(hit.getUri(), stats.getUri());
        assertEquals(hit.getIp(), stats.getIp());
        assertEquals(hit.getTimestamp(), stats.getTimestamp());
    }
}
