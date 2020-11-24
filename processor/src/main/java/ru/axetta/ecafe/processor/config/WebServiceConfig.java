package ru.axetta.ecafe.processor.config;

import javax.xml.ws.Endpoint;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.dashboard.DashboardServiceWS;
import ru.axetta.ecafe.processor.web.internal.CardSignature;
import ru.axetta.ecafe.processor.web.internal.EMIASController;
import ru.axetta.ecafe.processor.web.internal.FrontController;
import ru.axetta.ecafe.processor.web.internal.report.ReportControllerWS;
import ru.axetta.ecafe.processor.web.partner.acquiropay.soap.RegularPaymentWS;
import ru.axetta.ecafe.processor.web.partner.ezd.EZDControllerSOAP;
import ru.axetta.ecafe.processor.web.partner.integra.notify.NotifyControllerWS;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomControllerWS;
import ru.axetta.ecafe.processor.web.partner.integra.soap.POSPaymentControllerWS;
import ru.axetta.ecafe.processor.web.partner.integra.soap.PaymentControllerWS;
import ru.axetta.ecafe.processor.web.util.ClientRoomControllerLoggingInterceptor;
import ru.axetta.ecafe.processor.web.util.RemoveSecurityHeaderSoapInterceptor;

/**
 * Created by nuc on 27.10.2020.
 */
@Configuration
public class WebServiceConfig {

    @Bean
    public ServletRegistrationBean soapServlet() {
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
        RemoveSecurityHeaderSoapInterceptor removeSecurityHeaderSoapInterceptor = new RemoveSecurityHeaderSoapInterceptor();
        ClientRoomControllerLoggingInterceptor clientRoomControllerLoggingInterceptor = new ClientRoomControllerLoggingInterceptor();
        endpoint.getInInterceptors().add(removeSecurityHeaderSoapInterceptor);
        endpoint.getInInterceptors().add(clientRoomControllerLoggingInterceptor);
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

    @Bean
    public PaymentControllerWS paymentControllerWS() {
        return new PaymentControllerWS();
    }

    @Bean
    public Endpoint paymentControllerEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), paymentControllerWS());
        endpoint.publish("/payment");
        return endpoint;
    }

    @Bean
    public NotifyControllerWS notifyControllerWS() {
        return new NotifyControllerWS();
    }

    @Bean
    public Endpoint notifyControllerEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), notifyControllerWS());
        endpoint.publish("/notify");
        return endpoint;
    }

    @Bean
    public DashboardServiceWS dashboardServiceWS() {
        return new DashboardServiceWS();
    }

    @Bean
    public Endpoint dashboardServiceEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), dashboardServiceWS());
        endpoint.publish("/dashboard");
        return endpoint;
    }

    @Bean
    public ReportControllerWS reportControllerWS() {
        return new ReportControllerWS();
    }

    @Bean
    public Endpoint reportControllerEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), reportControllerWS());
        endpoint.publish("/integro");
        return endpoint;
    }

    @Bean
    public RegularPaymentWS regularPaymentWS() {
        return new RegularPaymentWS();
    }

    @Bean
    public Endpoint regularPaymentEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), regularPaymentWS());
        endpoint.publish("/regpay");
        return endpoint;
    }

    @Bean
    public POSPaymentControllerWS posPaymentControllerWS() {
        return new POSPaymentControllerWS();
    }

    @Bean
    public Endpoint posPaymentControllerEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), posPaymentControllerWS());
        endpoint.publish("/pos");
        return endpoint;
    }

    @Bean
    public EMIASController emiasControllerWS() {
        return new EMIASController();
    }

    @Bean
    public Endpoint emiasControllerEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), emiasControllerWS());
        endpoint.publish("/emias");
        return endpoint;
    }

    @Bean
    public CardSignature cardSignatureWS() {
        return new CardSignature();
    }

    @Bean
    public Endpoint cardSignatureEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), cardSignatureWS());
        endpoint.publish("/signcards");
        return endpoint;
    }

    @Bean
    public EZDControllerSOAP ezdControllerWS() {
        return new EZDControllerSOAP();
    }

    @Bean
    public Endpoint ezdControllerEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(springBus(), ezdControllerWS());
        endpoint.publish("/ezd");
        return endpoint;
    }

}
