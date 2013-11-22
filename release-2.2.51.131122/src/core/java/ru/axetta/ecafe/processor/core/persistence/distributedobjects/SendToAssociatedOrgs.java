/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects;

public enum SendToAssociatedOrgs {

    DontSend(0), //Для поддержки того как работает сейчас
    Send(1), //Для поддержки того как работает сейчас

    SendToNobody(2), //Не отправлять никому
    SendToSelf(3), //Отправлять только тому кто создал этот объект
    SendToMain(4), //Отправлять только тому кто создал этот объект и поставщику
    SendToAll(5); //Отправлять всем (тому кто создал, поставщику, организациям у которорых поставщик )

    private int sendMode;

    public int getSendMode() {
        return sendMode;
    }

    SendToAssociatedOrgs(int sendMode) {
        this.sendMode = sendMode;
    }

}
