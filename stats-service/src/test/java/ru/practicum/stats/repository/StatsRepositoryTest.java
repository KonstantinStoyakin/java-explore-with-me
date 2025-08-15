package ru.practicum.stats.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Sql(scripts = "classpath:test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class StatsRepositoryTest {

    @Autowired
    private StatsRepository repository;

    @Test
    void getStats_shouldReturnCorrectCount() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 2, 0, 0);

        List<ViewStats> result = repository.getStats(start, end, List.of("/events/1"));

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getHits());
    }

    @Test
    void getUniqueStats_shouldReturnUniqueCount() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 2, 0, 0);

        List<ViewStats> result = repository.getUniqueStats(start, end, List.of("/events/1"));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getHits());
    }
}
