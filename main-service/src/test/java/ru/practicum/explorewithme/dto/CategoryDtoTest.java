package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryDtoTest {

    private final Validator validator;

    public CategoryDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testCategoryDtoBuilder() {
        CategoryDto category = CategoryDto.builder()
                .id(1L)
                .name("Test Category")
                .build();
        assertEquals(1L, category.getId());
        assertEquals("Test Category", category.getName());
    }

    @Test
    void testCategoryDtoNoArgsConstructor() {
        CategoryDto category = new CategoryDto();
        assertNull(category.getId());
        assertNull(category.getName());
    }

    @Test
    void testCategoryDtoAllArgsConstructor() {
        CategoryDto category = new CategoryDto(1L, "Test Category");
        assertEquals(1L, category.getId());
        assertEquals("Test Category", category.getName());
    }

    @Test
    void testCategoryDtoValidationValid() {
        CategoryDto category = new CategoryDto(1L, "Valid Name");
        Set<ConstraintViolation<CategoryDto>> violations = validator.validate(category);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testCategoryDtoValidationBlankName() {
        CategoryDto category = new CategoryDto(1L, "   ");
        Set<ConstraintViolation<CategoryDto>> violations = validator.validate(category);
        assertEquals(1, violations.size());
        assertEquals(jakarta.validation.constraints.NotBlank.class,
                violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testCategoryDtoValidationNullName() {
        CategoryDto category = new CategoryDto(1L, null);
        Set<ConstraintViolation<CategoryDto>> violations = validator.validate(category);
        assertEquals(1, violations.size());
        assertEquals(jakarta.validation.constraints.NotBlank.class,
                violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testCategoryDtoValidationNameTooLong() {
        String longName = "A".repeat(51);
        CategoryDto category = new CategoryDto(1L, longName);
        Set<ConstraintViolation<CategoryDto>> violations = validator.validate(category);
        assertEquals(1, violations.size());
        assertEquals(jakarta.validation.constraints.Size.class,
                violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testCategoryDtoSettersAndGetters() {
        CategoryDto category = new CategoryDto();
        category.setId(2L);
        category.setName("New Category");
        assertEquals(2L, category.getId());
        assertEquals("New Category", category.getName());
    }

    @Test
    void testCategoryDtoEqualsAndHashCode() {
        CategoryDto category1 = new CategoryDto(1L, "Category");
        CategoryDto category2 = new CategoryDto(1L, "Category");
        CategoryDto category3 = new CategoryDto(2L, "Different");
        assertEquals(category1, category2);
        assertNotEquals(category1, category3);
        assertEquals(category1.hashCode(), category2.hashCode());
    }

    @Test
    void testCategoryDtoToString() {
        CategoryDto category = new CategoryDto(1L, "Test");
        String toString = category.toString();
        assertTrue(toString.contains("Test"));
        assertTrue(toString.contains("1"));
    }
}
