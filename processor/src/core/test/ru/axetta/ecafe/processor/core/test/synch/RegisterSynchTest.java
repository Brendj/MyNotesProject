package ru.axetta.ecafe.processor.core.test.synch;/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

import junit.framework.TestCase;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 11.07.13
 * Time: 15:01
 * To change this template use File | Settings | File Templates.
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/context.xml" })
public class RegisterSynchTest extends TestCase {
    private static RegisterSynchSingletonBeanTest beanTest = null;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RegisterSynchTest.class);


    /*@Test
    public void testInsert() throws Exception {
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("---               Тестирование вставки клиета                       ---");
        System.out.println("-----------------------------------------------------------------------");
        try {
            Org org = DAOService.getInstance().getOrg(0L);
            Org org2 = DAOService.getInstance().getOrg(3L);
            printOrgClients(org);
            printOrgClients(org2);

            List<ImportRegisterMSKClientsService.ExpandedPupilInfo> pupils  = new ArrayList<ImportRegisterMSKClientsService.ExpandedPupilInfo>();
            List<ImportRegisterMSKClientsService.ExpandedPupilInfo> pupils2 = new ArrayList<ImportRegisterMSKClientsService.ExpandedPupilInfo>();
            ImportRegisterMSKClientsService.ExpandedPupilInfo pupil;
            pupil = new ImportRegisterMSKClientsService.ExpandedPupilInfo(); //  Ничего не меняется
            pupil.firstName = "Петр";
            pupil.secondName = "Петрович";
            pupil.familyName = "Петров";
            pupil.guid = "1";
            pupil.group = "6А";
            pupil.setGuidOfOrg("000");
            pupils.add(pupil);
            pupil = new ImportRegisterMSKClientsService.ExpandedPupilInfo(); //  Перевод в другой класс
            pupil.firstName = "Иван";
            pupil.secondName = "Иванович";
            pupil.familyName = "Иванов";
            pupil.guid = "12";
            pupil.group = "11А";
            pupil.setGuidOfOrg("000");
            pupils.add(pupil);
            pupil = new ImportRegisterMSKClientsService.ExpandedPupilInfo(); //  Добавление
            pupil.firstName = "Сергей";
            pupil.secondName = "Сергеевич";
            pupil.familyName = "Сергеев";
            pupil.guid = "1234";
            pupil.group = "10А";
            pupil.setGuidOfOrg("000");
            pupils.add(pupil);
            pupil = new ImportRegisterMSKClientsService.ExpandedPupilInfo(); //  Перевод в другую школу
            pupil.firstName = "Павел";
            pupil.secondName = "Павлович";
            pupil.familyName = "Павлов";
            pupil.guid = "123";
            pupil.group = "10А";
            pupil.setGuidOfOrg("111");
            pupils2.add(pupil); // массив второй школы

            System.out.println("---               Синхронизация с массивом данных                   ---");
            RuntimeContext.getAppContext().getBean("importRegisterMSKClientsService", ImportRegisterMSKClientsService.class).
                                    parseClients("test", "test", org, pupils, true, null, false);
            RuntimeContext.getAppContext().getBean("importRegisterMSKClientsService", ImportRegisterMSKClientsService.class).
                    parseClients("test", "test", org2, pupils2, true, null, false);
            System.out.println("---               Синхронизация окончена                            ---");

            printOrgClients(org);
            printOrgClients(org2);
            System.out.println("-----------------------------------------------------------------------");
            System.out.println("---               Тестирование завершено                            ---");
            System.out.println("-----------------------------------------------------------------------");

            doAssert ();
        } catch (Exception e) {
            logger.error("Не удалось протестировать синхронизацию с Реестрами", e);
        }
    }*/


    public void printOrgClients (Org org) {
        List <Client> clients = DAOService.getInstance().getClientsByOrgId(org.getIdOfOrg());
        for (Client cl : clients) {
            String str = cl.getPerson().getFirstName() + " " +
                         cl.getPerson().getSecondName () + " " +
                         cl.getPerson().getSurname();
            if (cl.getClientGroup() != null) {
                str += ", " + cl.getClientGroup().getGroupName();
            }
            str += ", " + org.getOfficialName();
            System.out.println(str);
        }
    }


    public void doAssert () {
        //  Тестируем первую школу
        List <Client> clients = DAOService.getInstance().getClientsByOrgId(0L);
        assertNotNull(clients);
        Client newClient = null;
        for (Client cl : clients) {
            //  Проверяем Петр Петрович Петров
            if (cl.getPerson().getSurname().equals("Петров")) {
                assertEquals(cl.getPerson().getFirstName(), "Петр");
                assertEquals(cl.getPerson().getSecondName(), "Петрович");
                assertEquals(cl.getPerson().getSurname(), "Петров");
                assertEquals(cl.getClientGroup().getGroupName(), "6А");
                assertEquals(cl.getOrg().getOfficialName(), "ГБОУ СОШ №327");
            }
            //  Проверяем Иван Иванович Иванов
            if (cl.getPerson().getSurname().equals("Иванов")) {
                assertEquals(cl.getPerson().getFirstName(), "Иван");
                assertEquals(cl.getPerson().getSecondName(), "Иванович");
                assertEquals(cl.getPerson().getSurname(), "Иванов");
                assertEquals(cl.getClientGroup().getGroupName(), "11А");
                assertEquals(cl.getOrg().getOfficialName(), "ГБОУ СОШ №327");
            }
            //  Проверяем Сергей Сергеевич Сергеев
            if (cl.getPerson().getSurname().equals("Сергеев")) {
                newClient = cl;
                assertEquals(cl.getPerson().getFirstName(), "Сергей");
                assertEquals(cl.getPerson().getSecondName(), "Сергеевич");
                assertEquals(cl.getPerson().getSurname(), "Сергеев");
                assertEquals(cl.getClientGroup().getGroupName(), "10А");
                assertEquals(cl.getOrg().getOfficialName(), "ГБОУ СОШ №327");
            }
        }
        assertNotNull(newClient);


        //  Тестируем вторую школу
        clients = DAOService.getInstance().getClientsByOrgId(3L);
        assertNotNull(clients);
        Client changedClient = null;
        for (Client cl : clients) {
            //  Павел Павлович Павлов
            if (cl.getPerson().getSurname().equals("Павлов")) {
                changedClient = cl;
                assertEquals(cl.getPerson().getFirstName(), "Павел");
                assertEquals(cl.getPerson().getSecondName(), "Павлович");
                assertEquals(cl.getPerson().getSurname(), "Павлов");
                assertEquals(cl.getClientGroup().getGroupName(), "10А");
                assertEquals(cl.getOrg().getOfficialName(), "ГБОУ СОШ №355");
            }
            //  Удалить Удалить Удалить
            if (cl.getPerson().getSurname().equals("Удалить")) {
                assertEquals(cl.getPerson().getFirstName(), "Удалить");
                assertEquals(cl.getPerson().getSecondName(), "Удалить");
                assertEquals(cl.getPerson().getSurname(), "Удалить");
                assertEquals(cl.getClientGroup().getGroupName(), "Выбывшие");
                assertEquals(cl.getOrg().getOfficialName(), "ГБОУ СОШ №355");
            }
        }
    assertNotNull(changedClient);
    }
}
