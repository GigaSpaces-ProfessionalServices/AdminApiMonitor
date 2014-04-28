package com.gigaspaces.monitoring.metrics_source.adminapi;

import com.gigaspaces.annotation.pojo.SpaceId;
import com.gigaspaces.annotation.pojo.SpaceRouting;

/**
 * A simple object used to work with the Space.
 */
public class Message  {

    private Integer id;
    private String info;

    /**
     * Necessary Default constructor
     */
    public Message() {
    }

    /**
     * Constructs a new Message with the given id and info
     * and info.
     */
    public Message(Integer id, String info) {
        this.id = id;
        this.info = info;
    }

    /**
     * The id of this message.
     * We will use this attribute to route the message objects when
     * they are written to the space, defined in the Message.gs.xml file.
     */
    @SpaceId
    @SpaceRouting
    public Integer getId() {
        return id;
    }

    /**
     * The id of this message.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * The information this object holds.
     */
    public String getInfo() {
        return info;
    }

    /**
     * The information this object holds.
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * A simple toString to print out the state.
     */
    public String toString() {
        return "id[" + id + "] info[" + info +"]";
    }
}
