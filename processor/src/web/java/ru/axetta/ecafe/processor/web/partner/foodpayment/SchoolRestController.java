/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData.CreateGroupData;
import ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData.EditEmployeeData;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.List;


@Path(value = "")
@Controller
public class SchoolRestController {

    private Logger logger = LoggerFactory.getLogger(SchoolRestController.class);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "creategroup")
    public Response createGroup(CreateGroupData createGroupData){
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            groupManagementService.addOrgGroup(createGroupData.getOrgId(), createGroupData.getGroupName());
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return Response.status(HttpURLConnection.HTTP_CREATED).entity(new Result(0, "Ok")).build();
        }
        catch (RequestProcessingException e){
            logger.error(("Bad request: "+createGroupData.toString()+";"+e.toString()), e);
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(new Result(e.getErrorCode(), e.getErrorMessage())).build();
        }
        catch (Exception e){
            logger.error("Internal error: "+e.getMessage(), e);
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(new Result(100, "Ошибка сервера")).build();
        }
        finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "grouplist")
    public Response groupList(@QueryParam(value = "token") String token, @QueryParam(value = "userId") Long userId,
            @QueryParam(value = "orgId") Long orgId){
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ResponseGroups responseGroups = new ResponseGroups();
            groupManagementService = new GroupManagementService(persistenceSession);
            List<GroupInfo> groupInfoList = groupManagementService.getOrgGroups(orgId);
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseGroups.setGroups(groupInfoList);
            responseGroups.setErrorCode(0);
            responseGroups.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseGroups).build();

        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: token = %s; userId = %o; orgId = %o; %s",token,userId, orgId, e.toString()), e);
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(new Result(e.getErrorCode(), e.getErrorMessage())).build();
        }
        catch (Exception e){
            logger.error("Internal error: "+e.getMessage(), e);
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(new Result(0, "Ошибка сервера")).build();
        }
        finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "employees")
    public Response employees(@QueryParam(value = "token") String token, @QueryParam(value = "userId") Long userId,
            @QueryParam(value = "orgId") Long orgId){
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            ResponseEmployees responseEmployees = new ResponseEmployees();
            List<GroupEmployee> groupEmployeeList = groupManagementService.getEmployees(orgId);
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseEmployees.setEmployeeGroups(groupEmployeeList);
            responseEmployees.setErrorCode(0);
            responseEmployees.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseEmployees).build();
        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: token = %s; userId = %o; orgId = %o; %s",token,userId, orgId, e.toString()), e);
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(new Result(e.getErrorCode(), e.getErrorMessage())).build();
        }
        catch (Exception e){
            logger.error("Internal error: "+e.getMessage(), e);
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(new Result(100,"Ошибка сервера")).build();
        }
        finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "editemployee")
    public Response editEmployee(EditEmployeeData editEmployeeData){
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            groupManagementService.editEmployee(editEmployeeData.getOrgId(), editEmployeeData.getGroupName(),
                    editEmployeeData.getContractId(), editEmployeeData.getStatus());
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return Response.status(HttpURLConnection.HTTP_OK).entity(new Result(0, "Ok")).build();
        }
        catch (RequestProcessingException e){
            logger.error(("Bad request: "+editEmployeeData.toString()+";"+e.toString()), e);
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(new Result(e.getErrorCode(), e.getErrorMessage())).build();
        }
        catch (Exception e){
            logger.error("Internal error: "+e.getMessage(), e);
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(new Result(100, "Ошибка сервера")).build();
        }
        finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "clientslist")
    public Response clientsList(ClientsListRequest clientsListRequest) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            ResponseClients responseClients = groupManagementService.getClientsList(clientsListRequest.getGroupsList(), clientsListRequest.getOrgId());
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseClients.setErrorCode(0);
            responseClients.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseClients).build();
        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: token = %s; userId = %o; orgId = %o; %s", clientsListRequest.getToken(),
                    clientsListRequest.getUserId(), clientsListRequest.getOrgId(), e.toString()), e);
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(new Result(e.getErrorCode(), e.getErrorMessage())).build();
        }
        catch (Exception e){
            logger.error("Internal error: " + e.getMessage(), e);
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(new Result(100,"Ошибка сервера")).build();
        }
        finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "discounts")
    public Response getDiscounts(@QueryParam(value = "token") String token, @QueryParam(value = "userId") Long userId,
            @QueryParam(value = "orgId") Long orgId) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            ResponseDiscounts responseDiscounts = groupManagementService.getDiscountsList(orgId);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseDiscounts.setErrorCode(0);
            responseDiscounts.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseDiscounts).build();
        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: token = %s; userId = %o; orgId = %o; %s", token,
                    userId, orgId, e.toString()), e);
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(new Result(e.getErrorCode(), e.getErrorMessage())).build();
        }
        catch (Exception e){
            logger.error("Internal error: " + e.getMessage(), e);
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(new Result(100,"Ошибка сервера")).build();
        }
        finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "editdiscountclients")
    public Response editDiscountClients(DiscountClientsListRequest discountClientsListRequest) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            ResponseDiscountClients responseDiscounts = groupManagementService.processDiscountClientsList(discountClientsListRequest);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseDiscounts.setErrorCode(0);
            responseDiscounts.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseDiscounts).build();
        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: token = %s; userId = %o; orgId = %o; %s", discountClientsListRequest.getToken(),
                    discountClientsListRequest.getUserId(), discountClientsListRequest.getOrgId(), e.toString()), e);
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity(new Result(e.getErrorCode(), e.getErrorMessage())).build();
        }
        catch (Exception e){
            logger.error("Internal error: " + e.getMessage(), e);
            return Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity(new Result(100,"Ошибка сервера")).build();
        }
        finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

}
