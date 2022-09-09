package ru.axetta.ecafe.processor.web.partner.mesh_guardians;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao.*;

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
            checkParameterClientInfo(info);
            service.createClient(info);

            return Response.status(HttpURLConnection.HTTP_OK).build();
        } catch (IllegalArgumentException e) {
            log.error("InternalClientRestController.createClient: " + e.getMessage());
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorMsg.badRequest());
        } catch (NoResultException e) {
            log.error("InternalClientRestController.createClient: Unable to find organization for child with MESH-GUID:" + info.getChildrenPersonGUID());
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorMsg.notFound());
        } catch (Exception e){
            log.error("InternalClientRestController.createClient: Internal Error: ", e);
            return generateResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, ErrorMsg.internalError());
        }
    }

    @GET
    @Path(value = "client")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkClient(@QueryParam(value = "personGuid") String personGuid){
        try {
            checkParameter("personGuid", personGuid);
            ClientInfo clientInfo = service.getClientByMeshGUID(personGuid);

            return generateResponse(HttpURLConnection.HTTP_OK, clientInfo);
        } catch (IllegalArgumentException e) {
            log.error("InternalClientRestController.checkClient: " + e.getMessage());
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorMsg.badRequest());
        } catch (NoResultException e) {
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorMsg.notFound());
        }
    }

    @PUT
    @Path(value = "client")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateClient(@RequestBody ClientInfo info){
        try {
            checkParameterClientInfo(info);
            service.updateClient(info);

            return Response.status(HttpURLConnection.HTTP_OK).build();
        } catch (IllegalArgumentException e) {
            log.error("InternalClientRestController.updateClient: " + e.getMessage());
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorMsg.badRequest());
        } catch (NotFoundException e) {
            log.error("InternalClientRestController.updateClient: Unable to find guardian by MESH-GUID:" + info.getPersonGUID());
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorMsg.notFound());
        } catch (Exception e){
            log.error("InternalClientRestController.updateClient: Internal Error: ", e);
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
            log.error("InternalClientRestController.deleteClient: " + e.getMessage());
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorMsg.badRequest());
        } catch (NotFoundException e) {
            log.error("InternalClientRestController.deleteClient: Unable to find guardian by MESH-GUID:" + personGuid);
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorMsg.notFound());
        } catch (Exception e){
            log.error("InternalClientRestController.deleteClient: Internal Error: ", e);
            return generateResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, ErrorMsg.internalError());
        }
    }

    @POST
    @Path("guardians")
    public Response processGuardianRelations(@RequestBody GuardianRelationInfo guardianRelationInfo){
        try {
            checkParameterGuardianRelationInfo(guardianRelationInfo);
            service.processRelation(guardianRelationInfo);

            return Response.status(HttpURLConnection.HTTP_OK).build();
        } catch (IllegalArgumentException e) {
            log.error("InternalClientRestController.processGuardianRelations: " + e.getMessage());
            return generateResponse(HttpURLConnection.HTTP_BAD_REQUEST, ErrorMsg.badRequest());
        } catch (NotFoundException e) {
            log.error("InternalClientRestController.processGuardianRelations: Unable to find child by MESH-GUID:" + guardianRelationInfo.getChildrenPersonGuid());
            return generateResponse(HttpURLConnection.HTTP_NOT_FOUND, ErrorMsg.notFound());
        } catch (Exception e){
            log.error("InternalClientRestController.processGuardianRelations: Internal Error: ", e);
            return generateResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, ErrorMsg.internalError());
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

    private void checkParameterClientInfo(ClientInfo info) {
        if (StringUtils.isBlank(info.getPersonGUID())) {
            throw new IllegalArgumentException("PersonGUID is empty");
        } else if (StringUtils.isBlank(info.getFirstname())) {
            throw new IllegalArgumentException("Firstname is empty");
        } else if (StringUtils.isBlank(info.getLastname())) {
            throw new IllegalArgumentException("Lastname is empty");
        } else if (info.getGenderId() == null) {
            throw new IllegalArgumentException("GenderId is empty");
        } else if (info.getBirthdate() == null) {
            throw new IllegalArgumentException("Birthdate is empty");
        }
        if (!CollectionUtils.isEmpty(info.getDocuments())) {
            for (DocumentInfo di : info.getDocuments()) {
                if (di.getIdMKDocument() == null) {
                    throw new IllegalArgumentException("IdMKDocument in documents is empty");
                } else if (StringUtils.isBlank(di.getNumber())) {
                    throw new IllegalArgumentException("Number in documents is empty");
                } else if (di.getDocumentType() == null) {
                    throw new IllegalArgumentException("DocumentType is empty");
                }
            }
        }
    }

    private void checkParameterGuardianRelationInfo(GuardianRelationInfo info){
        if (StringUtils.isBlank(info.getChildrenPersonGuid())) {
            throw new IllegalArgumentException("ChildrenPersonGuid is empty");
        } else if(CollectionUtils.isEmpty(info.getGuardianPersonGuids())){
            throw new IllegalArgumentException("GuardianPersonGuids is empty");
        }
    }
}
