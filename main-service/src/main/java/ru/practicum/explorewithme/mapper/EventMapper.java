package ru.practicum.explorewithme.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.dto.EventFullDto;
import ru.practicum.explorewithme.dto.EventShortDto;
import ru.practicum.explorewithme.dto.NewEventDto;
import ru.practicum.explorewithme.dto.UpdateEventAdminRequest;
import ru.practicum.explorewithme.dto.UpdateEventUserRequest;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.EventState;

import java.time.LocalDateTime;

@Component
public class EventMapper {

    private final CategoryMapper categoryMapper;

    private final UserMapper userMapper;

    public EventMapper(CategoryMapper categoryMapper, UserMapper userMapper) {
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
    }

    public EventFullDto toEventFullDto(Event event) {
        if (event == null) {
            return null;
        }

        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation() != null ? event.getAnnotation() : "");
        dto.setCategory(categoryMapper.toCategoryDto(event.getCategory()));
        dto.setConfirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : 0);
        dto.setCreatedOn(event.getCreatedOn());
        dto.setDescription(event.getDescription() != null ? event.getDescription() : "");
        dto.setEventDate(event.getEventDate());
        dto.setInitiator(userMapper.toUserShortDto(event.getInitiator()));
        dto.setLocation(event.getLocation());
        dto.setPaid(event.getPaid() != null ? event.getPaid() : false);
        dto.setParticipantLimit(event.getParticipantLimit() != null ? event.getParticipantLimit() : 0);
        dto.setPublishedOn(event.getPublishedOn());
        dto.setRequestModeration(event.getRequestModeration() != null ? event.getRequestModeration() : true);
        dto.setState(event.getState() != null ? event.getState().name() : "PENDING");
        dto.setTitle(event.getTitle() != null ? event.getTitle() : "");
        dto.setViews(event.getViews() != null ? event.getViews() : 0L);

        return dto;
    }

    public EventShortDto toEventShortDto(Event event) {
        if (event == null) {
            return null;
        }

        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation() != null ? event.getAnnotation() : "");
        dto.setCategory(categoryMapper.toCategoryDto(event.getCategory()));
        dto.setConfirmedRequests(event.getConfirmedRequests() != null ? event.getConfirmedRequests() : 0);
        dto.setEventDate(event.getEventDate());
        dto.setInitiator(userMapper.toUserShortDto(event.getInitiator()));
        dto.setPaid(event.getPaid() != null ? event.getPaid() : false);
        dto.setTitle(event.getTitle() != null ? event.getTitle() : "");
        dto.setViews(event.getViews() != null ? event.getViews() : 0L);

        return dto;
    }

    public Event toEvent(NewEventDto newEventDto) {
        if (newEventDto == null) {
            return null;
        }

        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setLocation(newEventDto.getLocation());
        event.setPaid(newEventDto.getPaid() != null ? newEventDto.getPaid() : false);

        Integer participantLimit = newEventDto.getParticipantLimit();
        if (participantLimit != null && participantLimit < 0) {
            throw new ValidationException("Participant limit cannot be negative");
        }
        event.setParticipantLimit(participantLimit != null ? participantLimit : 0);

        event.setRequestModeration(newEventDto.getRequestModeration() != null
                ? newEventDto.getRequestModeration()
                : true
        );
        event.setTitle(newEventDto.getTitle());

        return event;
    }

    public void updateEventFromAdminRequest(UpdateEventAdminRequest updateRequest, Event event) {
        if (updateRequest == null || event == null) {
            return;
        }

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }

        if (updateRequest.getLocation() != null) {
            event.setLocation(updateRequest.getLocation());
        }

        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.getParticipantLimit() != null) {
            if (updateRequest.getParticipantLimit() < 0) {
                throw new ValidationException("Participant limit cannot be negative");
            }
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getStateAction() != null) {
            if ("PUBLISH_EVENT".equals(updateRequest.getStateAction())) {
                if (event.getState() != EventState.PENDING) {
                    throw new ConflictException("Cannot publish the event because it's not in the right state: "
                            + event.getState());
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if ("REJECT_EVENT".equals(updateRequest.getStateAction())) {
                if (event.getState() != EventState.PENDING) {
                    throw new ConflictException("Cannot reject the event because it's not in the right state: "
                            + event.getState());
                }
                event.setState(EventState.CANCELED);
            }
        }
    }

    public void updateEventFromUserRequest(UpdateEventUserRequest updateRequest, Event event) {
        if (updateRequest == null || event == null) {
            return;
        }

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.getDescription() != null) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }

        if (updateRequest.getLocation() != null) {
            event.setLocation(updateRequest.getLocation());
        }

        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.getParticipantLimit() != null) {
            if (updateRequest.getParticipantLimit() < 0) {
                throw new ValidationException("Participant limit cannot be negative");
            }
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        if (updateRequest.getStateAction() != null) {
            if ("SEND_TO_REVIEW".equals(updateRequest.getStateAction())) {
                event.setState(EventState.PENDING);
            } else if ("CANCEL_REVIEW".equals(updateRequest.getStateAction())) {
                event.setState(EventState.CANCELED);
            }
        }
    }
}