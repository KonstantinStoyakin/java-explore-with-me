package ru.practicum.explorewithme.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testExistsByEmail() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("test@email.com");
        boolean notExists = userRepository.existsByEmail("nonexistent@email.com");

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testFindAllByIdIn() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@email.com");
        User saved1 = userRepository.save(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@email.com");
        User saved2 = userRepository.save(user2);

        User user3 = new User();
        user3.setName("User 3");
        user3.setEmail("user3@email.com");
        userRepository.save(user3);

        Pageable pageable = PageRequest.of(0, 10);

        List<User> result = userRepository.findAllByIdIn(
                List.of(saved1.getId(), saved2.getId()),
                pageable
        );

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("user1@email.com")));
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("user2@email.com")));
        assertFalse(result.stream().anyMatch(u -> u.getEmail().equals("user3@email.com")));
    }

    @Test
    void testSaveAndFindById() {
        User user = new User();
        user.setName("New User");
        user.setEmail("new@email.com");

        User saved = userRepository.save(user);
        User found = userRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals("New User", found.getName());
        assertEquals("new@email.com", found.getEmail());
    }

    @Test
    void testFindAll() {
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@email.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@email.com");
        userRepository.save(user2);

        List<User> all = userRepository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void testDelete() {
        User user = new User();
        user.setName("To Delete");
        user.setEmail("delete@email.com");
        User saved = userRepository.save(user);

        userRepository.deleteById(saved.getId());

        assertFalse(userRepository.existsById(saved.getId()));
    }

    @Test
    void testFindAllByIdInWithPagination() {
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setName("User " + i);
            user.setEmail("user" + i + "@email.com");
            userRepository.save(user);
        }

        List<User> allUsers = userRepository.findAll();
        List<Long> allIds = allUsers.stream().map(User::getId).toList();

        Pageable firstPage = PageRequest.of(0, 2);
        Pageable secondPage = PageRequest.of(1, 2);

        List<User> firstPageResult = userRepository.findAllByIdIn(allIds, firstPage);
        List<User> secondPageResult = userRepository.findAllByIdIn(allIds, secondPage);

        assertEquals(2, firstPageResult.size());
        assertEquals(2, secondPageResult.size());
    }
}