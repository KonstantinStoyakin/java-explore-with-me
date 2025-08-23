package ru.practicum.explorewithme.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParticipationRequestTest {

    @Test
    void testParticipationRequest() {
        User requester = new User(1L, "User", "user@example.com");
        Event event = Event.builder().id(1L).title("Event").build();

        ParticipationRequest request = new ParticipationRequest();
        request.setId(1L);
        request.setEvent(event);
        request.setRequester(requester);
        request.setStatus(RequestStatus.PENDING);

        assertThat(request.getId()).isEqualTo(1L);
        assertThat(request.getEvent()).isEqualTo(event);
        assertThat(request.getRequester()).isEqualTo(requester);
        assertThat(request.getStatus()).isEqualTo(RequestStatus.PENDING);

        ParticipationRequest built = ParticipationRequest.builder()
                .id(2L)
                .event(event)
                .requester(requester)
                .status(RequestStatus.CONFIRMED)
                .build();

        assertThat(built.getId()).isEqualTo(2L);
        assertThat(built.getStatus()).isEqualTo(RequestStatus.CONFIRMED);
    }
}
