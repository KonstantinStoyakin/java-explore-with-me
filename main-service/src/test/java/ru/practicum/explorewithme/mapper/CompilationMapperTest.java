package ru.practicum.explorewithme.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.explorewithme.dto.CompilationDto;
import ru.practicum.explorewithme.dto.EventShortDto;
import ru.practicum.explorewithme.dto.NewCompilationDto;
import ru.practicum.explorewithme.dto.UpdateCompilationRequest;
import ru.practicum.explorewithme.model.Compilation;
import ru.practicum.explorewithme.model.Event;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompilationMapperTest {

    private CompilationMapper mapper;

    @BeforeEach
    void setUp() {
        EventMapper stubEventMapper = new EventMapper(new CategoryMapper(), new UserMapper());
        mapper = new CompilationMapper(stubEventMapper);
    }

    @Test
    void toCompilation_shouldMapNewCompilationDto() {
        NewCompilationDto dto = new NewCompilationDto(List.of(1L, 2L), true, "My compilation");
        Compilation compilation = mapper.toCompilation(dto);

        assertNotNull(compilation);
        assertEquals("My compilation", compilation.getTitle());
        assertTrue(compilation.getPinned());
        assertEquals(2, compilation.getEvents().size());
    }

    @Test
    void toCompilationDto_shouldMapCompilationToDto() {
        Compilation compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("Test");
        compilation.setPinned(false);

        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(2L);

        compilation.setEvents(Set.of(event1, event2));

        EventMapper stubEventMapper = new EventMapper(new CategoryMapper(), new UserMapper()) {
            @Override
            public EventShortDto toEventShortDto(Event event) {
                EventShortDto dto = new EventShortDto();
                dto.setId(event.getId());
                return dto;
            }
        };
        CompilationMapper mapper = new CompilationMapper(stubEventMapper);

        CompilationDto dto = mapper.toCompilationDto(compilation);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test", dto.getTitle());
        assertFalse(dto.getPinned());
        assertEquals(2, dto.getEvents().size());
        assertTrue(dto.getEvents().stream().anyMatch(e -> e.getId().equals(1L)));
        assertTrue(dto.getEvents().stream().anyMatch(e -> e.getId().equals(2L)));
    }


    @Test
    void updateCompilationFromRequest_shouldUpdateCompilation() {
        Compilation compilation = new Compilation();
        UpdateCompilationRequest request = new UpdateCompilationRequest(List.of(3L), true, "Updated title");

        mapper.updateCompilationFromRequest(request, compilation);

        assertEquals("Updated title", compilation.getTitle());
        assertTrue(compilation.getPinned());
        assertEquals(1, compilation.getEvents().size());
        assertEquals(3L, compilation.getEvents().iterator().next().getId());
    }
}
