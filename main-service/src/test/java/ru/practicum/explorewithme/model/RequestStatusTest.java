package ru.practicum.explorewithme.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestStatusTest {

    @Test
    void testRequestStatusValues() {
        RequestStatus[] statuses = RequestStatus.values();
        assertThat(statuses).contains(RequestStatus.PENDING, RequestStatus.CONFIRMED,
                RequestStatus.REJECTED, RequestStatus.CANCELED);
    }
}
