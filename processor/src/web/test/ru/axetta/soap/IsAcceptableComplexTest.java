/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.soap;

import junit.framework.TestCase;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodAgeGroupType;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodType;
import ru.axetta.ecafe.processor.web.partner.preorder.PreorderDAOService;
import ru.axetta.ecafe.processor.web.partner.preorder.PreorderGoodParamsContainer;
import ru.axetta.ecafe.processor.web.partner.preorder.dataflow.PreorderComplexItemExt;

import java.util.LinkedList;
import java.util.List;

public class IsAcceptableComplexTest extends TestCase {

    private ClientGroup testClientGroup;
    private List<PreorderComplexItemExt> testListOfPreordes;
    private List<PreorderGoodParamsContainer> testPreorderParamsContainer;
    private final Boolean HAS_DISCOUNT = true;

    private final String CONTROL_TEST_GROUP_NAME = "7-Ф";

    private final String ANSI_RED = "\u001B[31m";
    private final String ANSI_GREEN = "\u001B[32m";
    private final String ANSI_RESET = "\u001B[0m";

    @Override
    public void setUp(){
        this.initClient();
        this.initTestLists();
    }
    /*
    Проблемные комплексы с московского тестового стенда
686644498;"7-Ф";0;0;1;"Обед 1 (5-11)"
686644497;"7-Ф";0;0;1;"Завтрак 1 (5-11)"
686644496;"7-Ф";2;4;0;"Обед (Перечень блюд на выбор)"
686644495;"7-Ф";1;4;0;"Обед 3"
686644494;"7-Ф";2;4;0;"Обед 2"
686644493;"7-Ф";0;0;1;"Полдник 1 (1-4)"
686644492;"7-Ф";0;0;1;"Обед 1 (1-4)"
686644491;"7-Ф";0;0;1;"Завтрак 1 (1-4)"
    */


    private void initTestLists() {
        this.testListOfPreordes = new LinkedList<PreorderComplexItemExt>();

        PreorderComplexItemExt e1 = new PreorderComplexItemExt();
        e1.setDiscount(true);
        e1.setComplexName("Обед 1 (5-11)");
        this.testListOfPreordes.add(e1);

        PreorderComplexItemExt e2 = new PreorderComplexItemExt();
        e2.setDiscount(true);
        e2.setComplexName("Завтрак 1 (5-11)");
        this.testListOfPreordes.add(e2);

        PreorderComplexItemExt e3 = new PreorderComplexItemExt();
        e3.setDiscount(false);
        e3.setComplexName("Обед (Перечень блюд на выбор)");
        this.testListOfPreordes.add(e3);

        PreorderComplexItemExt e4 = new PreorderComplexItemExt();
        e4.setDiscount(false);
        e4.setComplexName("Обед 3");
        this.testListOfPreordes.add(e4);

        PreorderComplexItemExt e5 = new PreorderComplexItemExt();
        e5.setDiscount(false);
        e5.setComplexName("Обед 2");
        this.testListOfPreordes.add(e5);

        PreorderComplexItemExt e6 = new PreorderComplexItemExt();
        e6.setDiscount(true);
        e6.setComplexName("Полдник 1 (1-4)");
        this.testListOfPreordes.add(e6);

        PreorderComplexItemExt e7 = new PreorderComplexItemExt();
        e7.setDiscount(true);
        e7.setComplexName("Обед 1 (1-4)");
        this.testListOfPreordes.add(e7);

        PreorderComplexItemExt e8 = new PreorderComplexItemExt();
        e8.setDiscount(true);
        e8.setComplexName("Завтрак 1 (1-4)");
        this.testListOfPreordes.add(e8);

        this.testPreorderParamsContainer = new LinkedList<PreorderGoodParamsContainer>();

        PreorderGoodParamsContainer c1 = new PreorderGoodParamsContainer(0, 0);
        testPreorderParamsContainer.add(c1);

        PreorderGoodParamsContainer c2 = new PreorderGoodParamsContainer(0, 0);
        testPreorderParamsContainer.add(c2);

        PreorderGoodParamsContainer c3 = new PreorderGoodParamsContainer(2, 4);
        testPreorderParamsContainer.add(c3);

        PreorderGoodParamsContainer c4 = new PreorderGoodParamsContainer(1, 4);
        testPreorderParamsContainer.add(c4);

        PreorderGoodParamsContainer c5 = new PreorderGoodParamsContainer(2, 4);
        testPreorderParamsContainer.add(c5);

        PreorderGoodParamsContainer c6 = new PreorderGoodParamsContainer(0, 0);
        testPreorderParamsContainer.add(c6);

        PreorderGoodParamsContainer c7 = new PreorderGoodParamsContainer(0, 0);
        testPreorderParamsContainer.add(c7);

        PreorderGoodParamsContainer c8 = new PreorderGoodParamsContainer(0, 0);
        testPreorderParamsContainer.add(c8);
    }

    private void initClient() {
        this.testClientGroup = new ClientGroup(null, CONTROL_TEST_GROUP_NAME);
    }

    public void testAcceptableComplex(){
        PreorderDAOService service = new PreorderDAOService();
        for(int i = 0; i < this.testPreorderParamsContainer.size(); i++){
            PreorderComplexItemExt element = this.testListOfPreordes.get(i);
            PreorderGoodParamsContainer container = this.testPreorderParamsContainer.get(i);
            Boolean result = service.isAcceptableComplex(element, this.testClientGroup, this.HAS_DISCOUNT, container);

            String outputResult = result ? this.ANSI_GREEN + result.toString() + this.ANSI_RESET : this.ANSI_RED + result.toString() + this.ANSI_RESET;

            System.out.println("Method return: " + outputResult + " Client group: " + this.testClientGroup.getGroupName() + " and client HAS_DISCOUNT: " + this.HAS_DISCOUNT
                    + "\nComplexName: " + element.getComplexName() + " AgeGroup: " + GoodAgeGroupType.fromInteger(container.getAgeGroup())
                    + " GoodType: " + GoodType.fromInteger(container.getGoodType()) + "\n");
        }
    }



}
