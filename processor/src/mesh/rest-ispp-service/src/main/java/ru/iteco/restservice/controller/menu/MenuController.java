package ru.iteco.restservice.controller.menu;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.iteco.restservice.controller.menu.responsedto.MenuItem;
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

    //public MenuController() {
    //    menuService
    //}

    @GetMapping("/getMenuList")
    @ResponseBody
    public MenuListResponse getMenuList(@RequestParam @PositiveOrZero Long contractId,
                                        @RequestParam Date date) {
        try {
            MenuListResponse response = new MenuListResponse();
            List<MenuItem> items = menuService.getMenuList(date, contractId);
            response.getMenuItems().addAll(items);
            return response;
        } catch (Exception e){
            logger.error("Exception in getMenuList: ", e);
            throw e;
        }
    }
}
