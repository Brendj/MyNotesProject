package ru.axetta.ecafe.processor.core.sync;

import ru.axetta.ecafe.processor.core.logic.GoodDateForOrders;
import org.hibernate.Session;


/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.13
 * Time: 19:22
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractProcessor<RES extends AbstractToElement> extends GoodDateForOrders{

    protected final Session session;

    protected AbstractProcessor(Session session) {
        this.session = session;
    }

    public abstract <RES> RES  process() throws Exception;
}
