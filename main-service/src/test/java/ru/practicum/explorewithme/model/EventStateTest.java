package ru.practicum.explorewithme.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventStateTest {

    @Test
    void testEventStateValues() {
        EventState[] states = EventState.values();
        assertThat(states).contains(EventState.PENDING, EventState.PUBLISHED, EventState.CANCELED);
    }
}
