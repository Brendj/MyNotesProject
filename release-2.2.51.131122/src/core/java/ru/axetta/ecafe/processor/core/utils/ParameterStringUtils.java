/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import java.util.HashMap;
import java.util.Map;

public class ParameterStringUtils {
    
    public static String extractParameters(String prefix, String s) {
        if (s==null || s.length()==0) return null;
        StringBuffer params = new StringBuffer();
        int pos=0;
        for (;;) {
            if (pos>=s.length()) break;
            int startPos = s.indexOf('{', pos);
            if (startPos==-1) break;
            int endPos = s.indexOf('}', startPos);
            if (endPos==-1) break;
            params.append('{'+prefix+s.substring(startPos+1, endPos + 1));
            pos = endPos+1;
        }
        return params.toString();
    }

    public static void extractParameters(String prefix, String s, HashMap<String, String> payAddInfo) {
        int pos=0;
        for (;;) {
            if (pos>=s.length()) break;
            int startPos = s.indexOf('{', pos);
            if (startPos==-1) break;
            int eqPos = s.indexOf('=', startPos);
            if (eqPos==-1) break;
            int endPos = s.indexOf('}', eqPos);
            if (endPos==-1) break;
            String parName = s.substring(startPos+1, eqPos);
            String parValue = s.substring(eqPos+1, endPos);
            payAddInfo.put(prefix+parName, parValue);
            pos = endPos+1;
        }
        
    }

    public static String toString(HashMap<String, String> addInfo) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> e : addInfo.entrySet()) {
            sb.append('{').append(e.getKey()).append('=').append(e.getValue()).append('}');
        }
        return sb.toString();
    }
}
