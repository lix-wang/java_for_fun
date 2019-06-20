package com.xiao.helper;

import com.xiao.framework.biz.service.ActuatorService;
import com.xiao.interceptor.SessionUtils;
import com.xiao.model.User;
import org.springframework.stereotype.Component;

/**
 *
 * @author lix wang
 */
@Component
public class ActuatorHelper implements ActuatorService {
    @Override
    public boolean checkActuatorAccessPermission() {
        User user = SessionUtils.CURRENT_USER.get();
        if (user != null && "admin".equalsIgnoreCase(user.getName())) {
            return true;
        }
        return false;
    }
}
