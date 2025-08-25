package ru.practicum.explorewithme.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.explorewithme.dto.NewEventDto;
import ru.practicum.explorewithme.dto.UpdateEventAdminRequest;
import ru.practicum.explorewithme.dto.UpdateEventUserRequest;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.EventState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventMapperTest {

    private EventMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EventMapper(new CategoryMapper(), new UserMapper());
    }

    @Test
    void toEventFullDto_shouldMapEvent() {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Title");
        event.setState(EventState.PENDING);

        Category category = new Category();
        category.setId(100L);
        category.setName("Music");
        event.setCategory(category);

        var dto = mapper.toEventFullDto(event);

        assertEquals(1L, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("PENDING", dto.getState());
        assertEquals(100L, dto.getCategory().getId());
    }

    @Test
    void toEventShortDto_shouldMapEvent() {
        Event event = new Event();
        event.setId(2L);
        event.setTitle("Short title");

        Category category = new Category();
        category.setId(101L);
        category.setName("Sports");
        event.setCategory(category);

        var dto = mapper.toEventShortDto(event);

        assertEquals(2L, dto.getId());
        assertEquals("Short title", dto.getTitle());
        assertEquals(101L, dto.getCategory().getId());
    }

    @Test
    void toEvent_shouldMapNewEventDto() {
        NewEventDto dto = new NewEventDto();
        dto.setTitle("New Event");
        dto.setParticipantLimit(5);
        Event event = mapper.toEvent(dto);

        assertEquals("New Event", event.getTitle());
        assertEquals(5, event.getParticipantLimit());
    }

    @Test
    void toEvent_shouldThrowValidationException() {
        NewEventDto dto = new NewEventDto();
        dto.setParticipantLimit(-1);
        assertThrows(ValidationException.class, () -> mapper.toEvent(dto));
    }

    @Test
    void updateEventFromAdminRequest_shouldUpdateFieldsAndChangeState() {
        Event event = new Event();
        event.setState(EventState.PENDING);

        UpdateEventAdminRequest request = new UpdateEventAdminRequest();
        request.setTitle("Admin title");
        request.setStateAction("PUBLISH_EVENT");

        mapper.updateEventFromAdminRequest(request, event);

        assertEquals("Admin title", event.getTitle());
        assertEquals(EventState.PUBLISHED, event.getState());
        assertNotNull(event.getPublishedOn());
    }

    @Test
    void updateEventFromUserRequest_shouldUpdateFieldsAndChangeState() {
        Event event = new Event();
        UpdateEventUserRequest request = new UpdateEventUserRequest();
        request.setTitle("User title");
        request.setStateAction("CANCEL_REVIEW");

        mapper.updateEventFromUserRequest(request, event);

        assertEquals("User title", event.getTitle());
        assertEquals(EventState.CANCELED, event.getState());
    }
}
