package com.almuradev.guide.event;

import cpw.mods.fml.common.eventhandler.Event;

public class PageDeleteEvent extends Event {
    public final String identifier;

    public PageDeleteEvent(String identifier) {
        this.identifier = identifier;
    }
}
