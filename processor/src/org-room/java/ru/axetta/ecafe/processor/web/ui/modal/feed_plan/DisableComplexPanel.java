/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.modal.feed_plan;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 02.09.13
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class DisableComplexPanel extends BasicWorkspacePage {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(ClientFeedActionPanel.class);
    private List<DisableComplexListener> listeners;
    private List<Complex> complexes;





    /**
     * ****************************************************************************************************************
     * Загрузка данных из БД
     * ****************************************************************************************************************
     */
    public void fill () {
    }

    public void setComplexes (Map<Integer, Boolean> disabledComplexes) {
        if (complexes == null) {
            complexes = new ArrayList<Complex>();
        }
        complexes.clear();

        for (Integer complex : disabledComplexes.keySet()) {
            Complex c = new Complex(complex, disabledComplexes.get(complex));
            complexes.add(c);
        }
    }




    /**
     * ****************************************************************************************************************
     * Работа с UI
     * ****************************************************************************************************************
     */
    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(ClientFeedActionPanel.class).fill();
    }

    public List<Complex> getComplexes() {
        return complexes;
    }

    public void doApply () {
        DisableComplexEvent event = new DisableComplexEvent();
        for (Complex c : complexes) {
            event.addComplex(c.getComplex(), c.getDisabled());
        }
        
        for (DisableComplexListener listener : listeners) {
            listener.onDisableComplexEvent(event);
        }
    }

    public void doCancel () {

    }






    /**
     * ****************************************************************************************************************
     * Вспомогательные методы
     * ****************************************************************************************************************
     */
    public void addCallbackListener (BasicWorkspacePage page) {
        if (!(page instanceof DisableComplexListener)) {
            logger.error("Trying to add not listener for DisableComplexListener");
            return;
        }
        if (listeners == null) {
            listeners = new ArrayList<DisableComplexListener>();
        }
        listeners.add((DisableComplexListener) page);
    }






    public static class Complex {
        private int complex;
        private boolean disabled;

        public Complex(int complex, boolean disabled) {
            this.complex = complex;
            this.disabled = disabled;
        }

        public int getComplex() {
            return complex;
        }

        public void setComplex(int complex) {
            this.complex = complex;
        }

        public boolean getDisabled() {
            return disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }

        public String getIcon () {
            return disabled ? "visible_off" : "visible_on";
        }

        public void doChangeDisabled () {
            disabled = !disabled;
        }
    }
}
