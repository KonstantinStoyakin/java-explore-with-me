package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventRequestStatusUpdateRequestTest {

    @Test
    void testEventRequestStatusUpdateRequestNoArgsConstructor() {
        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();

        assertNull(request.getRequestIds());
        assertNull(request.getStatus());
    }

    @Test
    void testEventRequestStatusUpdateRequestAllArgsConstructor() {
        List<Long> requestIds = List.of(1L, 2L, 3L);

        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest(
                requestIds, "CONFIRMED"
        );

        assertEquals(requestIds, request.getRequestIds());
        assertEquals("CONFIRMED", request.getStatus());
    }

    @Test
    void testEventRequestStatusUpdateRequestSettersAndGetters() {
        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest();
        List<Long> requestIds = List.of(4L, 5L);

        request.setRequestIds(requestIds);
        request.setStatus("REJECTED");

        assertEquals(requestIds, request.getRequestIds());
        assertEquals("REJECTED", request.getStatus());
    }

    @Test
    void testEventRequestStatusUpdateRequestEqualsAndHashCode() {
        List<Long> ids1 = List.of(1L, 2L);
        List<Long> ids2 = List.of(1L, 2L);

        EventRequestStatusUpdateRequest req1 = new EventRequestStatusUpdateRequest(ids1, "CONFIRMED");
        EventRequestStatusUpdateRequest req2 = new EventRequestStatusUpdateRequest(ids2, "CONFIRMED");
        EventRequestStatusUpdateRequest req3 = new EventRequestStatusUpdateRequest(ids1, "REJECTED");

        assertEquals(req1, req2);
        assertNotEquals(req1, req3);
        assertEquals(req1.hashCode(), req2.hashCode());
    }

    @Test
    void testEventRequestStatusUpdateRequestToString() {
        EventRequestStatusUpdateRequest request = new EventRequestStatusUpdateRequest(
                List.of(1L), "TEST"
        );

        String toString = request.toString();
        assertTrue(toString.contains("TEST"));
        assertTrue(toString.contains("1"));
    }
}