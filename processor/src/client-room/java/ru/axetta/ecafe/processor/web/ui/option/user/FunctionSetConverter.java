/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOClientRoomService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
/*@Component
@Scope("singleton")*/
public class FunctionSetConverter implements Converter {
  /*  @PersistenceContext
    private EntityManager entityManager;*/
    private static final Logger logger = LoggerFactory.getLogger(FunctionSetConverter.class);
    private static final int MAX_LEN = 32;
    private static final String TAIL_FILL = "...";

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string) {
        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        DAOClientRoomService daoService= DAOClientRoomService.getInstance();
        StringBuilder stringBuilder = new StringBuilder();

        try {

            boolean first = true;
            for (Object currObject : (Set) object) {
                if (!first) {
                    stringBuilder.append(", ");
                }
                Long idOfFunction = (Long) currObject;


                Function function = daoService.getFunction(idOfFunction).get(0);

                stringBuilder.append(function.getFunctionName());
                first = false;
                if (stringBuilder.length() > MAX_LEN) {
                    break;
                }
            }

        } catch (Exception e) {
            logger.error("Failed to convert function set", e);
            return "";
        }
        int len = stringBuilder.length();
        if (len > MAX_LEN) {
            return stringBuilder.substring(0, MAX_LEN - TAIL_FILL.length()) + TAIL_FILL;
        }
        return stringBuilder.toString();
    }

}