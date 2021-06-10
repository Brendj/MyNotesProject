package ru.iteco.restservice.controller.menu;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.iteco.restservice.controller.menu.request.PreorderComplexRequest;
import ru.iteco.restservice.controller.menu.request.PreorderDishRequest;
import ru.iteco.restservice.controller.menu.request.ProhibitionRequest;
import ru.iteco.restservice.controller.menu.request.RegularPreorderRequest;
import ru.iteco.restservice.controller.menu.responsedto.*;
import ru.iteco.restservice.model.preorder.PreorderComplex;
import ru.iteco.restservice.model.preorder.PreorderMenuDetail;
import ru.iteco.restservice.model.preorder.RegularPreorder;
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

    @PostMapping("/preorder")
    @Operation(summary = "Создание предзаказа на комплекс",
            description = "Создание предзаказа на комплекс")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public PreorderComplexDTO createPreorderComplex(@RequestBody PreorderComplexRequest preorderComplexRequest) throws Exception {
        try {
            preorderService.checkCreateParameters(preorderComplexRequest);
            Date d = CalendarUtils.getDateFromLong(preorderComplexRequest.getDate());
            PreorderComplex pc = preorderService.createPreorder(preorderComplexRequest.getContractId(), d,
                    preorderComplexRequest.getGuardianMobile(), preorderComplexRequest.getComplexId(),
                    preorderComplexRequest.getAmount());
            return PreorderComplexDTO.build(pc);
        } catch (Exception e) {
            logger.error("Exception in createPreorderComplex: ", e);
            throw e;
        }
    }

    @PutMapping("/preorder/{id}")
    @Operation(summary = "Редактирование количества в предзаказе",
            description = "Редактирование количества в предзаказе")
    @ResponseBody
    public PreorderComplexDTO editPreorderComplex(@RequestBody PreorderComplexRequest preorderComplexRequest, @NotNull @PathVariable Long id) throws Exception {
        try {
            preorderComplexRequest.setPreorderId(id);
            preorderService.checkEditParameters(preorderComplexRequest);
            PreorderComplex pc = preorderService.editPreorder(preorderComplexRequest.getPreorderId(),
                    preorderComplexRequest.getContractId(),
                    preorderComplexRequest.getGuardianMobile(),
                    preorderComplexRequest.getAmount());
            return PreorderComplexDTO.build(pc);
        } catch (Exception e) {
            logger.error("Exception in editPreorderComplex: ", e);
            throw e;
        }
    }

    @DeleteMapping("/preorder/{id}")
    @Operation(summary = "Удаление предзаказа",
            description = "Удаление предзаказа")
    @ResponseBody
    public PreorderComplexDTO deletePreorderComplex(@RequestBody PreorderComplexRequest preorderComplexRequest, @NotNull @PathVariable Long id) throws Exception {
        try {
            preorderComplexRequest.setPreorderId(id);
            preorderService.checkDeleteParameters(preorderComplexRequest);
            PreorderComplex pc = preorderService.deletePreorder(preorderComplexRequest.getPreorderId(),
                    preorderComplexRequest.getContractId(),
                    preorderComplexRequest.getGuardianMobile());
            return PreorderComplexDTO.buildDeleted(pc);
        } catch (Exception e) {
            logger.error("Exception in deletePreorderComplex: ", e);
            throw e;
        }
    }

    @PostMapping("/preorderDish")
    @Operation(summary = "Создание предзаказа на блюдо",
            description = "Создание предзаказа на блюдо")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public PreorderMenuDetailDTO createPreorderMenuDetail(@RequestBody PreorderDishRequest preorderDishRequest) throws Exception {
        try {
            preorderService.checkDishCreateParameters(preorderDishRequest);
            Date d = CalendarUtils.getDateFromLong(preorderDishRequest.getDate());
            PreorderMenuDetail pmd = preorderService.createPreorderMenuDetail(preorderDishRequest.getContractId(), d,
                    preorderDishRequest.getGuardianMobile(), preorderDishRequest.getComplexId(),
                    preorderDishRequest.getDishId(), preorderDishRequest.getAmount());
            return PreorderMenuDetailDTO.build(pmd);
        } catch (Exception e) {
            logger.error("Exception in createPreorderMenuDetail: ", e);
            throw e;
        }
    }

    @PutMapping("/preorderDish/{id}")
    @Operation(summary = "Редактирование количества блюд в предзаказе",
            description = "Редактирование количества блюд в предзаказе")
    @ResponseBody
    public PreorderMenuDetailDTO editPreorderMenuDetail(@RequestBody PreorderDishRequest preorderDishRequest, @NotNull @PathVariable Long id) throws Exception {
        try {
            preorderDishRequest.setPreorderDishId(id);
            preorderService.checkDishEditParameters(preorderDishRequest);
            PreorderMenuDetail pmd = preorderService.editPreorderMenuDetail(preorderDishRequest.getPreorderDishId(),
                    preorderDishRequest.getContractId(),
                    preorderDishRequest.getGuardianMobile(),
                    preorderDishRequest.getAmount());
            return PreorderMenuDetailDTO.build(pmd);
        } catch (Exception e) {
            logger.error("Exception in editPreorderMenuDetail: ", e);
            throw e;
        }
    }

    @DeleteMapping("/preorderDish/{id}")
    @Operation(summary = "Удаление предзаказа на блюдо",
            description = "Удаление предзаказа на блюдо")
    @ResponseBody
    public PreorderMenuDetailDTO deletePreorderMenuDetail(@RequestBody PreorderDishRequest preorderDishRequest, @NotNull @PathVariable Long id) throws Exception {
        try {
            preorderDishRequest.setPreorderDishId(id);
            preorderService.checkDishDeleteParameters(preorderDishRequest);
            PreorderMenuDetail pmd = preorderService.deletePreorderMenuDetail(preorderDishRequest.getPreorderDishId(),
                    preorderDishRequest.getContractId(),
                    preorderDishRequest.getGuardianMobile());
            return PreorderMenuDetailDTO.buildDeleted(pmd);
        } catch (Exception e) {
            logger.error("Exception in deletePreorderMenuDetail: ", e);
            throw e;
        }
    }

    @PostMapping("/preorderRegular")
    @Operation(summary = "Создание регулярного предзаказа",
            description = "Создание регулярного предзаказа")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public RegularPreorderDTO createRegularPreorder(@RequestBody RegularPreorderRequest regularPreorderRequest) throws Exception {
        try {
            preorderService.checkRegularPreorderCreateParameters(regularPreorderRequest);
            RegularPreorder rp = preorderService.createRegularPreorder(regularPreorderRequest);
            return RegularPreorderDTO.build(rp);
        } catch (Exception e) {
            logger.error("Exception in createRegularPreorder: ", e);
            throw e;
        }
    }

    @PutMapping("/preorderRegular/{id}")
    @Operation(summary = "Редактирование регулярного предзаказа",
            description = "Редактирование регулярного предзаказа")
    @ResponseBody
    public RegularPreorderDTO editRegularPreorder(@RequestBody RegularPreorderRequest regularPreorderRequest, @NotNull @PathVariable Long id) throws Exception {
        try {
            regularPreorderRequest.setRegularPreorderId(id);
            preorderService.checkRegularPreorderEditParameters(regularPreorderRequest);
            RegularPreorder rp = preorderService.editRegularPreorder(regularPreorderRequest);
            return RegularPreorderDTO.build(rp);
        } catch (Exception e) {
            logger.error("Exception in editRegularPreorder: ", e);
            throw e;
        }
    }

    @DeleteMapping("/preorderRegular/{id}")
    @Operation(summary = "Удаление регулярного предзаказа",
            description = "Удаление регулярного предзаказа")
    @ResponseBody
    public RegularPreorderDTO deleteRegularPreorder(@RequestBody RegularPreorderRequest regularPreorderRequest, @NotNull @PathVariable Long id) throws Exception {
        try {
            regularPreorderRequest.setRegularPreorderId(id);
            preorderService.checkRegularPreorderDeleteParameters(regularPreorderRequest);
            preorderService.deleteRegularPreorder(regularPreorderRequest.getRegularPreorderId(),
                    regularPreorderRequest.getContractId());
            return null;
        } catch (Exception e) {
            logger.error("Exception in deleteRegularPreorder: ", e);
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
