package ru.practicum.explorewithme.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.model.ParticipationRequest;

@Component
public class RequestMapper {

    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        if (participationRequest == null) {
            return null;
        }

        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(participationRequest.getId());
        dto.setCreated(participationRequest.getCreated());
        dto.setEvent(participationRequest.getEvent() != null
                ? participationRequest.getEvent().getId()
                : null);
        dto.setRequester(participationRequest.getRequester() != null
                ? participationRequest.getRequester().getId()
                : null);
        dto.setStatus(participationRequest.getStatus());

        return dto;
    }
}