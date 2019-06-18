package com.xiao.biz.environment;

import com.xiao.biz.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author lix wang
 */
@Getter
@AllArgsConstructor
public enum ProfileType {
    DEV(Constants.ENV_DEV),
    ALPHA(Constants.ENV_ALPHA),
    BETA(Constants.ENV_BATE),
    PROD(Constants.ENV_PROD);

    private String name;

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
