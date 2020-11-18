package ru.axetta.ecafe.processor.config;

import javax.xml.ws.Endpoint;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.internal.FrontController;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomControllerWS;

/**
 * Created by nuc on 27.10.2020.
 */
@Configuration
public class WebServiceConfig {

    @Bean
    public ServletRegistrationBean disServlet() {
        CXFServlet cxfServlet = new CXFServlet();
        return new ServletRegistrationBean(cxfServlet, "/soap/*");
    }

    @Bean(name="cxf")
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Bean
    public ClientRoomControllerWS clientRoomControllerWS() {
        return new ClientRoomControllerWS();
    }

    @Bean
    public Endpoint clientRoomControllerEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), clientRoomControllerWS());
        endpoint.publish("/client");
        return endpoint;
    }

    @Bean
    public FrontController frontController() {
        return new FrontController();
    }

    @Bean
    public Endpoint frontControllerEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), frontController());
        endpoint.publish("/front");
        return endpoint;
    }


    /*@Autowired
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
    }*/
}
