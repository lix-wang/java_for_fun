package com.xiao.event;

import com.xiao.model.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 *
 * @author lix wang
 */
@Log4j2
@Component
public class ActuatorPublisher implements ApplicationEventPublisherAware {
   private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishActuatorEvent(User user, String accessUrl) {
        ActuatorEvent actuatorEvent = new ActuatorEvent(this, user, accessUrl);
        log.info("I am publishing an event");
        applicationEventPublisher.publishEvent(actuatorEvent);
        log.info("I am finish publishing an event");
    }
}
