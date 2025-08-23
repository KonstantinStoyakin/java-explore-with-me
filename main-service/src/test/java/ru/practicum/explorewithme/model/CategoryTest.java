package ru.practicum.explorewithme.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {

    @Test
    void testCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("TestCategory");

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("TestCategory");

        Category built = Category.builder()
                .id(2L)
                .name("BuiltCategory")
                .build();

        assertThat(built.getId()).isEqualTo(2L);
        assertThat(built.getName()).isEqualTo("BuiltCategory");
    }
}
