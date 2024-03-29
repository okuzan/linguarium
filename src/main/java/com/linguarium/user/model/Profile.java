package com.linguarium.user.model;

import com.linguarium.translator.model.Language;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Profile {

    @Id
    @Column(name = "id")
    Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    User user;

    String background;
    String profilePicLink;

    @Builder.Default
    int dailyGoal = 5;

    boolean friendshipRequestsBlocked;

    @Column(columnDefinition = "TIMESTAMP")
    LocalDateTime lastLogin;

    @Column
    @Builder.Default
    int streak = 1;

    @Column
    @Builder.Default
    Language uiLang = Language.EN;
}
