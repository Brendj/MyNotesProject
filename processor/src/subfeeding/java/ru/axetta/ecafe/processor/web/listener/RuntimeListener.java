package ru.axetta.ecafe.processor.web.listener;

import ru.axetta.ecafe.processor.web.ejb.Configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 29.04.14
 * Time: 12:41
 * To change this template use File | Settings | File Templates.
 */
@WebListener
public class RuntimeListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(RuntimeListener.class);

    @EJB
    private Configurations configurations;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String appPropPath = servletContextEvent.getServletContext().getInitParameter("appProperties");
        InputStream is = servletContextEvent.getServletContext().getResourceAsStream(appPropPath);
        Properties properties = new Properties();
        String url = "http://localhost:8080/processor/soap/client?wsdl";
        try {
            properties.load(is);
            url = properties.getProperty("clientServiceWsdlLocation");
        } catch (Exception ex) {
            logger.warn("appProperties non found: use http://localhost:8080/processor/soap/client?wsdl");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            configurations.createClientRoomController(url);
        }  catch (Exception ex) {
            logger.error("error createClientRoomController", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
