/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 08.08.16
 * Time: 10:37
 */

public class ClilentPhotoChangeItem {
    private long clientId;
    private byte[] imageBytes;
    private int src;
    private String lastProceedError;
    private String guardianName;

    public ClilentPhotoChangeItem() {
    }

    public ClilentPhotoChangeItem(long clientId, byte[] imageBytes, int src, String lastProceedError,
            String guardianName) {
        this.clientId = clientId;
        this.imageBytes = imageBytes;
        this.src = src;
        this.lastProceedError = lastProceedError;
        this.guardianName = guardianName;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public String getLastProceedError() {
        return lastProceedError;
    }

    public void setLastProceedError(String lastProceedError) {
        this.lastProceedError = lastProceedError;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }
}
