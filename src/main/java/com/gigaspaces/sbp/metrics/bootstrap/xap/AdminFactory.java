package com.gigaspaces.sbp.metrics.bootstrap.xap;

import com.gigaspaces.sbp.metrics.bootstrap.GsMonitorSettings;
import org.openspaces.admin.Admin;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/11/14
 * Time: 9:11 AM
 * the infrastructure necessary to marry GsMonitorSettings (from CLI) to the spring context
 * and build up an Admin
 */
@Component
public class AdminFactory {

    private Admin admin;

    @Resource
    private GsMonitorSettings gsMonitorSettings;



}
