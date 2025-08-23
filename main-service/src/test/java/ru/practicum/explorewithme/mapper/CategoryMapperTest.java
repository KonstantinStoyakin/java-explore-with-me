package ru.practicum.explorewithme.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.dto.NewCategoryDto;
import ru.practicum.explorewithme.model.Category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CategoryMapperTest {

    private final CategoryMapper mapper = new CategoryMapper();

    @Test
    void toCategory_shouldMapNewCategoryDtoToCategory() {
        NewCategoryDto newDto = new NewCategoryDto("Music");
        Category category = mapper.toCategory(newDto);

        assertNotNull(category);
        assertEquals("Music", category.getName());
    }

    @Test
    void toCategoryDto_shouldMapCategoryToCategoryDto() {
        Category category = Category.builder().id(1L).name("Sports").build();
        CategoryDto dto = mapper.toCategoryDto(category);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Sports", dto.getName());
    }
}
