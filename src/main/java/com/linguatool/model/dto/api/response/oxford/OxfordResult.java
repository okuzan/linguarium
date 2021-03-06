package com.linguatool.model.dto.api.response.oxford;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class OxfordResult {
    OxfordLexicalEntry[] lexicalEntries;
    String id;
    String language;
    String type;
    String word;
}
