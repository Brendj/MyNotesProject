package ru.axetta.ecafe.processor.web.partner;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SmartWatchVendor;
import ru.axetta.ecafe.processor.web.partner.oku.dataflow.ErrorResult;
import ru.axetta.ecafe.processor.web.partner.smartwatch.SmartWatchVendorManager;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.HttpURLConnection;

@Component
@Provider
public class RestEasyInterceptor implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    private static final String SMART_WATCH = "SmartWatchRestController";
    private static final String OKU = "OkuRestController";

    private static final Logger logger = LoggerFactory.getLogger(RestEasyInterceptor.class);

    public void filter(ContainerRequestContext requestContext) throws IOException {
        String controller = resourceInfo.getResourceClass().getSimpleName();
        switch (controller) {
            case SMART_WATCH:
                processSmartWatchRequest(requestContext);
                break;
            case OKU:
                processOKURequest(requestContext);
                break;
        }
    }

    private void processSmartWatchRequest(ContainerRequestContext requestContext) {
        try {
            SmartWatchVendorManager manager = RuntimeContext.getAppContext().getBean(SmartWatchVendorManager.class);
            StringBuilder sb = new StringBuilder();
            UriInfo info = requestContext.getUriInfo();

            sb.append(String.format("Try process request: %s | Inputted QueryParam:\n", info.getPath()));
            for (String key : info.getQueryParameters().keySet()) {
                sb.append(key).append(" : ").append(info.getQueryParameters().get(key));
                sb.append("\n");
            }
            logger.info(sb.toString());

            String requestHeaderKey = requestContext.getHeaderString("key");

            if (StringUtils.isEmpty(requestHeaderKey)) {
                throw new WebApplicationException(
                        Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("NOT_VALID_API_KEY").build());
            }

            SmartWatchVendor vendor = manager.getVendorIdByApiKey(requestHeaderKey);
            if (vendor == null || !vendor.getEnableService()){
                throw new WebApplicationException(
                        Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("NOT_VALID_API_KEY").build());
            }

            requestContext.getHeaders().add("vendorId", vendor.getIdOfVendor().toString());

        } catch (Exception e) {
            logger.error("Error in processSmartWatchRequest interceptor", e);
            throw e;
        }
    }

    private void processOKURequest(ContainerRequestContext requestContext) {
        String apiKey = RuntimeContext.getInstance().getOkuApiKey();

        String requestHeaderKey = requestContext.getHeaderString("Authorization");

        if (StringUtils.isEmpty(requestHeaderKey) || !requestHeaderKey.equals(apiKey)){
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_UNAUTHORIZED)
                    .entity(ErrorResult.unauthorized())
                    .build());
        }
    }
}
