package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class EventRequestStatusUpdateResultTest {

    @Test
    void testEventRequestStatusUpdateResultNoArgsConstructor() {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        assertNull(result.getConfirmedRequests());
        assertNull(result.getRejectedRequests());
    }

    @Test
    void testEventRequestStatusUpdateResultAllArgsConstructor() {
        ParticipationRequestDto request = new ParticipationRequestDto();
        List<ParticipationRequestDto> confirmed = List.of(request);
        List<ParticipationRequestDto> rejected = List.of();

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(
                confirmed, rejected
        );

        assertEquals(confirmed, result.getConfirmedRequests());
        assertEquals(rejected, result.getRejectedRequests());
    }

    @Test
    void testEventRequestStatusUpdateResultSettersAndGetters() {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        ParticipationRequestDto request1 = new ParticipationRequestDto();
        ParticipationRequestDto request2 = new ParticipationRequestDto();
        List<ParticipationRequestDto> confirmed = List.of(request1);
        List<ParticipationRequestDto> rejected = List.of(request2);

        result.setConfirmedRequests(confirmed);
        result.setRejectedRequests(rejected);

        assertEquals(confirmed, result.getConfirmedRequests());
        assertEquals(rejected, result.getRejectedRequests());
    }

    @Test
    void testEventRequestStatusUpdateResultEqualsAndHashCode() {
        ParticipationRequestDto request = new ParticipationRequestDto();
        List<ParticipationRequestDto> list1 = List.of(request);
        List<ParticipationRequestDto> list2 = List.of(request);

        EventRequestStatusUpdateResult res1 = new EventRequestStatusUpdateResult(list1, List.of());
        EventRequestStatusUpdateResult res2 = new EventRequestStatusUpdateResult(list2, List.of());
        EventRequestStatusUpdateResult res3 = new EventRequestStatusUpdateResult(List.of(), list1);

        assertEquals(res1, res2);
        assertNotEquals(res1, res3);
        assertEquals(res1.hashCode(), res2.hashCode());
    }

    @Test
    void testEventRequestStatusUpdateResultToString() {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(
                List.of(), List.of()
        );

        String toString = result.toString();
        assertNotNull(toString);
    }
}