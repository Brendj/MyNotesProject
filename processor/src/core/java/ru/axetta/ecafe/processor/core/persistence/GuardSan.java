/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 03.07.13
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */
public class GuardSan {
    private long idOfGuardSan;
    private Client client;
    private String guardSan;

    public GuardSan() {
    }

    public GuardSan(Client client, String guardSan) {
        this.client = client;
        this.guardSan = guardSan;
    }

    public long getIdOfGuardSan() {
        return idOfGuardSan;
    }

    public void setIdOfGuardSan(long idOfGuardSan) {
        this.idOfGuardSan = idOfGuardSan;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client= client;
    }

    public String getGuardSan() {
        return guardSan;
    }

    public void setGuardSan(String guardSan) {
        this.guardSan = guardSan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GuardSan guardSan1 = (GuardSan) o;

        if (idOfGuardSan != guardSan1.idOfGuardSan) {
            return false;
        }
        if (client != null ? !client.equals(guardSan1.client) : guardSan1.client != null) {
            return false;
        }
        if (guardSan != null ? !guardSan.equals(guardSan1.guardSan) : guardSan1.guardSan != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (idOfGuardSan ^ (idOfGuardSan >>> 32));
        result = 31 * result + (client != null ? client.hashCode() : 0);
        result = 31 * result + (guardSan != null ? guardSan.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GuardSan{" +
                "idOfGuardSan=" + idOfGuardSan +
                ", client=" + client +
                ", guardSan='" + guardSan + '\'' +
                '}';
    }
}
