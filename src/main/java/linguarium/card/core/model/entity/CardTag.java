package linguarium.card.core.model.entity;

import static lombok.AccessLevel.PRIVATE;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import linguarium.card.core.model.entity.pk.CardTagPK;
import linguarium.user.core.model.entity.Learner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = PRIVATE)
public class CardTag {

    @EmbeddedId
    @Builder.Default
    CardTagPK id = new CardTagPK();

    @ManyToOne
    @MapsId("cardId")
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    Card card;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id", referencedColumnName = "id")
    Tag tag;

    @ManyToOne
    @JoinColumn(name = "learner_id", referencedColumnName = "id")
    Learner learner;
}