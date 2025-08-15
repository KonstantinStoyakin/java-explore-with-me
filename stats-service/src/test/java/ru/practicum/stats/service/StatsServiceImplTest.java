package ru.practicum.stats.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.ViewStats;
import ru.practicum.stats.mapper.StatsMapper;
import ru.practicum.stats.model.Stats;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    @Mock
    private StatsRepository repository;

    @Mock
    private StatsMapper mapper;

    @InjectMocks
    private StatsServiceImpl service;

    @Test
    void addHit_shouldSaveEntity() {
        EndpointHit hit = new EndpointHit(null, "app", "/uri", "192.168.1.1", LocalDateTime.now());
        Stats stats = new Stats();

        when(mapper.toStats(hit)).thenReturn(stats);

        service.addHit(hit);

        verify(repository).save(stats);
    }

    @Test
    void getStats_whenUniqueFalse_shouldCallCorrectMethod() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<ViewStats> expected = List.of(new ViewStats("app", "/uri", 10L));

        when(repository.getStats(start, end, null)).thenReturn(expected);

        List<ViewStats> result = service.getStats(start, end, null, false);

        assertEquals(expected, result);
    }

    @Test
    void getStats_whenUniqueTrue_shouldCallCorrectMethod() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        List<ViewStats> expected = List.of(new ViewStats("app", "/uri", 5L));

        when(repository.getUniqueStats(start, end, null)).thenReturn(expected);

        List<ViewStats> result = service.getStats(start, end, null, true);

        assertEquals(expected, result);
    }
}