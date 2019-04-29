package com.xiao.environment;

import com.xiao.config.Constants;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author lix wang
 */
public enum ProfileType {
    DEV(Constants.ENV_DEV),
    ALPHA(Constants.ENV_ALPHA),
    BETA(Constants.ENV_BATE),
    PROD(Constants.ENV_PROD);

    private String name;

    ProfileType(String name) {
        this.name = name;
    }

    public static ProfileType getProfile(String type) {
        if (StringUtils.isNotBlank(type)) {
            String[] types = type.split(",");
            if (types != null && types.length > 0) {
                for (String typeText : types) {
                    for (ProfileType profileType : values()) {
                        if (profileType.match(typeText)) {
                            return profileType;
                        }
                    }
                }
            }
        }
        return DEV;
    }

    private boolean match(String text) {
        return name.equalsIgnoreCase(text);
    }
}
