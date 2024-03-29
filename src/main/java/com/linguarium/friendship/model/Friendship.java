package com.linguarium.friendship.model;

import com.linguarium.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"requester_id", "requestee_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"requesterId", "requesteeId"})
@IdClass(FriendshipPK.class)
public class Friendship implements Serializable {
    @Id
    @Column(name = "requester_id")
    private Long requesterId;

    @Id
    @Column(name = "requestee_id")
    private Long requesteeId;

    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User requester;

    @ManyToOne
    @JoinColumn(name = "requestee_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User requestee;

    @Column(columnDefinition = "TIMESTAMP", nullable = false, updatable = false)
    private LocalDateTime created;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updated;

    @Column(name = "status")
    private FriendshipStatus friendshipStatus;

    public Long whoDeniesFriendship() {
        if (this.getFriendshipStatus().equals(FriendshipStatus.FST_BLOCKED_SND)) {
            return requesterId;
        }
        if (this.getFriendshipStatus().equals(FriendshipStatus.SND_BLOCKED_FST)) {
            return requesteeId;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Friendship{"
                + "requesterId="
                + requesterId
                + ", requesteeId="
                + requesteeId
                + ", created="
                + created
                + ", updated="
                + updated
                + ", status="
                + friendshipStatus
                + '}';
    }
}
