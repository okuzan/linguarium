package com.linguatool.model.mapping;

import com.linguatool.model.dto.external_api.request.*;
import com.linguatool.model.entity.lang.Card;
import com.linguatool.model.entity.lang.Example;
import com.linguatool.model.entity.lang.LanguageEntity;
import com.linguatool.model.entity.lang.Translation;
import com.linguatool.model.entity.user.CardTag;
import com.linguatool.model.entity.user.Language;
import com.linguatool.repository.LanguageRepository;
import org.mapstruct.*;

import java.util.Optional;

@Mapper
public interface CardMapper {

    @Mapping(target = "language", expression = "java(languageMapping(dto.getLanguage(),repo))")
    Card cardDtoToEntity(CardCreationDto dto, LanguageRepository repo);

    @Mapping(target = "language", expression = "java(languageMapping(dto.getLanguage(),repo))")
    @Mapping(target = "id", expression = "java(null)")
    Card copyCardDtoToEntity(CardDto dto, LanguageRepository repo);

    @Mapping(target = "language", expression = "java(languageMapping(dto.getLanguage(),repo))")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(CardDto dto, @MappingTarget Card card, LanguageRepository repo);

    @Mapping(target = "tags", source = "tagCards")
    CardDto cardEntityToDto(Card cardEntity);

    Translation translationDtoToEntity(TranslationDto dto);

    default TagDto tagEntityToDto(CardTag entity) {
        return TagDto.builder()
                .text((entity.getTag()).getText())
                .build();
    }

    default LanguageEntity languageMapping(LanguageDto dto, LanguageRepository repo) {
        Optional<LanguageEntity> languageEntityOptional = repo.findByCode(
                Language.fromString(dto.getLanguageCode())
        );
        if (languageEntityOptional.isPresent()) {
            return languageEntityOptional.get();
        } else {
            throw new IllegalArgumentException("");
        }
    }

    default LanguageDto languageEntityToDto(LanguageEntity entity) {
        return LanguageDto.builder()
                .languageCode(entity.getCode().getCode())
                .build();
    }

    Example exampleDtoToEntity(ExampleDto dto);

}
