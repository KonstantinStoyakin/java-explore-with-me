package ru.practicum.explorewithme.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.EventState;
import ru.practicum.explorewithme.model.Location;
import ru.practicum.explorewithme.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testIncrementViews() {
        User user = createUser();
        Category category = createCategory();
        Event event = createEvent(user, category, EventState.PUBLISHED);
        event.setViews(5L);
        Event saved = eventRepository.save(event);

        entityManager.flush();
        entityManager.clear();

        eventRepository.incrementViews(saved.getId());

        entityManager.flush();
        entityManager.clear();
        Event updated = eventRepository.findById(saved.getId()).orElse(null);

        assertNotNull(updated);
        assertEquals(6L, updated.getViews());
    }

    @Test
    void testFindAllByInitiatorId() {
        User user = createUser();
        Category category = createCategory();

        Event event1 = createEvent(user, category, EventState.PENDING);
        Event event2 = createEvent(user, category, EventState.PUBLISHED);
        eventRepository.saveAll(List.of(event1, event2));

        Pageable pageable = PageRequest.of(0, 10);

        List<Event> result = eventRepository.findAllByInitiatorId(user.getId(), pageable);

        assertEquals(2, result.size());
    }

    @Test
    void testFindByIdAndInitiatorId() {
        User user = createUser();
        Category category = createCategory();
        Event event = createEvent(user, category, EventState.PENDING);
        Event saved = eventRepository.save(event);

        Optional<Event> found = eventRepository.findByIdAndInitiatorId(saved.getId(), user.getId());
        Optional<Event> notFound = eventRepository.findByIdAndInitiatorId(saved.getId(), 999L);

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertFalse(notFound.isPresent());
    }

    @Test
    void testExistsByCategoryId() {
        User user = createUser();
        Category category = createCategory();
        Event event = createEvent(user, category, EventState.PENDING);
        eventRepository.save(event);

        boolean exists = eventRepository.existsByCategoryId(category.getId());
        boolean notExists = eventRepository.existsByCategoryId(999L);

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testFindByState() {
        User user = createUser();
        Category category = createCategory();

        Event event1 = createEvent(user, category, EventState.PUBLISHED);
        Event event2 = createEvent(user, category, EventState.CANCELED);
        eventRepository.saveAll(List.of(event1, event2));

        Pageable pageable = PageRequest.of(0, 10);

        var published = eventRepository.findByState(EventState.PUBLISHED, pageable);
        var canceled = eventRepository.findByState(EventState.CANCELED, pageable);

        assertEquals(1, published.getTotalElements());
        assertEquals(1, canceled.getTotalElements());
    }

    @Test
    void testFindByStateAndCategoryIdIn() {
        User user = createUser();
        Category category1 = createCategory("Category 1");
        Category category2 = createCategory("Category 2");

        Event event1 = createEvent(user, category1, EventState.PUBLISHED);
        Event event2 = createEvent(user, category2, EventState.PUBLISHED);
        eventRepository.saveAll(List.of(event1, event2));

        Pageable pageable = PageRequest.of(0, 10);

        var result = eventRepository.findByStateAndCategoryIdIn(
                EventState.PUBLISHED,
                List.of(category1.getId()),
                pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(event1.getId(), result.getContent().get(0).getId());
    }

    @Test
    void testExistsByIdAndInitiatorId() {
        User user = createUser();
        Category category = createCategory();
        Event event = createEvent(user, category, EventState.PENDING);
        Event saved = eventRepository.save(event);

        boolean exists = eventRepository.existsByIdAndInitiatorId(saved.getId(), user.getId());
        boolean notExists = eventRepository.existsByIdAndInitiatorId(saved.getId(), 999L);

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testFindByIdWithCategoryAndInitiator() {
        User user = createUser();
        Category category = createCategory();
        Event event = createEvent(user, category, EventState.PENDING);
        Event saved = eventRepository.save(event);

        Optional<Event> result = eventRepository.findByIdWithCategoryAndInitiator(saved.getId());

        assertTrue(result.isPresent());
        assertNotNull(result.get().getCategory());
        assertNotNull(result.get().getInitiator());
    }

    @Test
    void testFindByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween() {
        User user1 = createUser("user1@email.com");
        User user2 = createUser("user2@email.com");

        Category category1 = createCategory("Category 1");
        Category category2 = createCategory("Category 2");

        LocalDateTime now = LocalDateTime.now();

        Event event1 = createEvent(user1, category1, EventState.PUBLISHED);
        event1.setEventDate(now.plusDays(1));

        Event event2 = createEvent(user2, category2, EventState.PENDING);
        event2.setEventDate(now.plusDays(2));

        eventRepository.saveAll(List.of(event1, event2));

        Pageable pageable = PageRequest.of(0, 10);

        var result = eventRepository.findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(
                List.of(user1.getId()),
                List.of(EventState.PUBLISHED),
                List.of(category1.getId()),
                now,
                now.plusDays(3),
                pageable
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(event1.getId(), result.getContent().get(0).getId());
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

    private Category createCategory() {
        return createCategory("Test Category");
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return categoryRepository.save(category);
    }

    private Event createEvent(User user, Category category, EventState state) {
        Event event = new Event();
        event.setTitle("Test Event");
        event.setAnnotation("Test Annotation");
        event.setDescription("Test Description");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setLocation(new Location(55.7558f, 37.6176f));
        event.setPaid(false);
        event.setParticipantLimit(0);
        event.setRequestModeration(true);
        event.setState(state);
        event.setInitiator(user);
        event.setCategory(category);
        return event;
    }
}