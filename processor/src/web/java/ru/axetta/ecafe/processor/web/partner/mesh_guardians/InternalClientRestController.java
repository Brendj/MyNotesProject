package ru.axetta.ecafe.processor.web.partner.mesh_guardians;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao.ClientInfo;
import ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao.ErrorMsg;
import ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao.IDAOEntity;

import javax.annotation.PostConstruct;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;

@Path(value = "")
@Controller
@ApplicationPath("/mesh-controller/")
public class InternalClientRestController extends Application {
    private static final Logger log = LoggerFactory.getLogger(InternalClientRestController.class);

    private MeshClientProcessorService service;

    @PostConstruct
    public void init(){
        service = RuntimeContext.getAppContext().getBean(MeshClientProcessorService.class);
    }

    @POST
    @Path(value = "client")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createClient(@RequestBody ClientInfo info){
        try {
            service.createClient(info);

            return Response.status(HttpURLConnection.HTTP_OK).build();
        } catch (IllegalArgumentException e) {
            log.error("Couldn't find all parameters", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorMsg.badRequest());
        } catch (NoResultException e) {
            log.error("Unable to find organizations", e);
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorMsg.notFound());
        }
    }

    @GET
    @Path(value = "client")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkClient(@QueryParam(value = "guardianMeshGuid") String personGuid){
        try {
            checkParameter("guardianMeshGuid", personGuid);
            ClientInfo clientInfo = service.getClientGuardianByMeshGUID(personGuid);

            return generateResponse(HttpURLConnection.HTTP_OK, clientInfo);
        } catch (IllegalArgumentException e) {
            log.error("Couldn't find all parameters", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorMsg.badRequest());
        } catch (NoResultException e) {
            log.error("Unable to find organizations", e);
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorMsg.notFound());
        }
    }

    @PUT
    @Path(value = "client")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateClient(@RequestBody ClientInfo info){
        try {
            service.updateClient(info);

            return Response.status(HttpURLConnection.HTTP_OK).build();
        } catch (IllegalArgumentException e) {
            log.error("Couldn't find all parameters", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorMsg.badRequest());
        } catch (NoResultException e) {
            log.error("Unable to find organizations", e);
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorMsg.notFound());
        } catch (Exception e){
            log.error("Internal Error: ", e);
            return generateResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, ErrorMsg.internalError());
        }
    }

    @DELETE
    @Path(value = "client")
    public Response deleteClient(@QueryParam(value = "guardianMeshGuid") String personGuid){
        try {
            checkParameter("guardianMeshGuid", personGuid);

            service.deleteClient(personGuid);

            return Response.status(HttpURLConnection.HTTP_OK).build();
        } catch (IllegalArgumentException e) {
            log.error("Couldn't find all parameters", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorMsg.badRequest());
        } catch (NoResultException e) {
            log.error("Unable to find organizations", e);
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorMsg.notFound());
        }
    }

    @POST
    @Path("guardians")
    public Response processGuardiaRelations(){
        try {
            //checkParameter("guardianMeshGuid", personGuid);


            return Response.status(HttpURLConnection.HTTP_OK).build();
        } catch (IllegalArgumentException e) {
            log.error("Couldn't find all parameters", e);
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorMsg.badRequest());
        } catch (NoResultException e) {
            log.error("Unable to find organizations", e);
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorMsg.notFound());
        }
    }

    private void checkParameter(String parameterName, String parameter) throws IllegalArgumentException {
        if (StringUtils.isEmpty(parameter)) {
            throw new IllegalArgumentException(String.format("Parameter \"%s\" couldn't be null", parameterName));
        }
    }

    private <T extends Number> void checkParameter(String parameterName, T parameter) throws IllegalArgumentException {
        if (null == parameter) {
            throw new IllegalArgumentException(String.format("Parameter \"%s\" couldn't be null", parameterName));
        }
    }

    private Response generateResponse(Integer responseCode, IDAOEntity entity) {
        return Response.status(responseCode).entity(entity).build();
    }
}
