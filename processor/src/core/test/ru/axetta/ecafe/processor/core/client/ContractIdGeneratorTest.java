package ru.axetta.ecafe.processor.core.client;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.09.13
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
public class ContractIdGeneratorTest {

    @Test
    public void testLuhnTest() throws Exception {
        System.out.println("12525 " + ContractIdGenerator.luhnTest("12525"));
        System.out.println("125 " + ContractIdGenerator.luhnTest("125"));
    }

    //@Test
    //public void testLuhnTest() throws Exception {
    //    BufferedReader br = new BufferedReader(new FileReader("c:\\contractIds1.csv"));
    //    try {
    //        String line = br.readLine();
    //        int totalCount =0;
    //        int count=0;
    //        while (line != null) {
    //            totalCount++;
    //            //if(!ContractIdGenerator.luhnTest(line)){
    //            //    System.out.println(count +": " +line);
    //            //    count++;
    //            //} else {
    //            //    for (int i=1;i<10; i++){
    //            //        if(ContractIdGenerator.luhnTest(line+"0"+i)){
    //            //            System.out.println(count +": " +line + " subscribe: "+line+"0"+i);
    //            //            count++;
    //            //        }
    //            //    }
    //            //}
    //            line = br.readLine();
    //        }
    //        double percent = (count * 100.0) / totalCount;
    //        System.out.printf("totalCount=%d \t error=%d \t percent=%s%n", totalCount, count, percent);
    //    } finally {
    //        br.close();
    //    }
    //}
}