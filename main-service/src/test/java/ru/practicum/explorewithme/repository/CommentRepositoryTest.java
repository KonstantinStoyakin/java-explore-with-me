package ru.practicum.explorewithme.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.explorewithme.model.Comment;
import ru.practicum.explorewithme.model.CommentStatus;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.EventState;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.model.Category;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    private User user;
    private Event event;
    private Comment comment;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        this.user = entityManager.persist(user);

        Category category = new Category();
        category.setName("Test Category");
        Category savedCategory = entityManager.persist(category);

        Event event = new Event();
        event.setAnnotation("Test Annotation");
        event.setCategory(savedCategory);
        event.setDescription("Test Description");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setInitiator(this.user);
        event.setPaid(false);
        event.setParticipantLimit(10);
        event.setRequestModeration(true);
        event.setTitle("Test Event");
        event.setState(EventState.PUBLISHED);
        this.event = entityManager.persist(event);

        Comment comment = new Comment();
        comment.setText("Test comment text");
        comment.setEvent(this.event);
        comment.setAuthor(this.user);
        comment.setStatus(CommentStatus.PENDING);
        this.comment = entityManager.persist(comment);
    }

    @Test
    void findAllByEventIdAndStatus_shouldReturnCommentsForEventAndStatus() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdOn").descending());
        List<Comment> comments = commentRepository.findAllByEventIdAndStatus(
                event.getId(), CommentStatus.PENDING, pageable
        );

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(comment.getId(), comments.get(0).getId());
    }

    @Test
    void findAllByEventIdAndStatus_shouldReturnEmptyListForNonExistingEvent() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Comment> comments = commentRepository.findAllByEventIdAndStatus(
                999L, CommentStatus.PENDING, pageable
        );

        assertNotNull(comments);
        assertTrue(comments.isEmpty());
    }

    @Test
    void findAllByAuthorId_shouldReturnCommentsForUser() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdOn").descending());
        List<Comment> comments = commentRepository.findAllByAuthorId(user.getId(), pageable);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(comment.getId(), comments.get(0).getId());
    }

    @Test
    void findAllByAuthorId_shouldReturnEmptyListForNonExistingUser() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Comment> comments = commentRepository.findAllByAuthorId(999L, pageable);

        assertNotNull(comments);
        assertTrue(comments.isEmpty());
    }

    @Test
    void findByIdAndAuthorId_shouldReturnCommentWhenExists() {
        Optional<Comment> foundComment = commentRepository.findByIdAndAuthorId(
                comment.getId(), user.getId()
        );

        assertTrue(foundComment.isPresent());
        assertEquals(comment.getId(), foundComment.get().getId());
    }

    @Test
    void findByIdAndAuthorId_shouldReturnEmptyForNonExistingComment() {
        Optional<Comment> foundComment = commentRepository.findByIdAndAuthorId(999L, user.getId());

        assertFalse(foundComment.isPresent());
    }

    @Test
    void findByIdAndAuthorId_shouldReturnEmptyForNonExistingUser() {
        Optional<Comment> foundComment = commentRepository.findByIdAndAuthorId(comment.getId(), 999L);

        assertFalse(foundComment.isPresent());
    }

    @Test
    void existsByIdAndAuthorId_shouldReturnTrueWhenExists() {
        boolean exists = commentRepository.existsByIdAndAuthorId(comment.getId(), user.getId());

        assertTrue(exists);
    }

    @Test
    void existsByIdAndAuthorId_shouldReturnFalseWhenNotExists() {
        boolean exists = commentRepository.existsByIdAndAuthorId(999L, user.getId());

        assertFalse(exists);
    }

    @Test
    void findPendingComments_shouldReturnPendingComments() {
        Comment publishedComment = new Comment();
        publishedComment.setText("Published comment");
        publishedComment.setEvent(event);
        publishedComment.setAuthor(user);
        publishedComment.setStatus(CommentStatus.PUBLISHED);
        entityManager.persist(publishedComment);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdOn").ascending());
        List<Comment> pendingComments = commentRepository.findPendingComments(pageable).getContent();

        assertNotNull(pendingComments);
        assertEquals(1, pendingComments.size());
        assertEquals(CommentStatus.PENDING, pendingComments.get(0).getStatus());
    }

    @Test
    void save_shouldPersistComment() {
        Comment newComment = new Comment();
        newComment.setText("New test comment");
        newComment.setEvent(event);
        newComment.setAuthor(user);
        newComment.setStatus(CommentStatus.PENDING);

        Comment savedComment = commentRepository.save(newComment);

        assertNotNull(savedComment.getId());
        assertEquals("New test comment", savedComment.getText());
        assertEquals(event.getId(), savedComment.getEvent().getId());
        assertEquals(user.getId(), savedComment.getAuthor().getId());
    }

    @Test
    void delete_shouldRemoveComment() {
        commentRepository.deleteById(comment.getId());

        Optional<Comment> deletedComment = commentRepository.findById(comment.getId());
        assertFalse(deletedComment.isPresent());
    }
}