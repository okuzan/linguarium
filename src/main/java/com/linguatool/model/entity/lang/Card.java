package com.linguatool.model.entity.lang;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linguatool.model.entity.user.CardTag;
import com.linguatool.model.entity.user.Language;
import com.linguatool.model.entity.user.Tag;
import com.linguatool.model.entity.user.User;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "card")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column
    String entry;

    @Column(columnDefinition = "TIMESTAMP")
    LocalDateTime created;

    @Column(columnDefinition = "TIMESTAMP")
    LocalDateTime modified;

    @Column(columnDefinition = "TIMESTAMP")
    LocalDateTime lastRepeat;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    User owner;

    @Type(type = "numeric_boolean")
    boolean irregularSpelling;

    @Type(type = "numeric_boolean")
    boolean activeLearning = true;

    @Type(type = "numeric_boolean")
    boolean falseFriend;

    @Type(type = "numeric_boolean")
    boolean irregularPlural;

    @Type(type = "numeric_boolean")
    boolean learnt;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "lang_id", referencedColumnName = "id")
    LanguageEntity language;

    @OneToMany(mappedBy = "card")
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Example> examples;

    @OneToMany(mappedBy = "card")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Set<CardTag> tagCards;

    public void addExample(Example example) {
        if (example != null) {
            this.examples.add(example);
            example.setCard(this);
        }
    }

    public void removeExample(Example example) {
        if (example != null) {
            this.examples.remove(example);
            example.setCard(null);
        }
    }

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "card")
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Translation> translations;

    private String notes;

    private String source;

    private int iterations = 0;

    private int priority = 2;

    private String ipa;

    private int frequency;

//    @ManyToMany
//    @JoinTable(name = "confused",
//        joinColumns = {@JoinColumn(name = "fst_word_id", referencedColumnName = "id")},
//        inverseJoinColumns = {@JoinColumn(name = "snd_word_id", referencedColumnName = "id")}
//    )
//    List<CardEntity> confusedWith;

    String wordFamily;

    String hardIndices;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return id.equals(card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

