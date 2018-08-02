/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.spb.scud;


import generated.spb.SCUD.EventList;
import generated.spb.SCUD.ObjectFactory;
import generated.spb.SCUD.PushResponse;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.service.scud.EventDataItem;
import ru.axetta.ecafe.processor.core.service.scud.ScudManager;
import ru.axetta.ecafe.processor.core.service.scud.ScudService;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScudManagerTest {
    private RuntimeContext context;
    private ApplicationContext applicationContext;
    private Properties properties;
    private ScudService service;
    private Session session;
    private List<Object[]> testDataFormDB;
    private SQLQuery query;
    private ObjectFactory scudObjectFactory = new ObjectFactory();

    @Before
    public void init() throws Exception {
        initTestDataList();

        context = mock(RuntimeContext.class);
        applicationContext = mock(ApplicationContext.class);
        properties = mock(Properties.class);
        service = mock(ScudService.class);
        session = mock(Session.class);
        query = mock(SQLQuery.class);

        context.setApplicationContext(applicationContext);
        when(context.getConfigProperties()).thenReturn(properties);
        when(context.createPersistenceSession()).thenReturn(session);

        when(session.createSQLQuery(anyString())).thenReturn(query);

        when(query.list()).thenReturn(testDataFormDB);

        when(applicationContext.getBean(RuntimeContext.class)).thenReturn(context);
        when(applicationContext.getBean(ScudService.class)).thenReturn(service);

        when(properties.getProperty("ecafe.processor.scudmanager.sendtoexternal", "false")).thenReturn("false");
        when(properties.getProperty("ecafe.processor.scudmanager.node", "1")).thenReturn("1");

        setMock(applicationContext);
    }

    private void initTestDataList() {
        testDataFormDB = new LinkedList<Object[]>();
        Object[] row1 = {
                "", null, "guid1",
                BigInteger.valueOf(1251572280L), 100, 1533033621000L, BigInteger.valueOf(3507961L), BigInteger.valueOf(1024L)
        };
        testDataFormDB.add(row1);
        Object[] row2 = {
                "", 5991L, "guid2",
                BigInteger.valueOf(152995193L), 1, 1456761312000L, BigInteger.valueOf(3906541L), BigInteger.valueOf(1178L)
        };
        testDataFormDB.add(row2);
        Object[] row3 = {
                "301", null, "eb72ee7f-bd14-2047-e043-a2997e0a66f5",
                BigInteger.valueOf(2480781752L), 100, 1531752066000L, BigInteger.valueOf(818003L), BigInteger.valueOf(301L)
        };
        testDataFormDB.add(row3);
        Object[] row4 = {
                "301", null, "eb3677b9-c7d3-3ed3-e043-a2997e0af9b1",
                BigInteger.valueOf(3870537400L), 100, 1531393717000L, BigInteger.valueOf(817958L), BigInteger.valueOf(301L)
        };
        testDataFormDB.add(row4);
        Object[] row5 = {
                "", 5506L, "e5ee95e0-39a1-5731-e043-a2997e0ad784",
                BigInteger.valueOf(2264235604L), 1, 1456767025000L, BigInteger.valueOf(267805L), BigInteger.valueOf(2424L)
        };
        testDataFormDB.add(row5);

    }

    private void setMock(ApplicationContext applicationContextMock) {
        try {
            Field applicationContext = RuntimeContext.class.getDeclaredField("applicationContext");
            applicationContext.setAccessible(true);
            applicationContext.set(applicationContext, applicationContextMock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void resetSingleton() throws Exception {
        Field applicationContext = RuntimeContext.class.getDeclaredField("applicationContext");
        applicationContext.setAccessible(true);
        applicationContext.set(null, null);
    }

    @Test
    public void testScudManagerIsNotNull(){
        ScudManager manager = new ScudManager();
        assertFalse("SCUD Manager is null", manager == null);
    }

    @Test
    public void testScudServiceIsNotNull(){
        ScudService service = new ScudService();
        assertFalse("SCUD Service is null", service == null);
    }

    @Test
    public void testScudServiceProcessDataForSending() throws Exception{
        when(service.sendEvent(anyList())).thenAnswer(new Answer<PushResponse>() {
            @Override
            public PushResponse answer(InvocationOnMock invocation) throws Exception{
                Object[] args = invocation.getArguments();
                List<EventDataItem> list = (List<EventDataItem>) args[0];
                JAXBContext jc = JAXBContext.newInstance(EventList.class);
                EventList eventList = scudObjectFactory.createEventList(list);
                Marshaller marshaller = jc.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                marshaller.marshal(eventList, System.out);

                PushResponse response = new PushResponse();
                response.setQueueId("Any String");
                response.setResult(true);
                return response;
            }
        });

        ScudManager manager = new ScudManager();
        manager.sendToExternal(10);
    }


}
