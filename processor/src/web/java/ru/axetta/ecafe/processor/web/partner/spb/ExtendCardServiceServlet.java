/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.spb;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.Result;
import ru.axetta.ecafe.processor.web.partner.integra.soap.ClientRoomController;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@WebServlet(
        name = "ExtendCardServiceServlet",
        description = "ExtendCardServiceServlet",
        urlPatterns = {"/cardservice"}
)
public class ExtendCardServiceServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ExtendCardServiceServlet.class);
    private static final String PARAM_UID = "uid";
    private static final String PARAM_CONTRACTID = "contractId";
    private static ClientRoomController controller = null;
    private static final String PARAM_CODE = "code";
    private static final String PARAM_DESCRIPTION = "description";
    private static final String PARAM_FIO = "fio";

    private static final String PARAM_OPERATION = "operation";
    private static final String PARAM_CARDPRINTEDNO = "cardprintedno";

    private static final String OPERATION_EXTENDCARD = "extendcard";
    private static final String OPERATION_GETCLIENTINFO = "getclientinfo";

    @Override
    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
        try {
            authorizeRequest(httpRequest);
            Map<String, String> map = parseParams(httpRequest);
            String operation = map.get(PARAM_OPERATION);
            if (operation.equals(OPERATION_EXTENDCARD)) {
                String cardno = map.get(PARAM_UID);
                String contractId = map.get(PARAM_CONTRACTID);

                logger.info(String.format("Incoming request to extend card valid date: uid=%s, contractId=%s", cardno,
                        contractId));

                ClientRoomController controller = createController();
                Result result = controller.extendValidDateOfCard(Long.parseLong(contractId), Long.parseLong(cardno));
                String responseString = String.format("%s=%s&%s=%s", PARAM_CODE, result.resultCode.toString(), PARAM_DESCRIPTION, result.description);
                serializeResponse(responseString, httpResponse);
            }

            if (operation.equals(OPERATION_GETCLIENTINFO)) {
                String cardprintedno = map.get(PARAM_CARDPRINTEDNO);
                Long value = Long.parseLong(cardprintedno);
                Result result = new Result(0L, "OK");
                Client client = null;
                try {
                    client = DAOReadonlyService.getInstance().getClientByCardPrintedNo(value);
                } catch (Exception e) {
                    if (e.getMessage().equals(DAOReadonlyService.CARD_NOT_FOUND) || e.getMessage().equals(DAOReadonlyService.SEVERAL_CARDS_FOUND)) {
                        result.resultCode = 100L;
                        result.description = e.getMessage();
                        String responseString = String.format("%s=%s&%s=%s", PARAM_CODE, result.resultCode.toString(), PARAM_DESCRIPTION, result.description);
                        serializeResponse(responseString, httpResponse);
                        return;
                    } else {
                        throw e;
                    }
                }
                StringBuilder sb = new StringBuilder();
                sb.append(PARAM_CODE + "=" + result.resultCode.toString());
                sb.append("&");
                sb.append(PARAM_DESCRIPTION + "=" + result.description);
                sb.append("&");
                sb.append(PARAM_CONTRACTID + "=" + client.getContractId().toString());
                sb.append("&");
                sb.append(PARAM_FIO + "=" + client.getPerson().getFullName());
                serializeResponse(sb.toString(), httpResponse);
            }
        } catch (Exception e) {
            logger.error("Error in SpbCardService", e);
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected void serializeResponse(String responseString, HttpServletResponse httpResponse) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(byteStream, true, "UTF-8");
        try {
            printStream.print(responseString);
        } catch (Exception e) {
            logger.error("Failed to serialize response", e);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        printStream.close();
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentLength(byteStream.size());
        byteStream.writeTo(httpResponse.getOutputStream());
    }

    private Map<String, String> parseParams(HttpServletRequest httpRequest) throws Exception {
        String paramString = httpRequest.getQueryString();
        String[] arr = paramString.split("&");
        Map<String, String> map = new HashMap<>();
        for (String param : arr) {
            String[] arr2 = param.split("=");
            map.put(arr2[0], arr2[1]);
        }
        return map;
    }

    public static ClientRoomController createController() throws Exception {
        if (controller == null) {
            Service service = Service.create(new URL("http://localhost:8080/processor/soap/client?wsdl"), new QName("http://soap.integra.partner.web.processor.ecafe.axetta.ru/",
                    "ClientRoomControllerWSService"));
            controller = service.getPort(ClientRoomController.class);
        }
        return controller;
    }

    @Override
    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException,
            IOException {
        doPost(httpRequest, httpResponse);
    }

    public void authorizeRequest(HttpServletRequest request) throws Exception {
        String apiKey = RuntimeContext.getInstance().getExtendCardServiceApiKey();
        String requestKey = request.getHeader("key");
        if (StringUtils.isEmpty(requestKey) || !requestKey.equals(apiKey)){
            throw new Exception("Not valid key");
        }
    }
}
