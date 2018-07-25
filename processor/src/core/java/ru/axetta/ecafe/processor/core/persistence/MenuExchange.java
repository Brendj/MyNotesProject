/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.utils.CompressUtils;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: kolpakov
 * Date: 09.11.2010
 * Time: 23:00:55
 * To change this template use File | Settings | File Templates.
 */
public class MenuExchange {
    public final static int FLAG_NONE=0, FLAG_ANCHOR_MENU=1, FLAG_SETTINGS=2;

    final static int MAX_MENU_DATA_LENGTH = 600000;

    private CompositeIdOfMenuExchange compositeIdOfMenuExchange;
    private String menuData;
    private int flags;

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    protected MenuExchange() {
        // For Hibernate only
    }
    final static char COMPRESS_MARKER='@';
    final static int MIN_LENGTH_FOR_COMPRESSION = 50 * 1024;

    public MenuExchange(Date menuDate, Long idOfOrg, String menuData, int flags) throws Exception {
        compositeIdOfMenuExchange = new CompositeIdOfMenuExchange(menuDate, idOfOrg);
        if (menuData!=null && menuData.length()>MIN_LENGTH_FOR_COMPRESSION) {
            String compressedData = COMPRESS_MARKER+CompressUtils.compressDataInBase64(menuData);
            if (MAX_MENU_DATA_LENGTH < compressedData.length()) {
                throw new Exception("Menu data length exceeds maximum size: " + MAX_MENU_DATA_LENGTH+"; size: "+menuData.length()+"; compressed: "+compressedData.length());
            }
            menuData = compressedData;
        }

        this.menuData = menuData;
        this.flags = flags;
    }

    public String getMenuData() {
        return menuData;
    }

    public String getMenuDataWithDecompress() throws Exception {
        if (menuData==null) return null;
        if (menuData.length()>0 && menuData.charAt(0)==COMPRESS_MARKER) {
            return CompressUtils.decompressDataFromBase64(menuData.substring(1));
        }
        return menuData;
    }



    public void setMenuData(String menuData) {
        this.menuData = menuData;
    }

    public MenuExchange(CompositeIdOfMenuExchange idOfMenuExchange, String menuData) {
        this.compositeIdOfMenuExchange = idOfMenuExchange;
        this.menuData = menuData;
    }

    public CompositeIdOfMenuExchange getCompositeIdOfMenuExchange() {
        return compositeIdOfMenuExchange;
    }

    public void setCompositeIdOfMenuExchange(CompositeIdOfMenuExchange compositeIdOfMenuExchange) {
        this.compositeIdOfMenuExchange = compositeIdOfMenuExchange;
    }

    @Override
    public String toString() {
        return "MenuExchange{" + "compositeIdOfMenuExchange=" + compositeIdOfMenuExchange + ", menuData='" + menuData
                + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuExchange)) {
            return false;
        }

        MenuExchange that = (MenuExchange) o;

        if (compositeIdOfMenuExchange != null ? !compositeIdOfMenuExchange.equals(that.compositeIdOfMenuExchange)
                : that.compositeIdOfMenuExchange != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = compositeIdOfMenuExchange != null ? compositeIdOfMenuExchange.hashCode() : 0;
        return result;
    }
}
