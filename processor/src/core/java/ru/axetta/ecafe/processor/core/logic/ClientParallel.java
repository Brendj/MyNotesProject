package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxOrgParallel;
import ru.axetta.ecafe.processor.core.persistence.foodbox.FoodBoxParallelType;

public class ClientParallel {

    public static void addFoodBoxModifire (Client client)
    {
        //Автоматическое открытие/закрытие фудбокса в зависимости от параллели
        if (verifyParallelForClient(client))
        {
            client.setFoodboxAvailability(true);
        }
    }

    public static boolean verifyParallelForClient (Client client)
    {
        //Получаем параллель клиента
        Integer parallel = ClientParallel.getClientParallel(client);
        if (parallel ==  0)
        {
            return false;
        } else
        {
            boolean availParal = false;
            for (FoodBoxParallelType foodBoxParallelType: FoodBoxParallelType.FoodBoxByParallel.getParallelTypes())
            {
                //Проверяем актуальность параллели
                if (foodBoxParallelType.getParallel().equals(parallel))
                {
                    availParal = true;
                    break;
                }
            }
            if (availParal)
            {
                //Проверяем параллель на доступность
                for (FoodBoxOrgParallel foodBoxOrgParallel: client.getOrg().getFoodBoxParallels())
                {
                    if (foodBoxOrgParallel.getParallel().equals(parallel) && foodBoxOrgParallel.getAvailable().equals(false))
                    {
                        return false;
                    }
                }
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    //Получение параллели клиента (0 - параллель не определена)
    public static Integer getClientParallel (Client client)
    {
        //Получаем параллель клиента
        Integer parallel;
        try {
            parallel = extractDigits(client.getParallel());
        } catch (Exception e) //т.е. не указана параллель
        {
            parallel = 0;
        }
        if (parallel == 0)
        {
            //Получаем параллель через название группы
            Integer clas;
            try {
                clas = extractDigits(client.getClientGroup().getGroupName());
            } catch (Exception e) //т.е. в названии группы нет чисел
            {
                clas = 0;
            }
            parallel = clas;
        }
        return parallel;
    }

    public static Integer extractDigits(String src) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (Character.isDigit(c)) {
                builder.append(c);
            } else {
                return Integer.valueOf(builder.toString());
            }
        }
        return Integer.valueOf(builder.toString());
    }
}
