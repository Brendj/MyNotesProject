package ru.axetta.ecafe.processor.config;

import javax.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomControllerWS;

/**
 * Created by nuc on 27.10.2020.
 */
@Configuration
public class WebServiceConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        CXFServlet cxfServlet = new CXFServlet();

        ServletRegistrationBean servletRegistrationBean =
                new ServletRegistrationBean(cxfServlet, "/soap/*");
        servletRegistrationBean.setLoadOnStartup(1);
        return servletRegistrationBean;
    }

    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public ClientRoomController clientRoomControllerService() {
        return new ClientRoomControllerWS();
    }

    @Bean
    @DependsOn("servletRegistrationBean")
    public Endpoint endpoint() {
        Bus bus = (Bus) applicationContext.getBean(Bus.DEFAULT_BUS_ID);
        EndpointImpl endpoint = new EndpointImpl(bus, clientRoomControllerService());
        //endpoint.publish("/ClientRoomControllerWS");
        // also showing how to add interceptors
        //endpoint.getServer().getEndpoint().getInInterceptors().add(new LoggingInInterceptor());
        //endpoint.getServer().getEndpoint().getOutInterceptors().add(new LoggingOutInterceptor());

        return endpoint;
    }
}
