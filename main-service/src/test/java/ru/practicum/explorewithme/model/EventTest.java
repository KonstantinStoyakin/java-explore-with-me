package ru.practicum.explorewithme.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    void testEvent() {
        Category category = new Category(1L, "Category");
        User user = new User(1L, "User", "user@example.com");
        Location location = new Location(10f, 20f);

        Event event = new Event();
        event.setId(1L);
        event.setAnnotation("Annotation");
        event.setCategory(category);
        event.setConfirmedRequests(5);
        event.setDescription("Description");
        event.setEventDate(LocalDateTime.now());
        event.setInitiator(user);
        event.setLocation(location);
        event.setPaid(true);
        event.setParticipantLimit(100);
        event.setRequestModeration(true);
        event.setState(EventState.PENDING);
        event.setTitle("EventTitle");
        event.setViews(50L);

        assertThat(event.getId()).isEqualTo(1L);
        assertThat(event.getCategory()).isEqualTo(category);
        assertThat(event.getInitiator()).isEqualTo(user);
        assertThat(event.getLocation()).isEqualTo(location);
        assertThat(event.getTitle()).isEqualTo("EventTitle");
        assertThat(event.getState()).isEqualTo(EventState.PENDING);

        Event built = Event.builder()
                .id(2L)
                .annotation("Anno")
                .category(category)
                .confirmedRequests(10)
                .description("Desc")
                .eventDate(LocalDateTime.now())
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(50)
                .requestModeration(false)
                .state(EventState.PUBLISHED)
                .title("BuiltEvent")
                .views(5L)
                .build();

        assertThat(built.getId()).isEqualTo(2L);
        assertThat(built.getTitle()).isEqualTo("BuiltEvent");
        assertThat(built.getState()).isEqualTo(EventState.PUBLISHED);
    }
}
