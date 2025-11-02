package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.model.Comment;
import ru.practicum.explorewithme.model.CommentStatus;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEventIdAndStatus(Long eventId, CommentStatus status, Pageable pageable);

    List<Comment> findAllByAuthorId(Long userId, Pageable pageable);

    Optional<Comment> findByIdAndAuthorId(Long commentId, Long userId);

    boolean existsByIdAndAuthorId(Long commentId, Long userId);

    @Query("SELECT c FROM Comment c WHERE c.status = 'PENDING'")
    Page<Comment> findPendingComments(Pageable pageable);
}