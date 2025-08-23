package ru.practicum.explorewithme.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.EventState;
import ru.practicum.explorewithme.model.Location;
import ru.practicum.explorewithme.model.ParticipationRequest;
import ru.practicum.explorewithme.model.RequestStatus;
import ru.practicum.explorewithme.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testFindAllByRequesterId() {
        User user = createUser();
        Event event = createEvent();

        ParticipationRequest request1 = createRequest(user, event, RequestStatus.CONFIRMED);
        ParticipationRequest request2 = createRequest(user, event, RequestStatus.PENDING);
        requestRepository.saveAll(List.of(request1, request2));

        List<ParticipationRequest> result = requestRepository.findAllByRequesterId(user.getId());

        assertEquals(2, result.size());
    }

    @Test
    void testFindAllByEventId() {
        User user1 = createUser("user1@email.com");
        User user2 = createUser("user2@email.com");
        Event event = createEvent();

        ParticipationRequest request1 = createRequest(user1, event, RequestStatus.PENDING);
        ParticipationRequest request2 = createRequest(user2, event, RequestStatus.CONFIRMED);
        requestRepository.saveAll(List.of(request1, request2));

        List<ParticipationRequest> result = requestRepository.findAllByEventId(event.getId());

        assertEquals(2, result.size());
    }

    @Test
    void testFindAllByIdIn() {
        User user = createUser();
        Event event = createEvent();

        ParticipationRequest request1 = createRequest(user, event, RequestStatus.PENDING);
        ParticipationRequest request2 = createRequest(user, event, RequestStatus.CONFIRMED);
        List<ParticipationRequest> saved = requestRepository.saveAll(List.of(request1, request2));

        List<Long> requestIds = List.of(saved.get(0).getId(), saved.get(1).getId());

        List<ParticipationRequest> result = requestRepository.findAllByIdIn(requestIds);

        assertEquals(2, result.size());
    }

    @Test
    void testCountConfirmedRequestsByEventId() {
        User user1 = createUser("user1@email.com");
        User user2 = createUser("user2@email.com");
        Event event = createEvent();

        ParticipationRequest request1 = createRequest(user1, event, RequestStatus.CONFIRMED);
        ParticipationRequest request2 = createRequest(user2, event, RequestStatus.CONFIRMED);
        ParticipationRequest request3 = createRequest(
                createUser("user3@email.com"),
                event,
                RequestStatus.PENDING
        );
        requestRepository.saveAll(List.of(request1, request2, request3));

        Long count = requestRepository.countConfirmedRequestsByEventId(event.getId());

        assertEquals(2L, count);
    }

    @Test
    void testExistsByRequesterIdAndEventId() {
        User user = createUser();
        Event event = createEvent();
        ParticipationRequest request = createRequest(user, event, RequestStatus.PENDING);
        requestRepository.save(request);

        boolean exists = requestRepository.existsByRequesterIdAndEventId(user.getId(), event.getId());
        boolean notExists = requestRepository.existsByRequesterIdAndEventId(999L, event.getId());

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testFindByIdAndRequesterId() {
        User user = createUser();
        Event event = createEvent();
        ParticipationRequest request = createRequest(user, event, RequestStatus.PENDING);
        ParticipationRequest saved = requestRepository.save(request);

        Optional<ParticipationRequest> found = requestRepository.findByIdAndRequesterId(saved.getId(), user.getId());
        Optional<ParticipationRequest> notFound = requestRepository.findByIdAndRequesterId(saved.getId(), 999L);

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertFalse(notFound.isPresent());
    }

    @Test
    void testSaveAndDelete() {
        User user = createUser();
        Event event = createEvent();
        ParticipationRequest request = createRequest(user, event, RequestStatus.PENDING);

        ParticipationRequest saved = requestRepository.save(request);
        requestRepository.deleteById(saved.getId());

        assertFalse(requestRepository.existsById(saved.getId()));
    }

    private User createUser() {
        return createUser("test@email.com");
    }

    private User createUser(String email) {
        User user = new User();
        user.setName("Test User");
        user.setEmail(email);
        return userRepository.save(user);
    }

    private Event createEvent() {
        User user = createUser("event_owner@email.com");
        Category category = createCategory();

        Event event = new Event();
        event.setTitle("Test Event");
        event.setAnnotation("Test Annotation");
        event.setDescription("Test Description");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setLocation(new Location(55.7558f, 37.6176f));
        event.setPaid(false);
        event.setParticipantLimit(10);
        event.setRequestModeration(true);
        event.setState(EventState.PUBLISHED);
        event.setInitiator(user);
        event.setCategory(category);
        return eventRepository.save(event);
    }

    private Category createCategory() {
        Category category = new Category();
        category.setName("Test Category");
        return categoryRepository.save(category);
    }

    private ParticipationRequest createRequest(User user, Event event, RequestStatus status) {
        ParticipationRequest request = new ParticipationRequest();
        request.setRequester(user);
        request.setEvent(event);
        request.setStatus(status);
        request.setCreated(LocalDateTime.now());
        return request;
    }
}