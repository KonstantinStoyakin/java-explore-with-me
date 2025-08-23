package ru.practicum.explorewithme.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CompilationTest {

    @Test
    void testCompilation() {
        Event event1 = Event.builder().id(1L).title("E1").build();
        Event event2 = Event.builder().id(2L).title("E2").build();

        Compilation compilation = new Compilation();
        compilation.setId(1L);
        compilation.setEvents(Set.of(event1, event2));
        compilation.setPinned(true);
        compilation.setTitle("CompilationTitle");

        assertThat(compilation.getId()).isEqualTo(1L);
        assertThat(compilation.getEvents()).containsExactlyInAnyOrder(event1, event2);
        assertThat(compilation.getPinned()).isTrue();
        assertThat(compilation.getTitle()).isEqualTo("CompilationTitle");

        Compilation built = Compilation.builder()
                .id(2L)
                .events(Set.of(event1))
                .pinned(false)
                .title("BuiltCompilation")
                .build();

        assertThat(built.getId()).isEqualTo(2L);
        assertThat(built.getTitle()).isEqualTo("BuiltCompilation");
    }
}
