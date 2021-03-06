package com.linguatool.repository;

import com.linguatool.model.entity.user.CardTag;
import com.linguatool.model.entity.user.CardTagPK;
import com.linguatool.model.entity.user.Tag;
import com.linguatool.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface CardTagRepository extends JpaRepository<CardTag, CardTagPK> {

    Set<CardTag> getByUserAndTag(User user, Tag tag);

    @Query(value = "select distinct tag_id from card_tag where user_id = ?1", nativeQuery = true)
    Set<Tag> getUsersTags(User user);

    void deleteByUserAndTag(User user, Tag tag);

}
