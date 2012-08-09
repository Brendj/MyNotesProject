/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.contract;

import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEntityGroupPage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEntityItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 06.08.12
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ContractGroupPage extends AbstractEntityGroupPage<ContractItem> {

}
