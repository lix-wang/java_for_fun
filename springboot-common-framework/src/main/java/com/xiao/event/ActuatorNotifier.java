package com.xiao.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author lix wang
 */
@Log4j2
@Component
public class ActuatorNotifier implements ApplicationListener<ActuatorEvent> {
    @Override
    public void onApplicationEvent(ActuatorEvent event) {
        log.info("I received a actuator event from: " + event.getUser() + " uri: " + event.getAccessUrl());
    }
}
