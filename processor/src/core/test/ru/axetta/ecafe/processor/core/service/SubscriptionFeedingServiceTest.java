package ru.axetta.ecafe.processor.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 19.05.14
 * Time: 12:18
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/datatest-context.xml"})
public class SubscriptionFeedingServiceTest {

    @Autowired
    SubscriptionFeedingService service;

    @Test
    public void testFindActiveCycleDiagram() throws Exception {
        System.out.println("Test");
    }
}
