package ru.iteco.restservice.controller.menu;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.iteco.restservice.controller.menu.request.PreorderComplexRequest;
import ru.iteco.restservice.controller.menu.request.ProhibitionRequest;
import ru.iteco.restservice.controller.menu.responsedto.CategoryItem;
import ru.iteco.restservice.controller.menu.responsedto.ComplexesResponse;
import ru.iteco.restservice.controller.menu.responsedto.MenuListResponse;
import ru.iteco.restservice.servise.CalendarUtils;
import ru.iteco.restservice.servise.ComplexService;
import ru.iteco.restservice.servise.MenuService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.iteco.restservice.servise.PreorderService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;
import java.util.List;

@Validated
@RestController
@RequestMapping("/menu")
@Tag(name = "Menu-controller", description = "Школьное меню")
public class MenuController {
    private final Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    MenuService menuService;

    @Autowired
    ComplexService complexService;

    @Autowired
    PreorderService preorderService;

    @GetMapping("/getMenuList")
    @ResponseBody
    @Operation(
            summary = "Получение меню буфета школьной столовой",
            description = "Получение меню буфета школьной столовой по номеру лицевого счета клиента")
    public MenuListResponse getMenuList(@Parameter(description = "Номер лицевого счета клиента", example = "13177")
                                        @RequestParam @PositiveOrZero Long contractId,
                                        @Parameter(description = "Дата запроса меню ", example = "1620714787123")
                                        @RequestParam Long date) {
        try {
            MenuListResponse response = new MenuListResponse();
            Date d = CalendarUtils.getDateFromLong(date);
            List<CategoryItem> items = menuService.getMenuList(d, contractId);
            response.getCategoryItems().addAll(items);
            return response;
        } catch (Exception e){
            logger.error("Exception in getMenuList: ", e);
            throw e;
        }
    }

    @GetMapping("/getComplexes")
    @ResponseBody
    @Operation(summary = "Получение меню комплексного питания",
    description = "Получение меню питания за счет средств бюджета города Москвы и меню платного горячего питания")
    public ComplexesResponse getComplexes(@Parameter(description = "Номер лицевого счета клиента", example = "13177")
                                          @RequestParam @PositiveOrZero Long contractId,
                                          @Parameter(description = "Дата запроса меню в Timestamp (ms)", example = "1620714787123")
                                          @RequestParam @PositiveOrZero Long date) throws Exception {
        try {
            Date d = CalendarUtils.getDateFromLong(date);
            ComplexesResponse response = complexService.getComplexes(d, contractId);
            return response;
        } catch (Exception e){
            logger.error("Exception in getComplexes: ", e);
            throw e;
        }
    }

    @PostMapping
    @Operation(summary = "Создание предзаказа",
            description = "Создание предзаказа")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Long createPreorderComplex(@RequestBody PreorderComplexRequest preorderComplexRequest) throws Exception {
        try {
            Date d = CalendarUtils.getDateFromLong(preorderComplexRequest.getDate());
            return preorderService.createPreorder(preorderComplexRequest.getContractId(), d,
                    preorderComplexRequest.getGuardianMobile(), preorderComplexRequest.getComplexId(),
                    preorderComplexRequest.getAmount());
        } catch (Exception e) {
            logger.error("Exception in createPreorderComplex: ", e);
            throw e;
        }
    }

    @PostMapping("/prohibition")
    @Operation(summary = "Создание ограничения",
    description = "Создание ограничения на блюда, подкатегорию или на целую категорию для клиента, указанного по л/с."
            + " В запросе должен участвовать один из трёх идентификаторов."
            + " Передача 2 и более идентификаторов за раз запрещено")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public Long createProhibition(@RequestBody ProhibitionRequest prohibitionRequest){
        try {
            return menuService.createProhibitionData(
                    prohibitionRequest.getContractId(),
                    prohibitionRequest.getIdOfDish(),
                    prohibitionRequest.getIdOfCategory(),
                    prohibitionRequest.getIdOfCategoryItem()
            );
        } catch (Exception e){
            logger.error("Exception in createProhibition ", e);
            throw e;
        }
    }

    @DeleteMapping("/prohibition/{id}")
    @Operation(summary = "Удаление ограничения",
            description = "Удаляет ограничение по указаному ID. В методе присуствует каскадное удаление ограничений."
                    + "Т.е. удаление ограничения на категорию вызовет удаление ограничений на подкатегории и блюда,"
                    + " относящихся к этой категории. Результат операции: ID удаленных записей ")
    @ResponseBody
    public List<Long> deleteProhibition(@NotNull @PathVariable Long id){
        try{
            return menuService.deleteProhibitionById(id);
        } catch (Exception e){
            logger.error("Exception in deleteProhibition ", e);
            throw e;
        }
    }
}
