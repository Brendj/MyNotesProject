package ru.iteco.restservice.model.enums;

/**
 * Created by nuc on 04.05.2021.
 */
public enum Predefined {
    CLIENT_STUDENTS_CLASS_BEGIN(1000000000L,"Ученики"),
    CLIENT_EMPLOYEES (1100000000L,"Пед. состав"),
    CLIENT_EMPLOYEE (1100000001L,"Сотрудники"),
    CLIENT_ADMINISTRATION (1100000010L,"Администрация"),
    CLIENT_TECH_EMPLOYEES (1100000020L,"Тех. персонал"),
    CLIENT_PARENTS (1100000030L,"Родители"),
    CLIENT_VISITORS (1100000040L,"Посетители"),
    CLIENT_OTHERS ( 1100000050L,"Другое"),
    // все группы не-учащиеся и не-выбывшие должны быть до CLIENT_OTHERS
    CLIENT_LEAVING ( 1100000060L,"Выбывшие"),
    CLIENT_DELETED ( 1100000070L,"Удаленные"),
    CLIENT_DISPLACED ( 1100000080L,"Перемещенные"),
    CLIENT_OTHER_ORG (1100000090L, "Обучающиеся других ОО"),
    CLIENT_PARENT_OTHER_ORG (1100000100L, "Родители обучающихся других ОО"),
    CLIENT_EMPLOYEE_OTHER_ORG (1100000110L, "Сотрудники других ОО"),
    CLIENT_OUT_ORG (1100000120L, "Вне ОУ");

    private Long value;
    private String nameOfGroup;

    private Predefined(Long value, String nameOfGroup){
        this.value = value;
        this.nameOfGroup = nameOfGroup;
    }

    public static Predefined parse(Long value){
        Predefined currentPredefined = null;
        for (Predefined predefined: Predefined.values()){
            if(predefined.value.equals(value)){
                currentPredefined = predefined;
                break;
            }
        }
        return currentPredefined;
    }

    public static Predefined parse(String value){
        Predefined currentPredefined = null;
        for (Predefined predefined: Predefined.values()){
            if(predefined.nameOfGroup.equals(value)){
                currentPredefined = predefined;
                break;
            }
        }
        return currentPredefined;
    }

    public String getNameOfGroup() {
        return nameOfGroup;
    }

    public Long getValue() {
        return value;
    }
}
