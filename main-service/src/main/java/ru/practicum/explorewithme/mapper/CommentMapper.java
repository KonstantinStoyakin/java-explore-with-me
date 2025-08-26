package ru.practicum.explorewithme.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.dto.CommentDto;
import ru.practicum.explorewithme.dto.NewCommentDto;
import ru.practicum.explorewithme.model.Comment;
import ru.practicum.explorewithme.model.CommentStatus;
import ru.practicum.explorewithme.model.User;

@Component
public class CommentMapper {

    private final UserMapper userMapper;

    public CommentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthor(userMapper.toUserShortDto(comment.getAuthor()));
        dto.setEventId(comment.getEvent() != null ? comment.getEvent().getId() : null);
        dto.setStatus(comment.getStatus() != null ? comment.getStatus().name() : "PENDING");
        dto.setCreatedOn(comment.getCreatedOn());
        dto.setUpdatedOn(comment.getUpdatedOn());

        return dto;
    }

    public Comment toComment(NewCommentDto newCommentDto, User author) {
        if (newCommentDto == null || author == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setAuthor(author);
        comment.setStatus(CommentStatus.PENDING);

        return comment;
    }
}