package org.georges.georges.Config;

import org.springframework.context.ApplicationEvent;

public class MessageSendEvent extends ApplicationEvent {

    public MessageSendEvent(Object source) {
        super(source);
    }
}
