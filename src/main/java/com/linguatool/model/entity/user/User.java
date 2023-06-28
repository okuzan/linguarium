package com.linguatool.model.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linguatool.model.entity.lang.Card;
import com.linguatool.model.entity.lang.CardSuggestion;
import com.linguatool.model.entity.lang.Language;
import com.linguatool.model.entity.lang.LanguageEntity;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.linguatool.util.GeneralUtils.generateId;


@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
@Table(name = "account")
public class User implements Serializable {
    private static final long serialVersionUID = 65981149772133526L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "PROVIDER_USER_ID")
    private String providerUserId;

    private String profilePicLink;

    @Column(unique = true)
    private String email;

    @Type(type = "numeric_boolean")
    private boolean enabled;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(columnDefinition = "TIMESTAMP", name = "created_date", nullable = false, updatable = false)
    protected LocalDateTime created;

    @Column(columnDefinition = "TIMESTAMP")
    protected LocalDateTime modified;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime lastLogin;

    @Column
    private int streak = 1;

    private String password;

    private String background;

    private String provider;

    private int dailyGoal = 5;
    private double vocabularyLevel = 5.5;

    @Column(name = "ui_lang")
    private Language uiLanguage = Language.ENGLISH;

    private boolean friendshipRequestsBlocked;

    @JsonIgnore
    @ManyToMany
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    private Set<Role> roles;

    @OneToMany(mappedBy = "requestee", fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Friendship> friendshipsInitiated;

    @OneToMany(mappedBy = "requester", fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Friendship> friendshipsRequested;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Card> cards;

    @OneToMany(mappedBy = "sender")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CardSuggestion> suggestedByMe;

    @OneToMany(mappedBy = "recipient")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CardSuggestion> suggestedToMe;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinTable(name = "user_target_lang",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "lang_id", referencedColumnName = "id")}
    )
    private Set<LanguageEntity> targetLanguages;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinTable(name = "user_fluent_lang",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "lang_id", referencedColumnName = "id")}
    )
    private Set<LanguageEntity> fluentLanguages;


    public void addCard(Card card) {
        if (card != null) {
            this.cards.add(card);
            card.setOwner(this);
        }
    }

    public void addCards(Iterable<Card> cards) {
        cards.forEach(this::addCard);
    }

    public void removeCard(Card card) {
        if (card != null) {
            this.cards.remove(card);
            card.setOwner(null);
        }
    }

    @PrePersist
    void usernameGenerator() {
        if (this.username == null) {
            this.username = generateId();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return enabled == user.enabled && streak == user.streak && dailyGoal == user.dailyGoal && Double.compare(user.vocabularyLevel, vocabularyLevel) == 0 && friendshipRequestsBlocked == user.friendshipRequestsBlocked && Objects.equals(id, user.id) && Objects.equals(providerUserId, user.providerUserId) && Objects.equals(profilePicLink, user.profilePicLink) && Objects.equals(email, user.email) && Objects.equals(username, user.username) && Objects.equals(created, user.created) && Objects.equals(modified, user.modified) && Objects.equals(lastLogin, user.lastLogin) && Objects.equals(password, user.password) && Objects.equals(background, user.background) && Objects.equals(provider, user.provider) && uiLanguage == user.uiLanguage && Objects.equals(roles, user.roles) && Objects.equals(friendshipsInitiated, user.friendshipsInitiated) && Objects.equals(friendshipsRequested, user.friendshipsRequested) && Objects.equals(cards, user.cards) && Objects.equals(suggestedByMe, user.suggestedByMe) && Objects.equals(suggestedToMe, user.suggestedToMe) && Objects.equals(targetLanguages, user.targetLanguages) && Objects.equals(fluentLanguages, user.fluentLanguages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, providerUserId, profilePicLink, email, enabled, username, created, modified, lastLogin, streak, password, background, provider, dailyGoal, vocabularyLevel, uiLanguage, friendshipRequestsBlocked, roles, friendshipsInitiated, friendshipsRequested, cards, suggestedByMe, suggestedToMe, targetLanguages, fluentLanguages);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", providerUserId='" + providerUserId + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", username='" + username + '\'' +
                ", created=" + created +
                ", modified=" + modified +
                ", password='" + password + '\'' +
                ", provider='" + provider + '\'' +
                ", friendshipRequestsBlocked=" + friendshipRequestsBlocked +
                '}';
    }
}
