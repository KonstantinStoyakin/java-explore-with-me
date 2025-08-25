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

class NewCategoryDtoTest {

    private final Validator validator;

    public NewCategoryDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNewCategoryDtoBuilder() {
        NewCategoryDto category = NewCategoryDto.builder().name("Test Category").build();
        assertEquals("Test Category", category.getName());
    }

    @Test
    void testNewCategoryDtoNoArgsConstructor() {
        NewCategoryDto category = new NewCategoryDto();
        assertNull(category.getName());
    }

    @Test
    void testNewCategoryDtoAllArgsConstructor() {
        NewCategoryDto category = new NewCategoryDto("Test Category");
        assertEquals("Test Category", category.getName());
    }

    @Test
    void testNewCategoryDtoValidationValid() {
        NewCategoryDto category = new NewCategoryDto("Valid Name");
        Set<ConstraintViolation<NewCategoryDto>> violations = validator.validate(category);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNewCategoryDtoValidationBlankName() {
        NewCategoryDto category = new NewCategoryDto("   ");
        Set<ConstraintViolation<NewCategoryDto>> violations = validator.validate(category);
        assertEquals(1, violations.size());
        assertEquals(jakarta.validation.constraints.NotBlank.class,
                violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testNewCategoryDtoValidationNullName() {
        NewCategoryDto category = new NewCategoryDto(null);
        Set<ConstraintViolation<NewCategoryDto>> violations = validator.validate(category);
        assertEquals(1, violations.size());
        assertEquals(jakarta.validation.constraints.NotBlank.class,
                violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testNewCategoryDtoValidationNameTooLong() {
        String longName = "A".repeat(51);
        NewCategoryDto category = new NewCategoryDto(longName);
        Set<ConstraintViolation<NewCategoryDto>> violations = validator.validate(category);
        assertEquals(1, violations.size());
        assertEquals(jakarta.validation.constraints.Size.class,
                violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testNewCategoryDtoSettersAndGetters() {
        NewCategoryDto category = new NewCategoryDto();
        category.setName("New Category");
        assertEquals("New Category", category.getName());
    }

    @Test
    void testNewCategoryDtoEqualsAndHashCode() {
        NewCategoryDto category1 = new NewCategoryDto("Category");
        NewCategoryDto category2 = new NewCategoryDto("Category");
        NewCategoryDto category3 = new NewCategoryDto("Different");
        assertEquals(category1, category2);
        assertNotEquals(category1, category3);
        assertEquals(category1.hashCode(), category2.hashCode());
    }

    @Test
    void testNewCategoryDtoToString() {
        NewCategoryDto category = new NewCategoryDto("Test");
        assertTrue(category.toString().contains("Test"));
    }
}
