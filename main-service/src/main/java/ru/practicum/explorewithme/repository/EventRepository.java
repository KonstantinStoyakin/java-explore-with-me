package ru.practicum.explorewithme.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Modifying
    @Query("UPDATE Event e SET e.views = COALESCE(e.views, 0) + 1 WHERE e.id = :id")
    @Transactional
    void incrementViews(@Param("id") Long id);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    boolean existsByCategoryId(Long categoryId);

    Page<Event> findByState(EventState state, Pageable pageable);

    Page<Event> findByStateAndCategoryIdIn(EventState state, List<Long> categoryIds, Pageable pageable);

    boolean existsByIdAndInitiatorId(Long eventId, Long userId);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.category LEFT JOIN FETCH e.initiator WHERE e.id = :id")
    Optional<Event> findByIdWithCategoryAndInitiator(@Param("id") Long id);

    @Query("SELECT e FROM Event e " +
            "WHERE (:userIds IS NULL OR e.initiator.id IN :userIds) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categoryIds IS NULL OR e.category.id IN :categoryIds) " +
            "AND e.eventDate BETWEEN COALESCE(:rangeStart, e.eventDate) AND COALESCE(:rangeEnd, e.eventDate)")
    Page<Event> findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(
            @Param("userIds") List<Long> userIds,
            @Param("states") List<EventState> states,
            @Param("categoryIds") List<Long> categoryIds,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);
}