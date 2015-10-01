package ru.axetta.ecafe.processor.core.client;

import ru.axetta.ecafe.processor.core.persistence.Org;

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
        System.out.println("308430438 " + ContractIdGenerator.luhnTest("308430438"));
        System.out.println("308499995 " + ContractIdGenerator.luhnTest("308499995"));
        System.out.println("1308400009 " + ContractIdGenerator.luhnTest("1308400009"));
        System.out.println("1308499993 " + ContractIdGenerator.luhnTest("1308499993"));
        System.out.println("2308400007 " + ContractIdGenerator.luhnTest("2308400007"));
        System.out.println("9308499996 " + ContractIdGenerator.luhnTest("9308499996"));
        System.out.println("10308400000 " + ContractIdGenerator.luhnTest("10308400000"));
    }

    @Test
    public void testGetNextContractId() throws Exception {
        System.out.println("Result=" + ContractIdGenerator.getNextContractId(3084, 3043));
        System.out.println("Result=" + ContractIdGenerator.getNextContractId(3084, 9999));
        System.out.println("Result=" + ContractIdGenerator.getNextContractId(3084, 10000));
        System.out.println("Result=" + ContractIdGenerator.getNextContractId(3084, 19999));
        System.out.println("Result=" + ContractIdGenerator.getNextContractId(3084, 20000));
        System.out.println("Result=" + ContractIdGenerator.getNextContractId(3084, 99999));
        System.out.println("Result=" + ContractIdGenerator.getNextContractId(3084, 100000));
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