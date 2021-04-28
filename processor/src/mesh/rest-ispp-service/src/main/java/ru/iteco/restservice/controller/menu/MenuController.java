package ru.iteco.restservice.controller.menu;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.iteco.restservice.controller.menu.responsedto.CategoryItem;
import ru.iteco.restservice.controller.menu.responsedto.MenuListResponse;
import ru.iteco.restservice.servise.MenuService;

import javax.validation.constraints.PositiveOrZero;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/menu")
@Tag(name = "Menu-controller", description = "Школьное меню")
public class MenuController {
    private final Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    MenuService menuService;

    @GetMapping("/getMenuList")
    @ResponseBody
    @Operation(
            summary = "Получение меню буфета школьной столовой",
            description = "Получение меню буфета школьной столовой по номеру лицевого счета клиента")
    public MenuListResponse getMenuList(@Parameter(description = "Номер лицевого счета клиента", example = "13177")
                                        @RequestParam @PositiveOrZero Long contractId,
                                        @Parameter(description = "Дата запроса меню", example = "2021-04-23")
                                        @RequestParam Date date) {
        try {
            MenuListResponse response = new MenuListResponse();
            List<CategoryItem> items = menuService.getMenuList(date, contractId);
            response.getCategoryItems().addAll(items);
            return response;
        } catch (Exception e){
            logger.error("Exception in getMenuList: ", e);
            throw e;
        }
    }
}
