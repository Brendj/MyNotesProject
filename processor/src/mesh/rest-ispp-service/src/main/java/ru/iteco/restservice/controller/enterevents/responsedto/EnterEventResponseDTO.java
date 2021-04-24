/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.enterevents.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EnterEventResponseDTO", description = "Данные по событию прохода")
public class EnterEventResponseDTO {

    @Schema(description = "Время события в Timestamp (ms)", example = "1617091352000")
    private Long dateTime;

    @Schema(description = "Код события", example = "100")
    private Integer direction;

    @Schema(description = "Расшифровка кода события", example = "Обнаружен на подносе карты внутри здания")
    private String directionText;

    @Schema(description = "Адрес здания, где произошло событие", example = "7-я ул. Текстильщиков / Дом- 9, Стр- 1")
    private String address;

    @Schema(description = "Кароткое название организации, где произошло событие", example = "ГБОУ СОШ № 654-3(458)")
    private String shortNameInfoService;

    @Schema(description = "ФИО работника организации, отметивший проход клиента",
            example = "Евгений Иванович Иванов", nullable = true)
    private String childPassChecker;

    @Schema(description = "Флаг того, что отметка сделана работником (0 – классный руководитель; 1 – охранник)",
            example = "1", nullable = true)
    private Integer childPassCheckerMethod;

    @Schema(description = "ФИО представителя обучающегося приведшего обучающегося в здание",
            example = "Елена Владимировна Смирнова", nullable = true)
    private String repName;

    public EnterEventResponseDTO(Long dateTime, Integer direction, String directionText, String address,
            String shortNameInfoService, String childPassChecker, Integer childPassCheckerMethod, String repName) {
        this.dateTime = dateTime;
        this.direction = direction;
        this.directionText = directionText;
        this.address = address;
        this.shortNameInfoService = shortNameInfoService;
        this.childPassChecker = childPassChecker;
        this.childPassCheckerMethod = childPassCheckerMethod;
        this.repName = repName;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public String getDirectionText() {
        return directionText;
    }

    public void setDirectionText(String directionText) {
        this.directionText = directionText;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    public String getChildPassChecker() {
        return childPassChecker;
    }

    public void setChildPassChecker(String childPassChecker) {
        this.childPassChecker = childPassChecker;
    }

    public Integer getChildPassCheckerMethod() {
        return childPassCheckerMethod;
    }

    public void setChildPassCheckerMethod(Integer childPassCheckerMethod) {
        this.childPassCheckerMethod = childPassCheckerMethod;
    }

    public String getRepName() {
        return repName;
    }

    public void setRepName(String repName) {
        this.repName = repName;
    }
}
