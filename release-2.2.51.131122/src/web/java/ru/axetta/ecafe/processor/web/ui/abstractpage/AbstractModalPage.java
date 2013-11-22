/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.abstractpage;

import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

public class AbstractModalPage extends BasicPage {

    public void show() {
        MainPage.getSessionInstance().registerModalPageShow(this);
    }
    public void hide() {
        MainPage.getSessionInstance().registerModalPageHide(this);
    }
}
