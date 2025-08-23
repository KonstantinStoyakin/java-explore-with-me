package ru.practicum.explorewithme.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.dto.NewCategoryDto;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.CategoryMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void addCategory_shouldCreateNewCategory() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("Test Category");
        Category category = new Category(1L, "Test Category");
        CategoryDto expectedDto = new CategoryDto(1L, "Test Category");

        when(categoryRepository.existsByName("Test Category")).thenReturn(false);
        when(categoryMapper.toCategory(newCategoryDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toCategoryDto(category)).thenReturn(expectedDto);

        CategoryDto result = categoryService.addCategory(newCategoryDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(categoryRepository).existsByName("Test Category");
        verify(categoryRepository).save(category);
    }

    @Test
    void addCategory_shouldThrowConflictExceptionWhenNameExists() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("Existing Category");

        when(categoryRepository.existsByName("Existing Category")).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.addCategory(newCategoryDto));
        verify(categoryRepository).existsByName("Existing Category");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_shouldDeleteCategory() {
        Long catId = 1L;

        when(eventRepository.existsByCategoryId(catId)).thenReturn(false);
        doNothing().when(categoryRepository).deleteById(catId);

        assertDoesNotThrow(() -> categoryService.deleteCategory(catId));
        verify(eventRepository).existsByCategoryId(catId);
        verify(categoryRepository).deleteById(catId);
    }

    @Test
    void deleteCategory_shouldThrowConflictExceptionWhenCategoryHasEvents() {
        Long catId = 1L;

        when(eventRepository.existsByCategoryId(catId)).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.deleteCategory(catId));
        verify(eventRepository).existsByCategoryId(catId);
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void updateCategory_shouldUpdateCategory() {
        Long catId = 1L;
        CategoryDto categoryDto = new CategoryDto(catId, "Updated Category");
        Category existingCategory = new Category(catId, "Old Category");
        Category updatedCategory = new Category(catId, "Updated Category");
        CategoryDto expectedDto = new CategoryDto(catId, "Updated Category");

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName("Updated Category")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(categoryMapper.toCategoryDto(updatedCategory)).thenReturn(expectedDto);

        CategoryDto result = categoryService.updateCategory(catId, categoryDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        assertEquals("Updated Category", existingCategory.getName());
        verify(categoryRepository).findById(catId);
        verify(categoryRepository).existsByName("Updated Category");
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void updateCategory_shouldThrowNotFoundExceptionWhenCategoryNotFound() {
        Long catId = 1L;
        CategoryDto categoryDto = new CategoryDto(catId, "Updated Category");

        when(categoryRepository.findById(catId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.updateCategory(catId, categoryDto));
        verify(categoryRepository).findById(catId);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_shouldThrowConflictExceptionWhenNameExists() {
        Long catId = 1L;
        CategoryDto categoryDto = new CategoryDto(catId, "Existing Category");
        Category existingCategory = new Category(catId, "Old Category");

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName("Existing Category")).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.updateCategory(catId, categoryDto));
        verify(categoryRepository).findById(catId);
        verify(categoryRepository).existsByName("Existing Category");
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_shouldNotCheckNameWhenNameNotChanged() {
        Long catId = 1L;
        CategoryDto categoryDto = new CategoryDto(catId, "Same Category");
        Category existingCategory = new Category(catId, "Same Category");
        CategoryDto expectedDto = new CategoryDto(catId, "Same Category");

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(existingCategory);
        when(categoryMapper.toCategoryDto(existingCategory)).thenReturn(expectedDto);

        CategoryDto result = categoryService.updateCategory(catId, categoryDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(categoryRepository).findById(catId);
        verify(categoryRepository, never()).existsByName(any());
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void getCategories_shouldReturnCategories() {
        Integer from = 0;
        Integer size = 10;
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by("id").descending());
        Category category1 = new Category(1L, "Category 1");
        Category category2 = new Category(2L, "Category 2");
        Page<Category> categoryPage = new PageImpl<>(List.of(category1, category2));
        CategoryDto dto1 = new CategoryDto(1L, "Category 1");
        CategoryDto dto2 = new CategoryDto(2L, "Category 2");

        when(categoryRepository.findAll(pageRequest)).thenReturn(categoryPage);
        when(categoryMapper.toCategoryDto(category1)).thenReturn(dto1);
        when(categoryMapper.toCategoryDto(category2)).thenReturn(dto2);

        List<CategoryDto> result = categoryService.getCategories(from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
        verify(categoryRepository).findAll(pageRequest);
    }

    @Test
    void getCategories_shouldHandlePagination() {
        Integer from = 10;
        Integer size = 5;
        PageRequest pageRequest = PageRequest.of(2, size, Sort.by("id").descending());
        Page<Category> categoryPage = new PageImpl<>(List.of());

        when(categoryRepository.findAll(pageRequest)).thenReturn(categoryPage);

        List<CategoryDto> result = categoryService.getCategories(from, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository).findAll(pageRequest);
    }

    @Test
    void getCategory_shouldReturnCategory() {
        Long catId = 1L;
        Category category = new Category(catId, "Test Category");
        CategoryDto expectedDto = new CategoryDto(catId, "Test Category");

        when(categoryRepository.findById(catId)).thenReturn(Optional.of(category));
        when(categoryMapper.toCategoryDto(category)).thenReturn(expectedDto);

        CategoryDto result = categoryService.getCategory(catId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(categoryRepository).findById(catId);
    }

    @Test
    void getCategory_shouldThrowNotFoundException() {
        Long catId = 1L;

        when(categoryRepository.findById(catId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.getCategory(catId));
        verify(categoryRepository).findById(catId);
    }
}