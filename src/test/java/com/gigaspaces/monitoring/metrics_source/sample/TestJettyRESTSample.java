package com.gigaspaces.monitoring.metrics_source.sample;

import com.gigaspaces.async.AsyncFuture;
import com.gigaspaces.client.WriteModifiers;
import com.gigaspaces.monitoring.metrics_source.adminapi.Message;
import com.gigaspaces.monitoring.metrics_source.space_proxy.MeasurementExposerImpl;
import com.gigaspaces.query.IdQuery;
import com.j_spaces.core.LeaseContext;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openspaces.core.GigaSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ExecutionException;

/**
 * NON-JAVADOC
 * Example requests
 * GET http://localhost:8080/perf_interceptor?id=4
 * POST http://localhost:8080/perf_interceptor?id=4&messag=Hello
 * PUT http://localhost:8080/perf_interceptor?id=4&message=updated
 * DELETE http://localhost:8080/perf_interceptor?id=4
 */

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(locations = {"/META-INF/spring/pu.xml", "/META-INF/spring/mbean-server.xml"})
public class TestJettyRESTSample {

    @Autowired
    @Qualifier(value = "wrappedGigaSpace")
    private GigaSpace gigaSpace;

    private MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

    @Value( "${bean_id}" )
    private String beanId;

    private ObjectName objectName ;

    @Autowired
    private MeasurementExposerImpl exposer;

    @Before
    public void setUp() throws Exception {
        objectName = new ObjectName(beanId);
        mBeanServer.registerMBean(exposer, objectName);
        exposer.startSpewing();
    }

    @Test
    public void testEmbeddedJetty() throws Exception {
        Server server = new Server(8080);
        server.setHandler(new HelloHandler(gigaSpace));
        server.start();
        server.join();
    }

    public void setGigaSpace(GigaSpace gigaSpace) {
        this.gigaSpace = gigaSpace;
    }

    static class HelloHandler extends AbstractHandler
    {

        private GigaSpace gigaSpace;

        HelloHandler(GigaSpace gigaSpace) {
            this.gigaSpace = gigaSpace;
        }

        public void handle(String target,Request baseRequest, HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException
        {
            Message result = null;
            Integer id = Integer.parseInt(request.getParameter("id"));
            String method = request.getMethod();
            switch (method){
                case "GET": {
                    if (request.getParameter("async") != null){
                        AsyncFuture<Message> messageAsyncFuture = gigaSpace.asyncRead(new Message(), id);
                        try {
                            result = messageAsyncFuture.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }   else {
                        result = gigaSpace.readById(Message.class, id);
                    }
                    break;
                }
                case "POST": {
                    LeaseContext<Message> message = gigaSpace.write(new Message(id, request.getParameter("message")));
                    result = message.getObject();
                    break;
                }
                case "PUT": {
                    LeaseContext<Message> context = gigaSpace.write(new Message(id, request.getParameter("message")), WriteModifiers.UPDATE_ONLY);
                    result = context.getObject();
                    break;
                }
                case "DELETE": {
                    result = gigaSpace.takeById(new IdQuery<Message>(Message.class, id));
                    break;
                }
            }
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            response.getWriter().println("<h1>Hello World</h1>");
            response.getWriter().println(result);
        }
    }
}
