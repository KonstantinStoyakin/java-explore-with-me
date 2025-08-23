package ru.practicum.explorewithme.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.dto.CompilationDto;
import ru.practicum.explorewithme.dto.NewCompilationDto;
import ru.practicum.explorewithme.dto.UpdateCompilationRequest;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.CompilationMapper;
import ru.practicum.explorewithme.model.Compilation;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.repository.CompilationRepository;
import ru.practicum.explorewithme.repository.EventRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompilationServiceImplTest {

    @Mock
    private CompilationRepository compilationRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CompilationMapper compilationMapper;

    @InjectMocks
    private CompilationServiceImpl compilationService;

    @Test
    void addCompilation_shouldCreateNewCompilation() {
        NewCompilationDto newCompilationDto = new NewCompilationDto();
        newCompilationDto.setTitle("Test Compilation");
        newCompilationDto.setPinned(true);
        newCompilationDto.setEvents(List.of(1L, 2L));

        Compilation compilation = new Compilation();
        compilation.setTitle("Test Compilation");
        compilation.setPinned(true);

        CompilationDto expectedDto = new CompilationDto();
        expectedDto.setId(1L);
        expectedDto.setTitle("Test Compilation");
        expectedDto.setPinned(true);
        expectedDto.setEvents(List.of());

        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(2L);

        when(compilationMapper.toCompilation(newCompilationDto)).thenReturn(compilation);
        when(eventRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(event1, event2));
        when(compilationRepository.save(compilation)).thenReturn(compilation);
        when(compilationMapper.toCompilationDto(compilation)).thenReturn(expectedDto);

        CompilationDto result = compilationService.addCompilation(newCompilationDto);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getTitle(), result.getTitle());
        verify(compilationMapper).toCompilation(newCompilationDto);
        verify(eventRepository).findAllById(List.of(1L, 2L));
        verify(compilationRepository).save(compilation);
    }

    @Test
    void addCompilation_shouldHandleNullEvents() {
        NewCompilationDto newCompilationDto = new NewCompilationDto();
        newCompilationDto.setTitle("Test Compilation");
        newCompilationDto.setPinned(true);
        newCompilationDto.setEvents(null);

        Compilation compilation = new Compilation();
        compilation.setTitle("Test Compilation");
        compilation.setPinned(true);

        CompilationDto expectedDto = new CompilationDto();
        expectedDto.setId(1L);
        expectedDto.setTitle("Test Compilation");
        expectedDto.setPinned(true);
        expectedDto.setEvents(List.of());

        when(compilationMapper.toCompilation(newCompilationDto)).thenReturn(compilation);
        when(compilationRepository.save(compilation)).thenReturn(compilation);
        when(compilationMapper.toCompilationDto(compilation)).thenReturn(expectedDto);

        CompilationDto result = compilationService.addCompilation(newCompilationDto);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        verify(compilationMapper).toCompilation(newCompilationDto);
        verify(eventRepository, never()).findAllById(any());
        verify(compilationRepository).save(compilation);
    }

    @Test
    void deleteCompilation_shouldDeleteCompilation() {
        Long compId = 1L;

        when(compilationRepository.existsById(compId)).thenReturn(true);
        doNothing().when(compilationRepository).deleteById(compId);

        assertDoesNotThrow(() -> compilationService.deleteCompilation(compId));
        verify(compilationRepository).existsById(compId);
        verify(compilationRepository).deleteById(compId);
    }

    @Test
    void deleteCompilation_shouldThrowNotFoundException() {
        Long compId = 1L;

        when(compilationRepository.existsById(compId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> compilationService.deleteCompilation(compId));
        verify(compilationRepository).existsById(compId);
        verify(compilationRepository, never()).deleteById(any());
    }

    @Test
    void updateCompilation_shouldUpdateCompilation() {
        Long compId = 1L;
        UpdateCompilationRequest updateRequest = new UpdateCompilationRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setPinned(false);
        updateRequest.setEvents(List.of(3L, 4L));

        Compilation compilation = new Compilation();
        compilation.setId(compId);
        compilation.setTitle("Old Title");
        compilation.setPinned(true);

        Event event3 = new Event();
        event3.setId(3L);
        Event event4 = new Event();
        event4.setId(4L);

        CompilationDto expectedDto = new CompilationDto();
        expectedDto.setId(compId);
        expectedDto.setTitle("Updated Title");
        expectedDto.setPinned(false);
        expectedDto.setEvents(List.of());

        when(compilationRepository.findById(compId)).thenReturn(Optional.of(compilation));
        when(eventRepository.findAllById(List.of(3L, 4L))).thenReturn(List.of(event3, event4));
        when(compilationRepository.save(compilation)).thenReturn(compilation);
        when(compilationMapper.toCompilationDto(compilation)).thenReturn(expectedDto);

        doAnswer(invocation -> {
            UpdateCompilationRequest req = invocation.getArgument(0);
            Compilation comp = invocation.getArgument(1);

            if (req.getTitle() != null) {
                comp.setTitle(req.getTitle());
            }
            if (req.getPinned() != null) {
                comp.setPinned(req.getPinned());
            }
            return null;
        }).when(compilationMapper).updateCompilationFromRequest(
                any(UpdateCompilationRequest.class),
                any(Compilation.class)
        );

        CompilationDto result = compilationService.updateCompilation(compId, updateRequest);

        assertNotNull(result);
        assertEquals(expectedDto.getTitle(), result.getTitle());
        assertEquals(expectedDto.getPinned(), result.getPinned());
        assertEquals("Updated Title", compilation.getTitle());
        assertEquals(false, compilation.getPinned());
        verify(compilationRepository).findById(compId);
        verify(eventRepository).findAllById(List.of(3L, 4L));
        verify(compilationRepository).save(compilation);
    }

    @Test
    void updateCompilation_shouldHandleNullEvents() {
        Long compId = 1L;
        UpdateCompilationRequest updateRequest = new UpdateCompilationRequest();
        updateRequest.setTitle("Updated Title");

        Compilation compilation = new Compilation();
        compilation.setId(compId);
        compilation.setTitle("Old Title");
        compilation.setPinned(true);
        compilation.setEvents(Set.of(new Event()));

        CompilationDto expectedDto = new CompilationDto();
        expectedDto.setId(compId);
        expectedDto.setTitle("Updated Title");
        expectedDto.setPinned(true);
        expectedDto.setEvents(List.of());

        when(compilationRepository.findById(compId)).thenReturn(Optional.of(compilation));
        when(compilationRepository.save(compilation)).thenReturn(compilation);
        when(compilationMapper.toCompilationDto(compilation)).thenReturn(expectedDto);

        CompilationDto result = compilationService.updateCompilation(compId, updateRequest);

        assertNotNull(result);
        assertEquals(expectedDto.getTitle(), result.getTitle());
        verify(compilationRepository).findById(compId);
        verify(eventRepository, never()).findAllById(any());
        verify(compilationRepository).save(compilation);
    }

    @Test
    void updateCompilation_shouldThrowNotFoundException() {
        Long compId = 1L;
        UpdateCompilationRequest updateRequest = new UpdateCompilationRequest();

        when(compilationRepository.findById(compId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> compilationService.updateCompilation(compId, updateRequest));
        verify(compilationRepository).findById(compId);
        verify(compilationRepository, never()).save(any());
    }

    @Test
    void getCompilations_shouldReturnAllCompilationsWhenPinnedIsNull() {
        Boolean pinned = null;
        Integer from = 0;
        Integer size = 10;
        PageRequest pageRequest = PageRequest.of(0, size);

        Compilation compilation1 = new Compilation();
        compilation1.setId(1L);
        Compilation compilation2 = new Compilation();
        compilation2.setId(2L);
        Page<Compilation> compilationPage = new PageImpl<>(List.of(compilation1, compilation2));

        CompilationDto dto1 = new CompilationDto();
        dto1.setId(1L);
        dto1.setTitle("Compilation 1");
        dto1.setPinned(true);
        dto1.setEvents(List.of());

        CompilationDto dto2 = new CompilationDto();
        dto2.setId(2L);
        dto2.setTitle("Compilation 2");
        dto2.setPinned(false);
        dto2.setEvents(List.of());

        when(compilationRepository.findAll(pageRequest)).thenReturn(compilationPage);
        when(compilationMapper.toCompilationDto(compilation1)).thenReturn(dto1);
        when(compilationMapper.toCompilationDto(compilation2)).thenReturn(dto2);

        List<CompilationDto> result = compilationService.getCompilations(pinned, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(compilationRepository).findAll(pageRequest);
        verify(compilationRepository, never()).findAllByPinned(anyBoolean(), any());
    }

    @Test
    void getCompilations_shouldReturnPinnedCompilations() {
        Boolean pinned = true;
        Integer from = 0;
        Integer size = 10;
        PageRequest pageRequest = PageRequest.of(0, size);

        Compilation compilation = new Compilation();
        compilation.setId(1L);
        compilation.setPinned(true);
        List<Compilation> compilations = List.of(compilation);

        CompilationDto dto = new CompilationDto();
        dto.setId(1L);
        dto.setTitle("Pinned Compilation");
        dto.setPinned(true);
        dto.setEvents(List.of());

        when(compilationRepository.findAllByPinned(pinned, pageRequest)).thenReturn(compilations);
        when(compilationMapper.toCompilationDto(compilation)).thenReturn(dto);

        List<CompilationDto> result = compilationService.getCompilations(pinned, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(compilationRepository).findAllByPinned(pinned, pageRequest);
        verify(compilationRepository, never()).findAll((Pageable) any());
    }

    @Test
    void getCompilation_shouldReturnCompilation() {
        Long compId = 1L;
        Compilation compilation = new Compilation();
        compilation.setId(compId);
        compilation.setTitle("Test Compilation");

        CompilationDto expectedDto = new CompilationDto();
        expectedDto.setId(compId);
        expectedDto.setTitle("Test Compilation");
        expectedDto.setPinned(true);
        expectedDto.setEvents(List.of());

        when(compilationRepository.findById(compId)).thenReturn(Optional.of(compilation));
        when(compilationMapper.toCompilationDto(compilation)).thenReturn(expectedDto);

        CompilationDto result = compilationService.getCompilation(compId);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getTitle(), result.getTitle());
        verify(compilationRepository).findById(compId);
    }

    @Test
    void getCompilation_shouldThrowNotFoundException() {
        Long compId = 1L;

        when(compilationRepository.findById(compId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> compilationService.getCompilation(compId));
        verify(compilationRepository).findById(compId);
    }
}