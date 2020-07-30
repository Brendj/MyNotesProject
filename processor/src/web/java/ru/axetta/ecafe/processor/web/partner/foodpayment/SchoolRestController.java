/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.foodpayment;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.RefreshToken;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.foodpayment.QueryData.*;
import ru.axetta.ecafe.processor.web.token.security.jwt.JwtTokenProvider;
import ru.axetta.ecafe.processor.web.token.security.service.JWTLoginService;
import ru.axetta.ecafe.processor.web.token.security.service.JWTLoginServiceImpl;
import ru.axetta.ecafe.processor.web.token.security.service.JwtUserDetailsImpl;
import ru.axetta.ecafe.processor.web.token.security.util.login.JwtLoginErrors;
import ru.axetta.ecafe.processor.web.token.security.util.login.JwtLoginException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jboss.resteasy.spi.validation.ValidateRequest;
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
            checkPermission(authentication, User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification());
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            if(!groupManagementService.isFriendlyOrg(createGroupData.getOrgId(), jwtUserDetails.getIdOfOrg().longValue()))
                throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                        GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
            //Проверка на доступ определенной роли
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
    public Response groupList(){
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
            checkPermission(authentication, User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification());
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            List<GroupInfo> groupInfoList = groupManagementService.getOrgGroups(jwtUserDetails.getIdOfOrg());
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseGroups.setGroups(groupInfoList);
            responseGroups.setErrorCode(0);
            responseGroups.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseGroups).build();

        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: ", e));
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
    public Response employees(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            checkPermission(authentication, User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification());
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            ResponseEmployees responseEmployees = new ResponseEmployees();
            List<GroupEmployee> groupEmployeeList = groupManagementService.getEmployees(jwtUserDetails.getIdOfOrg());
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseEmployees.setEmployeeGroups(groupEmployeeList);
            responseEmployees.setErrorCode(0);
            responseEmployees.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseEmployees).build();
        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: %s", e.toString()), e);
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
            checkPermission(authentication, User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification());
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            if(!groupManagementService.isFriendlyOrg(editEmployeeData.getOrgId(), jwtUserDetails.getIdOfOrg().longValue()))
                throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                        GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
            //Проверка на доступ определенной роли
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
            checkPermission(authentication, User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification());
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            //Проверка на доступ определенной роли
            ResponseClients responseClients = groupManagementService.getClientsList(clientsListRequest.getGroupsList(),
                    jwtUserDetails.getIdOfOrg());
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseClients.setErrorCode(0);
            responseClients.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseClients).build();
        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: %s", e.toString()), e);
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
    public Response getDiscounts() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            checkPermission(authentication, User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification());
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            ResponseDiscounts responseDiscounts = groupManagementService.getDiscountsList(jwtUserDetails.getIdOfOrg());
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseDiscounts.setErrorCode(0);
            responseDiscounts.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseDiscounts).build();
        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: %s", e.toString()), e);
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
            checkPermission(authentication, User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification());
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            //Проверка на доступ определенной роли
            ResponseDiscountClients responseDiscounts = groupManagementService
                    .processDiscountClientsList(jwtUserDetails.getIdOfOrg(),
                            discountClientsListRequest.getDiscountId(),
                            discountClientsListRequest.getStatus(), discountClientsListRequest.getClients());
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseDiscounts.setErrorCode(0);
            responseDiscounts.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseDiscounts).build();
        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: %s", e.toString()), e);
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
            checkPermission(authentication, User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification());
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            //Проверка на доступ определенной роли
            ResponseDiscountGroups responseDiscounts = groupManagementService
                    .processDiscountGroupsList(jwtUserDetails.getIdOfOrg(),
                            discountClientsListRequest.getDiscountId(),
                            discountClientsListRequest.getStatus(), discountClientsListRequest.getGroups());
            persistenceTransaction.commit();
            persistenceTransaction = null;
            responseDiscounts.setErrorCode(0);
            responseDiscounts.setErrorMessage("Ok");
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseDiscounts).build();

        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: %s", e.toString()), e);
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
    @Path(value = "editclientsgroup")
    public Response editClientsGroup(EditClientsGroupRequest editClientsGroupRequest) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            checkPermission(authentication, User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification());
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            if(!groupManagementService.isFriendlyOrg(editClientsGroupRequest.getNewOrgId(), jwtUserDetails.getIdOfOrg())){
                throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                        GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
            }
            ClientGroup clientGroup = groupManagementService.getClientGroupByOrgIdAndGroupName(editClientsGroupRequest.getNewOrgId(),
                    editClientsGroupRequest.getNewGroupName());
            List<Client> clients = groupManagementService.getClientsForContractIds(clientGroup,
                    editClientsGroupRequest.getContractIds(), editClientsGroupRequest.isStrictEditMode());
            ResponseEditClientsGroup responseEditClientsGroup = new ResponseEditClientsGroup();
            responseEditClientsGroup.setGroups(groupManagementService.moveClientsInGroup(clientGroup,clients, jwtUserDetails.getUsername()));
            responseEditClientsGroup.setErrorCode(0);
            responseEditClientsGroup.setErrorMessage("OK");
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseEditClientsGroup).build();

        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: %s", e.toString()), e);
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
    @Path(value = "editgroupclientsgroup")
    public Response editGroupClientsGroup(EditGroupClientsGroupRequest editGroupClientsGroupRequest) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            checkPermission(authentication, User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification());
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            //Проверка на доступ определенной роли
            if(!groupManagementService.isFriendlyOrg(editGroupClientsGroupRequest.getNewOrgId(), jwtUserDetails.getIdOfOrg())){
                throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                        GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
            }
            ClientGroup clientGroup = groupManagementService.getClientGroupByOrgIdAndGroupName(editGroupClientsGroupRequest.getNewOrgId(),
                    editGroupClientsGroupRequest.getNewGroupName());
            List<Client> clients = groupManagementService.getClientsForGroups(clientGroup,
                    editGroupClientsGroupRequest.getOldGroups(), editGroupClientsGroupRequest.isStrictEditMode());
            ResponseEditClientsGroup responseEditClientsGroup = new ResponseEditClientsGroup();
            responseEditClientsGroup.setGroups(groupManagementService.moveClientsInGroup(clientGroup,clients, jwtUserDetails.getUsername()));
            responseEditClientsGroup.setErrorCode(0);
            responseEditClientsGroup.setErrorMessage("OK");
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseEditClientsGroup).build();

        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: %s", e.toString()), e);
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
    @Path(value = "friendlyorgs")
    public Response friendlyOrgs() {
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
            Long orgId = groupManagementService.getIdOfOrgFromUser(jwtUserDetails.getUsername());
            ResponseFriendlyOrgs responseFriendlyOrgs = new ResponseFriendlyOrgs(groupManagementService.getFriendlyOrgs(orgId));
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return Response.status(HttpURLConnection.HTTP_OK).entity(responseFriendlyOrgs).build();

        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: %s", e.toString()), e);
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
    @ValidateRequest
    @Path(value = "createclient")
    public Response createClient(CreateClientRequestDTO createClientRequestDTO) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        IGroupManagementService groupManagementService;
        try{
            createClientRequestDTO.validateRequest();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            groupManagementService = new GroupManagementService(persistenceSession);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            checkPermission(authentication, User.DefaultRole.INFORMATION_SYSTEM_OPERATOR.getIdentification().intValue());
            JwtUserDetailsImpl jwtUserDetails = (JwtUserDetailsImpl) authentication.getPrincipal();
            ClientGroup clientGroup;
            try {
                clientGroup = groupManagementService.getClientGroupByOrgIdAndGroupName(jwtUserDetails.getIdOfOrg(),
                        createClientRequestDTO.getGroupName());
            }
            catch (RequestProcessingException ex){
                logger.info(String.format("Client group %s with org id %d not found. Create client group.", createClientRequestDTO.getGroupName(), jwtUserDetails.getIdOfOrg()));
                clientGroup = DAOUtils.createClientGroup(persistenceSession, jwtUserDetails.getIdOfOrg(),
                        createClientRequestDTO.getGroupName());
            }
            groupManagementService.createClient(clientGroup, CreateClientRequestDTO.convertRequestToClient(createClientRequestDTO));
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return Response.status(HttpURLConnection.HTTP_OK).entity(new Result(100, "OK")).build();

        }
        catch (RequestProcessingException e){
            logger.error(String.format("Bad request: %s", e.toString()), e);
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

    private void checkPermission(Authentication authentication, Integer idOfGroup) throws Exception {
        checkAuthentication(authentication);
        Integer userIdOfGroup = ((JwtUserDetailsImpl) authentication.getPrincipal()).getIdOfRole();
        if(userIdOfGroup == null || idOfGroup == null)
            throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
        if(userIdOfGroup.intValue() != idOfGroup.intValue())
            throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
    }

    private void checkAuthentication(Authentication authentication) throws Exception {
        if(authentication == null || authentication.getPrincipal() == null || !authentication.isAuthenticated()){
            throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
        }
        if(!authentication.isAuthenticated())
            throw new RequestProcessingException(GroupManagementErrors.USER_NOT_FOUND.getErrorCode(),
                    GroupManagementErrors.USER_NOT_FOUND.getErrorMessage());
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
