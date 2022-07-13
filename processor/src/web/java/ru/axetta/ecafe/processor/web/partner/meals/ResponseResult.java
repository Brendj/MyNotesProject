package ru.axetta.ecafe.processor.web.partner.meals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxPreorder;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.partner.meals.models.OrderErrorInfo;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ResponseResult {
    private Logger logger = LoggerFactory.getLogger(MealsController.class);
    public Result errorAuth()
    {
        Result result = new Result();
        logger.error("Не удалось авторизовать пользователя");
        result.setCode(ResponseCodes.RC_WRONG_KEY.getCode().toString());
        result.setDescription(ResponseCodes.RC_WRONG_KEY.toString());
        return result;
    }

    public List<Result> errorTimeParse(Exception e)
    {
        Result result = new Result();
        logger.error("Ошибка при получении парсинга времени работы Фудбокса", e);
        result.setCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
        result.setDescription(ResponseCodes.RC_INTERNAL_ERROR.toString());
        List<Result> results = new ArrayList<>();
        results.add(result);
        return results;
    }

    public Result emptyContractId()
    {
        logger.error("Отсутствует contractId");
        return wrongRequest();
    }

    public Result wrongFormatContractId(String contractIdStr, Exception e)
    {
        logger.error(String.format("Неверный формат contract %s", contractIdStr), e);
        return wrongRequest();
    }

    public Result noXRID()
    {
        logger.error("Отсутствует x-request-id");
        return wrongRequest();
    }

    public Result noClient()
    {
        Result result = new Result();
        logger.error("Клиент не найден");
        result.setCode(ResponseCodes.RC_NOT_FOUND_CLIENT.getCode().toString());
        result.setDescription(ResponseCodes.RC_NOT_FOUND_CLIENT.toString());
        return result;
    }

    public Result orgDisableFoodBox()
    {
        Result result = new Result();
        logger.error("У организации не включен функционал фудбокса");
        result.setCode(ResponseCodes.RC_NOT_FOUND_ORG.getCode().toString());
        result.setDescription(ResponseCodes.RC_NOT_FOUND_ORG.toString());
        return result;
    }

    public Result wrongParallel()
    {
        Result result = new Result();
        logger.error("У клиента не подходящая параллель");
        result.setCode(ResponseCodes.RC_NOT_FOUND_AVAILABLE_PARALLEL.getCode().toString());
        result.setDescription(ResponseCodes.RC_NOT_FOUND_AVAILABLE_PARALLEL.toString());
        return result;
    }

    public Result clientNoFoodbox()
    {
        Result result = new Result();
        logger.error("У клиента не включен функционал фудбокса");
        result.setCode(ResponseCodes.RC_NOT_FOUND_AVAILABLE_CLIENT.getCode().toString());
        result.setDescription(ResponseCodes.RC_NOT_FOUND_AVAILABLE_CLIENT.toString());
        return result;
    }

//    public Result doubleIdentif(String xrequestStr)
//    {
//        Result result = new Result();
//        logger.error(String.format("Заказ с данным идентификатором уже зарегистрирвоан в системе. externalid = %s", xrequestStr));
//        result.setCode(ResponseCodes.RC_FOUND_FOODBOX.getCode().toString());
//        result.setDescription(ResponseCodes.RC_FOUND_FOODBOX.toString());
//        return result;
//    }

    public OrderErrorInfo notEndedPrev(FoodBoxPreorder foodBoxPreorder)
    {
        logger.error("У клиента имеются необработанные заказы");
        OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
        orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_HAVE_PREORDER.getCode());
        orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_HAVE_PREORDER.toString());
        orderErrorInfo.getDetails().setFoodboxOrderId(foodBoxPreorder.getIdFoodBoxPreorder());
        return orderErrorInfo;
    }

    public OrderErrorInfo moreLimitByDay(Long expenditureLimit)
    {
        logger.error("Сумма заказа превышает дневной лимит трат");
        OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
        orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_LIMIT.getCode());
        orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_LIMIT.toString());
        orderErrorInfo.getDetails().setBalanceLimit(expenditureLimit);
        return orderErrorInfo;
    }

    public OrderErrorInfo moreLimit(Long balance)
    {
        logger.error("Сумма заказа превышает баланс клиента");
        OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
        orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_NOMONEY.getCode());
        orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_NOMONEY.toString());
        orderErrorInfo.getDetails().setBalance(balance);
        return orderErrorInfo;
    }

    public Result internalError(Exception e, int type)
    {
        Result result = new Result();
        if (type == 1)
            logger.error("Ошибка при сохранении заказа для Фудбокса", e);
        if (type == 2)
            logger.error("Ошибка при получении заказа для Фудбокса", e);
        if (type == 3)
            logger.error("Ошибка при сохранении флага для клиента", e);
        result.setCode(ResponseCodes.RC_INTERNAL_ERROR.getCode().toString());
        result.setDescription(ResponseCodes.RC_INTERNAL_ERROR.toString());
        return result;
    }

    public OrderErrorInfo moreCountDish(Long countDish)
    {
        OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
        orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_DISH_COUNT.getCode());
        orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_DISH_COUNT.toString());
        orderErrorInfo.getDetails().setAmount(countDish);
        return orderErrorInfo;
    }

    public OrderErrorInfo allDishNotAvailable(String xrequestStr)
    {
        logger.error(String.format("Все блюда из заказа не доступны foodBoxid: %s", xrequestStr));
        OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
        orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_NO_DISH.getCode());
        orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_NO_DISH.toString());
        return orderErrorInfo;
    }

    public OrderErrorInfo errorCell(Long orgId)
    {
        logger.error(String.format("У организации с id = %s нет свободных ячеек фудбокса", orgId));
        OrderErrorInfo orderErrorInfo = new OrderErrorInfo();
        orderErrorInfo.setCode(ResponseCodesError.RC_ERROR_CELL.getCode());
        orderErrorInfo.setInformation(ResponseCodesError.RC_ERROR_CELL.toString());
        return orderErrorInfo;
    }

    public Result wrongFormatFrom(String fromStr, Exception e)
    {
        logger.error(String.format("Неверный формат from %s", fromStr), e);
        return wrongRequest();
    }

    public Result wrongFormatTo(String toStr, Exception e)
    {
        logger.error(String.format("Неверный формат to %s", toStr), e);
        return wrongRequest();
    }

    public Result wrongFormatSort(String sortStr, Exception e)
    {
        logger.error(String.format("Неверный формат sort %s", sortStr), e);
        return wrongRequest();
    }

    public Result wrongFormatFoodBoxOrder(String foodboxOrderId, Exception e)
    {
        logger.error(String.format("Неверный формат foodboxOrderId %s", foodboxOrderId), e);
        return wrongRequest();
    }

    public Result notFindPreorder (Long isppIdFoodbox)
    {
        Result result = new Result();
        logger.error(String.format("Не найден заказ с id %s", isppIdFoodbox));
        result.setCode(ResponseCodes.RC_NOT_FOUND_FOODBOX.getCode().toString());
        result.setDescription(ResponseCodes.RC_NOT_FOUND_FOODBOX.toString());
        return result;
    }

    public Result wrongOnDate ()
    {
        logger.error("Неверный формат onDate");
        return wrongRequest();
    }

    public Result emptyFoodBoxAvailable ()
    {
        logger.error("Отсутствует foodBoxAvailable");
        return wrongRequest();
    }

    public Result wrongFoodBoxAvailable ()
    {
        logger.error("Неверный формат foodBoxAvailable");
        return wrongRequest();
    }

    public Result ok ()
    {
        Result result = new Result();
        logger.error("Все ОК");
        result.setCode(ResponseCodes.RC_OK.getCode().toString());
        result.setDescription(ResponseCodes.RC_OK.toString());
        return result;
    }

    private Result wrongRequest()
    {
        Result result = new Result();
        result.setCode(ResponseCodes.RC_WRONG_REQUST.getCode().toString());
        result.setDescription(ResponseCodes.RC_WRONG_REQUST.toString());
        return result;
    }
}
