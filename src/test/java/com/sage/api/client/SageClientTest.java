package com.sage.api.client;

import com.sage.api.models.Goat;
import com.sage.api.models.Job;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Pat on 3/2/2016.
 */
public class SageClientTest extends TestCase {

    //private File isNotJava, isJavaWith, isJavaWithout;
    private File classWithSageTask;
    private SageClient testObject;

    public void setUp() throws Exception {
        super.setUp();
        testObject = new SageClient();
        //isJavaWith = new File("C:\\Users\\Pat\\Desktop\\Sage\\TestClassWith.java");
        //isJavaWithout = new File ("C:\\Users\\Pat\\Desktop\\Sage\\TestClassWithout.java");
        //isNotJava = new File("C:\\Users\\Pat\\Desktop\\Sage\\TextFile.txt");
        classWithSageTask = new File("src\\test\\java\\ClassWithSageTask.java");
    }

    public void tearDown() throws Exception {

    }

    // Tests the goat endpoint
    public void testGoat() throws IOException, InterruptedException {
        try {
            Map<String, String> map = new HashMap<String, String>();
            map.put("age", "10");
            map.put("aggression", "100");
            map.put("goatId", "65");
            map.put("weight", "500");
            List<Goat> goatList = testObject.requestGoats(map);
            for (Goat goat : goatList) {
                System.out.println(goat.getGoatId());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Test the placeJobOrder endpoint
    public void testPlaceBatchOrder() throws IOException, InterruptedException, ExecutionException {
        List<byte[]> dataSet = new ArrayList<byte[]>();
        for (int i = 0; i < 50; i++) {
            dataSet.add("GarbageData".getBytes());
        }
        Map<Integer, Integer> jobMap = testObject.placeBatchOrder(classWithSageTask, new BigDecimal(200), 360000, dataSet);
        //Collections.sort(jobMap);
        for (int id : jobMap.keySet()) {
            System.out.println(id);
        }
    }

    public void testGetJob() throws IOException, InterruptedException {
        try {
            Job job = testObject.getJob(750);
            if (job != null) {
                System.out.println(job.getJobId());
                System.out.println(job.getBounty());
                System.out.println(job.getOrdererId());
                System.out.println(job.getNodeId());
                System.out.println(job.getStatus());
                System.out.println(job.getTimeout());
                System.out.println(job.getData());
                System.out.println(job.getResult());
                System.out.println(job.getCompletion());
            }
            else {
                System.out.println("Job was null: Either didn't exist or you do not have access to it");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testPollJob() throws IOException, InterruptedException {
        try {
            boolean completed = testObject.pollJob(750);
            if (completed) {
                System.out.println("Completed");
            }
            else {
                System.out.println("Not Completed");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testLogout() {
        //testObject.logoutGoogle();
        testObject.logout();
    }

    /*
    // Tests that a Java file implementing the interface SageTask passed into verifyImplementsSageTask will return true
    public void testVerifyImplementsSageTaskWithInterface() throws IOException {
        try {
            boolean result = testObject.verifyImplementsSageTask(isJavaWith);
            assertEquals(true, result);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Tests that a Java file not implementing the interface SageTask passed into verifyImplementsSageTask will return false
    public void testVerifyImplementsSageTaskWithoutInterface() throws IOException {
        try {
            boolean result = testObject.verifyImplementsSageTask(isJavaWithout);
            assertEquals(false, result);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Tests that a non Java file passed into verifyImplementsSageTask will return false
    public void testVerifyImplementsSageTaskNotJava() throws IOException {
        try {
            boolean result = testObject.verifyImplementsSageTask(isNotJava);
            assertEquals(false, result);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

    // Tests that a Java file implementing the interface SageTask passed into fileToBase64String will return non null
    /*public void testFileToBase64StringWithInterface() throws IOException {
        try {
            String result = testObject.fileToBase64String(isJavaWith);
            assertNotNull(result);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    // Tests that a Java file not implementing the interface SageTask passed into fileToBase64String will return null
    /*public void testFileToBase64StringWithoutInterface() throws IOException {
        try {
            String result = testObject.fileToBase64String(isJavaWithout);
            assertNull(result);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    // Tests that a non Java file passed into fileToBase64String will return null
   /* public void testFileToBase64StringNotJava() throws IOException {
        try {
            String result = testObject.fileToBase64String(isNotJava);
            assertNull(result);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    // Tests that the Base64 encoded file name that is returned contains just the file name without the extension
    /*public void testFileToBase64StringFileNameWithoutExtension() throws IOException {
        try {
            String result = new String(Base64.decode(testObject.fileToBase64String(isJavaWith).split("\\.")[0]), "UTF-8");
            assertEquals(isJavaWith.getName().split("\\.")[0], result);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}

