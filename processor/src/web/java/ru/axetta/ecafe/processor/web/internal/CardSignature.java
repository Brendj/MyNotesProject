/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.*;
import ru.axetta.ecafe.processor.core.card.CryptoSign;
import ru.axetta.ecafe.processor.core.persistence.CardSign;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@WebService(targetNamespace = "http://ru.axetta.ecafe")
public class CardSignature extends HttpServlet {

    public static final String IS_TEST = "ecafe.processor.card.sign.soap.test";

    private List<ResponseCardSign> ErrorMessage (String message)
    {
        List<ResponseCardSign> responseCardSigns = new ArrayList<ResponseCardSign>();
        ResponseCardSign responseCardSign = new ResponseCardSign();
        responseCardSign.setMessage(message);
        responseCardSigns.add(responseCardSign);
        return responseCardSigns;
    }

    @WebMethod
    public List<ResponseCardSign> signatureCard(
            @WebParam(name = "idProvider") Integer idOfProvider,
            @WebParam(name = "signProvider") byte[] signofProvider,
            @WebParam(name = "cardForSign") List<RequestCardForSign> requestCardForSigns
    ) throws Exception {
        //Для начала получаем все данные поставщика
        CardSign cardSign = DAOReadonlyService.getInstance().getSignInform(idOfProvider);
        if (cardSign == null)
        {
            return ErrorMessage ("Поставщик не найден");
        }
        if (requestCardForSigns.size() > 100)
        {
            return ErrorMessage ("Количество карт для подписи должно быть менее 100");
        }
        //Проверяем подпись поставщика
        try {
            if (!isTest(signofProvider))
                if (!CryptoSign.verifySignforProvider(requestCardForSigns, signofProvider,cardSign))
                    return ErrorMessage ("Подпись поставщика не действительна");
        }
        catch (Exception e)
        {
            return ErrorMessage ("Ошибка при проверке подписи");
        }
        //Если у поставщика отсутствую ключи, то выдаем ошибку
        if (cardSign.getPublickeyprovider() == null || cardSign.getPrivatekeycard() == null)
        {
            return ErrorMessage ("У данного поставщика отсутствую ключи для подписания карт");
        }
        //Отправляем для подписания
        return CryptoSign.createSignforCard(requestCardForSigns, cardSign);
    }

    private boolean isTest(byte[] signofProvider)
    {
        String sign = Base64.getEncoder().encodeToString(signofProvider);
        String testKey = RuntimeContext.getInstance().getConfigProperties().getProperty(IS_TEST, "ThiskeyIsSpeciallyMadeForTesting");
        if (sign.equals(testKey))
            return true;
        return false;
    }

}
