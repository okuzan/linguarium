package linguarium.user.core.service.impl;

import java.util.Set;
import linguarium.engine.translator.model.enums.Language;
import linguarium.user.core.model.entity.Learner;
import linguarium.user.core.model.entity.Profile;
import linguarium.user.core.model.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserUtility {
    public static User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@example.com");
        Profile profile = new Profile();
        profile.setUiLang(Language.EN);
        profile.setAvatarUrl("profile.jpg");
        profile.setBackground("background.jpg");
        profile.setStreak(5);
        user.setProfile(profile);
        Learner learner = new Learner();
        learner.setTargetLangs(Set.of(Language.DE, Language.FR));
        learner.setFluentLangs(Set.of(Language.ES, Language.RU));
        user.setLearner(learner);
        return user;
    }
}
