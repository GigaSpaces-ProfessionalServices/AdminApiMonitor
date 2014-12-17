package com.gigaspaces.sbp.metrics.bootstrap.xap;

import com.gigaspaces.Factory;
import org.openspaces.admin.AdminFactory;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: jason
 * Date: 11/18/14
 * Time: 3:08 PM
 *
 * Provides a means of testing with DI.
 */
@Component
class AdminFactoryFactory implements Factory<AdminFactory> {

    /**
     * {@inheritDoc}
     */
    @Override
    public AdminFactory build(){
        return new AdminFactory();
    }

}
