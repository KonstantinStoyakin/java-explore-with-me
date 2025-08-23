package ru.practicum.stats.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.EndpointHit;
import ru.practicum.stats.model.Stats;

@Component
public class StatsMapper {
    public Stats toStats(EndpointHit endpointHit) {
        return Stats.builder()
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }
}