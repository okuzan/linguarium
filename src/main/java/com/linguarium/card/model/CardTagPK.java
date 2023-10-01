package com.linguarium.card.model;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class CardTagPK implements Serializable {
    private Long cardId;
    private Long tagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CardTagPK that = (CardTagPK) o;
        return cardId.equals(that.cardId) && tagId.equals(that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId, tagId);
    }
}