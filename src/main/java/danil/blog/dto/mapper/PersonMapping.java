package danil.blog.dto.mapper;

import danil.blog.dto.PersonDto;
import danil.blog.models.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface PersonMapping {
    @Mapping(target="posts")
    PersonDto toDto(Person person);
    @Mapping(target="posts")
    Person toEntity(PersonDto dto);
}
