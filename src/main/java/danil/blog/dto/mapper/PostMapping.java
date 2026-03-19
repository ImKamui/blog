package danil.blog.dto.mapper;

import danil.blog.dto.PostDto;
import danil.blog.models.Post;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapping {
    @Mapping(target = "postText", source = "postText")
    @Mapping(target = "postImage", source = "postImage")
    @Mapping(target = "username", expression = "java(post.getOwner().getUsername())")
    PostDto toPostDto(Post post);

    @AfterMapping
    default void setOwnerUsername(Post post, @MappingTarget PostDto dto) {
        if (post.getOwner() != null) {
            dto.setUsername(post.getOwner().getUsername());
        }
    }
}
