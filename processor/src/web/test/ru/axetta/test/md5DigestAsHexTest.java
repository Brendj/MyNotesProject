/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.test;

import junit.framework.TestCase;

import ru.axetta.ecafe.processor.core.utils.CryptoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class md5DigestAsHexTest extends TestCase {
    public void testMd5Digest(){
        System.out.println(CryptoUtils.MD5((""+System.currentTimeMillis())));
    }

    public void testRegEx(){
        String str = "0=1;1=1;2=0;3=0;4=0;5=0;6=0;7=0;8=0;9=0;10=0;11=0;12=0;13=0;14=0;15=0;16=0;17=0;18=0;19=0;20=0;21=0;22=0;23=0;24=0;25=0;26=0;27=0;28=0;29=0;30=0;31=0;32=0;33=0;34=0;35=0;36=0;37=0;38=0;39=0;40=0;41=0;42=0;43=0;44=0;45=0;46=0;47=0;48=0;49=1";

        List<String> allMatches = new ArrayList<String>();
        Matcher m = Pattern.compile("\\d+(=1)")
                .matcher(str);
        while (m.find()) {
            allMatches.add(m.group().replace("=1",""));
        }
        for(String inStr: allMatches){
            System.out.println(inStr);
        }
    }

}
