package com.xiao.event;

import com.xiao.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author lix wang
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ActuatorEvent extends ApplicationEvent {
    private final User user;
    private final String accessUrl;

    /**
     * Create a new ApplicationEvent.
     * @param source the object on which the event initially occurred (never {@code null})
     * @param user
     * @param accessUrl
     */
    public ActuatorEvent(Object source, User user, String accessUrl) {
        super(source);
        this.user = user;
        this.accessUrl = accessUrl;
    }
}
