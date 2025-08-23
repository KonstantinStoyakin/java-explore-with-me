package ru.practicum.explorewithme.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.ParticipationRequest;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.model.RequestStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RequestMapperTest {

    private final RequestMapper mapper = new RequestMapper();

    @Test
    void toParticipationRequestDto_shouldMapParticipationRequest() {
        User user = new User();
        user.setId(5L);

        Event event = new Event();
        event.setId(10L);

        ParticipationRequest request = new ParticipationRequest();
        request.setId(1L);
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);
        request.setStatus(RequestStatus.PENDING);

        ParticipationRequestDto dto = mapper.toParticipationRequestDto(request);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getEvent());
        assertEquals(5L, dto.getRequester());
        assertEquals(RequestStatus.PENDING, dto.getStatus());
        assertNotNull(dto.getCreated());
    }
}
