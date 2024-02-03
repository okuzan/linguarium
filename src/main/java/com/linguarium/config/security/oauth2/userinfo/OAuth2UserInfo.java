package com.linguarium.config.security.oauth2.userinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public abstract String getId();

    public abstract String getName();

    public abstract String getFirstName();

    public abstract String getLastName();

    public abstract String getEmail();

    public abstract String getImageUrl();

    public String getStringAttribute(String attributeName) {
        Object value = attributes.get(attributeName);
        return (value instanceof String) ? (String) value : null;
    }

    public String getNestedStringAttribute(String nestedAttributeName) {
        String[] nestedAttributes = nestedAttributeName.split("\\.");
        Object currentObj = attributes;
        for (String attr : nestedAttributes) {
            if (currentObj instanceof Map) {
                currentObj = ((Map<?, ?>) currentObj).get(attr);
            } else {
                return null;
            }
        }
        return (currentObj instanceof String) ? (String) currentObj : null;
    }
}