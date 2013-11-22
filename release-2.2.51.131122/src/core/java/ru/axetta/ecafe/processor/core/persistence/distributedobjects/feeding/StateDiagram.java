package ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 02.09.13
 * Time: 15:10
 * To change this template use File | Settings | File Templates.
 */
public enum StateDiagram {
    /*
    * Состояние циклограммы
    * 0 – активная
    * 1 – ожидается активация
    * 2 – заблокировать
    * */
    ACTIVE, WAIT, BLOCK

}
