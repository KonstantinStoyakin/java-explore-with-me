package ru.practicum.explorewithme.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.model.Compilation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CompilationRepositoryTest {

    @Autowired
    private CompilationRepository compilationRepository;

    @Test
    void testFindAllByPinned() {
        Compilation compilation1 = new Compilation();
        compilation1.setPinned(true);
        compilation1.setTitle("Pinned Compilation");
        compilationRepository.save(compilation1);

        Compilation compilation2 = new Compilation();
        compilation2.setPinned(false);
        compilation2.setTitle("Not Pinned Compilation");
        compilationRepository.save(compilation2);

        Pageable pageable = PageRequest.of(0, 10);

        List<Compilation> pinned = compilationRepository.findAllByPinned(true, pageable);
        List<Compilation> notPinned = compilationRepository.findAllByPinned(false, pageable);

        assertEquals(1, pinned.size());
        assertEquals("Pinned Compilation", pinned.get(0).getTitle());

        assertEquals(1, notPinned.size());
        assertEquals("Not Pinned Compilation", notPinned.get(0).getTitle());
    }

    @Test
    void testSaveAndFindById() {
        Compilation compilation = new Compilation();
        compilation.setTitle("Test Compilation");
        compilation.setPinned(true);

        Compilation saved = compilationRepository.save(compilation);
        Compilation found = compilationRepository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals("Test Compilation", found.getTitle());
        assertTrue(found.getPinned());
    }

    @Test
    void testFindAll() {
        Compilation compilation1 = new Compilation();
        compilation1.setTitle("Compilation 1");
        compilation1.setPinned(true);
        compilationRepository.save(compilation1);

        Compilation compilation2 = new Compilation();
        compilation2.setTitle("Compilation 2");
        compilation2.setPinned(false);
        compilationRepository.save(compilation2);

        List<Compilation> all = compilationRepository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void testDelete() {
        Compilation compilation = new Compilation();
        compilation.setTitle("To Delete");
        compilation.setPinned(false);
        Compilation saved = compilationRepository.save(compilation);

        compilationRepository.deleteById(saved.getId());

        assertFalse(compilationRepository.existsById(saved.getId()));
    }
}