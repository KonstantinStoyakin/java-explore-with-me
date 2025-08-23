package ru.practicum.explorewithme.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.model.Category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testExistsByName() {
        Category category = new Category();
        category.setName("Test Category");
        categoryRepository.save(category);

        boolean exists = categoryRepository.existsByName("Test Category");
        boolean notExists = categoryRepository.existsByName("Non-existent Category");

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testFindAllWithPageable() {
        Category category1 = new Category();
        category1.setName("Category 1");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Category 2");
        categoryRepository.save(category2);

        Pageable pageable = PageRequest.of(0, 10);

        var result = categoryRepository.findAll(pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream()
                .anyMatch(c -> c.getName().equals("Category 1")));
        assertTrue(result.getContent().stream()
                .anyMatch(c -> c.getName().equals("Category 2")));
    }

    @Test
    void testSaveAndFindById() {
        Category category = new Category();
        category.setName("New Category");

        Category saved = categoryRepository.save(category);
        Category found = categoryRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals("New Category", found.getName());
    }

    @Test
    void testDelete() {
        Category category = new Category();
        category.setName("To Delete");
        Category saved = categoryRepository.save(category);

        categoryRepository.deleteById(saved.getId());

        assertFalse(categoryRepository.existsById(saved.getId()));
    }
}