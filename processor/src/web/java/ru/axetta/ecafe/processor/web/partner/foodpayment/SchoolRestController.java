/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.RefreshToken;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData.CreateGroupData;
import ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData.EditEmployeeData;
import ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData.LoginData;
import ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData.RefreshTokenData;
import ru.axetta.ecafe.processor.web.token.security.jwt.JwtTokenProvider;
import ru.axetta.ecafe.processor.web.token.security.service.JWTLoginService;
import ru.axetta.ecafe.processor.web.token.security.service.JWTLoginServiceImpl;
import ru.axetta.ecafe.processor.web.token.security.service.JwtUserDetailsImpl;
import ru.axetta.ecafe.processor.web.token.security.util.login.JwtLoginErrors;
import ru.axetta.ecafe.processor.web.token.security.util.login.JwtLoginException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.List;


@Path(value = "/")
@Controller
public class SchoolRestController {

    private Logger logger = LoggerFactory.getLogger(SchoolRestController.class);


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "authorization/signin")
    public Response signin(@Context HttpServletRequest request, LoginData loginData){
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        JwtTokenProvider tokenService;
        JWTLoginService jwtLoginService;
        try{
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if(loginData.getUsername() == null || loginData.getPassword() == null)
                throw new JwtLoginException(JwtLoginErrors.USERNAME_IS_NULL.getErrorCode(),
                        JwtLoginErrors.USERNAME_IS_NULL.getErrorMessage());
            tokenService = new JwtTokenProvider();
            jwtLoginService = new JWTLoginServiceImpl();
            if(jwtLoginService.login(loginData.getUsername(), loginData.getPassword(), request.getRemoteAddr(), persistenceSession)){
                String token = tokenService.createToken(loginData.getUsername());
                String refreshToken = tokenService.createRefreshToken(loginData.getUsername(),request.getRemoteAddr(),persistenceSession);
                persistenceSession.flush();
                persistenceTransaction.commit();
                persistenceTransaction = null;
                return Response.status(HttpURLConnection.HTTP_OK).entity(new JwtLoginDTO(token, refreshToken)).build();
            }
            else
                throw new JwtLoginException(JwtLoginErrors.UNSUCCESSFUL_AUTHORIZATION.getErrorCode(),
                        JwtLoginErrors.UNSUCCESSFUL_AUTHORIZATION.getErrorMessage());

        }
        catch (JwtLoginException e){
            logger.error("Login error: "+e.getMessage(), e);
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(new Result(e.getErrorCode(), e.getErrorMessage())).build();
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
    @Path(value = "authorization/refreshtoken")
    public Response refreshToken(@Context HttpServletRequest request, RefreshTokenData refreshTokenData){
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        JwtTokenProvider tokenService;
        try{
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            tokenService = RuntimeContext.getAppContext().getBean(JwtTokenProvider.class);
            RefreshToken refreshToken = tokenService.refreshTokenIsValid(refreshTokenData.getRefreshToken(),request.getRemoteAddr(),persistenceSession);
            String accessToken = tokenService.createToken(refreshToken.getUser().getUserName());
            String newRefreshToken = tokenService.createRefreshToken(refreshToken.getUser().getUserName(),
                    request.getRemoteAddr(), persistenceSession);
            return Response.status(HttpURLConnection.HTTP_OK).entity(new JwtLoginDTO(accessToken, newRefreshToken)).build();

        }
        catch (JwtLoginException e){
            logger.error("Login error: "+e.getMessage(), e);
            return Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity(new Result(e.getErrorCode(), e.getErrorMessage())).build();
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
    @Path(value = "creategroup")
    public Response createGroup(@Context HttpServletRequest request,CreateGroupData createGroupData){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            checkAuthentication(authentication);
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            //Проверка на доступ определенной роли
            groupManagementService.checkPermission(jwtUserDetails.getIdOfRole(),
                    User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification(),
                    jwtUserDetails.getIdOfOrg().longValue(), createGroupData.getOrgId());
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
    public Response groupList(@QueryParam(value = "orgId") Long orgId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ResponseGroups responseGroups = new ResponseGroups();
            groupManagementService = new GroupManagementService(persistenceSession);
            checkAuthentication(authentication);
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            //Проверка на доступ определенной роли
            groupManagementService.checkPermission(jwtUserDetails.getIdOfRole(),
                    User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification(),
                    jwtUserDetails.getIdOfOrg().longValue(), orgId.longValue());
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
            logger.error(String.format("Bad request: orgId = %o; %s",orgId, e.toString()), e);
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
    public Response employees(@QueryParam(value = "orgId") Long orgId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            checkAuthentication(authentication);
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            //Проверка на доступ определенной роли
            groupManagementService.checkPermission(jwtUserDetails.getIdOfRole(),
                    User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification(),
                    jwtUserDetails.getIdOfOrg().longValue(), orgId.longValue());
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
            logger.error(String.format("Bad request: orgId = %o; %s", orgId, e.toString()), e);
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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            checkAuthentication(authentication);
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            //Проверка на доступ определенной роли
            groupManagementService.checkPermission(jwtUserDetails.getIdOfRole(),
                    User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification(),
                    jwtUserDetails.getIdOfOrg().longValue(), editEmployeeData.getOrgId());
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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            checkAuthentication(authentication);
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            //Проверка на доступ определенной роли
            groupManagementService.checkPermission(jwtUserDetails.getIdOfRole(),
                    User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification(),
                    jwtUserDetails.getIdOfOrg().longValue(), clientsListRequest.getOrgId().longValue());
            ResponseClients responseClients = groupManagementService.getClientsList(clientsListRequest.getGroupsList(), clientsListRequest.getOrgId());
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseClients.setErrorCode(0);
            responseClients.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseClients).build();
        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: orgId = %o; %s", clientsListRequest.getOrgId(), e.toString()), e);
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
    public Response getDiscounts(@QueryParam(value = "orgId") Long orgId) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            checkAuthentication(authentication);
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            //Проверка на доступ определенной роли
            groupManagementService.checkPermission(jwtUserDetails.getIdOfRole(),
                    User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification(),
                    jwtUserDetails.getIdOfOrg().longValue(), orgId.longValue());
            ResponseDiscounts responseDiscounts = groupManagementService.getDiscountsList(orgId);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseDiscounts.setErrorCode(0);
            responseDiscounts.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseDiscounts).build();
        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: token = %s; userId = %o; orgId = %o; %s", orgId, e.toString()), e);
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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            checkAuthentication(authentication);
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            //Проверка на доступ определенной роли
            groupManagementService.checkPermission(jwtUserDetails.getIdOfRole(),
                    User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification(),
                    jwtUserDetails.getIdOfOrg().longValue(), discountClientsListRequest.getOrgId().longValue());
            ResponseDiscountClients responseDiscounts = groupManagementService.processDiscountClientsList(discountClientsListRequest);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseDiscounts.setErrorCode(0);
            responseDiscounts.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseDiscounts).build();
        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: orgId = %o; %s", discountClientsListRequest.getOrgId(), e.toString()), e);
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
    @Path(value = "editdiscountgroups")
    public Response editDiscountGroups(DiscountGroupsListRequest discountClientsListRequest) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            checkAuthentication(authentication);
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            //Проверка на доступ определенной роли
            groupManagementService.checkPermission(jwtUserDetails.getIdOfRole(),
                    User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification(),
                    jwtUserDetails.getIdOfOrg().longValue(), discountClientsListRequest.getOrgId().longValue());
            ResponseDiscountGroups responseDiscounts = groupManagementService.processDiscountGroupsList(discountClientsListRequest);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseDiscounts.setErrorCode(0);
            responseDiscounts.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseDiscounts).build();

        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: orgId = %o; %s", discountClientsListRequest.getOrgId(), e.toString()), e);
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

    private void checkAuthentication(Authentication authentication) throws Exception {
        if(authentication == null || authentication.getPrincipal() == null || !authentication.isAuthenticated()){
            throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
        }
        if(!userDetailsIsValid((JwtUserDetailsImpl) authentication.getPrincipal())){
            throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
        }
    }

    private boolean userDetailsIsValid(JwtUserDetailsImpl userDetails){
        if(userDetails.isEnabled() && userDetails.getIdOfOrg() != null && userDetails.getIdOfRole() != null && userDetails.getIdOfUser() != null){
            return true;
        }
        return false;
    }



}
