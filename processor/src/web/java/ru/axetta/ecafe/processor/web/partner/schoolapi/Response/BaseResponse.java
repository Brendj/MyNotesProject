/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response;

import java.io.Serializable;

public abstract class BaseResponse implements Serializable
{
    protected int result;
    protected String errorText;

    public int getResult() { return result; }
    public void setResult(int value) { result = value; }

    public String getErrorText() { return errorText; }
    public void setErrorText(String value) { errorText = value; }
}
