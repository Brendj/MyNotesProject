package ru.axetta.ecafe.processor.web.partner.meals.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.partner.meals.ResponseCodes;
import ru.axetta.ecafe.processor.web.partner.meals.Result;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class MealsJwtFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION = "Authorization";
    public static final String MEALS_AUTHORIZATION = "auth-token";
    public static final String FOODBOX_DIR = "/foodbox/";
    public static final String NEWLINE = System.getProperty("line.separator");
    private static final ThreadLocal<SimpleDateFormat> simpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS"); }
    };
    public static final String LOGGING_FOR_MEALS = "ecafe.processor.meals.logging";

    @Autowired
    private MealsJwtProvider jwtProvider;

    @Override
    public void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = getMealsTokenFromRequest(servletRequest);
        try {
            MealsUserDetails customUserDetails;
            if (token == null || !token.equals("test")) {
                jwtProvider.validateToken(token);
                String msh = jwtProvider.getMshFromToken(token);
                customUserDetails = new MealsUserDetails(msh);
            } else
            {
                customUserDetails = new MealsUserDetails("test");
            }
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (MealsInvalidToken e) {
            SecurityContextHolder.clearContext();
            servletResponse.setContentType("application/json;charset=UTF-8");
            servletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Result result = new Result();
            result.setCode(ResponseCodes.RC_WRONG_KEY.getCode().toString());
            result.setDescription(ResponseCodes.RC_WRONG_KEY.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            List<Result> results = new ArrayList<>();
            results.add(result);
            objectMapper.writeValue(servletResponse.getOutputStream(), results);
        }

        if (availableLogging()) {
            ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(servletRequest);
            ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper(servletResponse);

            filterChain.doFilter(req, resp);
            logging(req, resp);

            resp.copyBodyToResponse();
        }
        else
        {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private void logging(ContentCachingRequestWrapper req, ContentCachingResponseWrapper resp)
    {
        try {
            String requestBody = new String(req.getContentAsByteArray(), StandardCharsets.UTF_8);
            String responseBody =  new String(resp.getContentAsByteArray(), StandardCharsets.UTF_8);

            String paramStringReq = req.getQueryString().replace("&", ";");

            StringBuilder paramStringHeadersReq = new StringBuilder();
            Enumeration<String> headerNamesReq = req.getHeaderNames();
            if (headerNamesReq != null) {
                while (headerNamesReq.hasMoreElements()) {
                    String header = headerNamesReq.nextElement();
                    paramStringHeadersReq.append(paramStringHeadersReq).append(header).append("=").append(req.getHeader(header)).append(";");
                }
            }

            StringBuilder paramStringHeadersResp = new StringBuilder();
            Collection<String> headerNamesResps = resp.getHeaderNames();
            for (String headerNamesResp:headerNamesResps)
            {
                paramStringHeadersReq.append(paramStringHeadersReq).
                        append(headerNamesResp).append("=").append(req.getHeader(headerNamesResp)).append(";");
            }

            requestBody = requestBody.replace("\n", "").replace("\r", "").replace("\t", "");
            responseBody = responseBody.replace("\n", "").replace("\r", "").replace("\t", "");
            File dir = new File(FOODBOX_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String foodboxFilePath = FOODBOX_DIR + CalendarUtils.formatToDateShortUnderscoreFormat(new Date()) + ".log";
            File foodboxFile = new File(foodboxFilePath);
            if (!foodboxFile.exists()) {
                foodboxFile.createNewFile();
            }
            FileWriter writer = new FileWriter(foodboxFile, true);
            String date = simpleDateFormat.get().format(new Date());
            writer.append(date + " in: " + NEWLINE +
                    "\tQuery: " + paramStringReq + NEWLINE +
                    "\tHeaders: " + paramStringHeadersReq + NEWLINE +
                    "\tBody: " + requestBody + NEWLINE);
            writer.append(date + " out: " + NEWLINE +
                    "\tHeaders: " + paramStringHeadersResp + NEWLINE +
                    "\tBody: " + responseBody + NEWLINE);
            writer.close();
        } catch (Exception e)
        {
            logger.error("Ошибка при логировании фудбокса", e);
            e.printStackTrace();
        }
    }

    private Boolean availableLogging()
    {
        return Boolean.parseBoolean(RuntimeContext.getInstance().getConfigProperties().
                getProperty(LOGGING_FOR_MEALS, "false"));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String servletPath = request.getServletPath();
        if (servletPath != null && !servletPath.toLowerCase().startsWith("/ispp/meals/v1")) {
            return true;
        }
        return super.shouldNotFilter(request);
    }

    private String getMealsTokenFromRequest(HttpServletRequest request) {
        return request.getHeader(MEALS_AUTHORIZATION);
    }
}
