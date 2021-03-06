package com.gigaspaces.sbp.metrics.bootstrap.xap;

import org.openspaces.admin.Admin;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/18/14
 * Time: 2:19 PM
 */
@Component
class SpaceConnections {

    ConnectToSpace connect(String spaceName, Admin admin){
        return new ConnectToSpace(spaceName, admin);
    }
}
