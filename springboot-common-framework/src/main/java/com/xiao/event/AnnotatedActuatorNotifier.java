package com.xiao.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *
 * @author lix wang
 */
@Log4j2
@Component
public class AnnotatedActuatorNotifier {
    @Async
    @EventListener
    public void handleActuatorEvent(ActuatorEvent event) {
       log.info("Annotated actuator notifier received a event: " + event.getAccessUrl());
    }
}
