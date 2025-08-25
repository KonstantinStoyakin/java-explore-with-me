package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    Page<Category> findAll(Pageable pageable);
}
