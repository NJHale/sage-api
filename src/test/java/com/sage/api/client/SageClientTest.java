package com.sage.api.client;

import com.sage.api.models.Goat;
import com.sage.api.models.Job;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pat on 3/2/2016.
 */
public class SageClientTest extends TestCase {

    private File isNotJava, isJavaWith, isJavaWithout;
    private File classWithSageTask;
    private SageClient testObject;
    /*
    private String tempToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImNlZjUwNTEzNjVjMjBiNDkwODg2N2UyZjg1ZGUxZTU0MWM2Y2NkM2MifQ."
            + "eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXRfaGFzaCI6ImRJeUJhNGlid2tSOUdPeU4yZEUxTWciLCJhdWQiOiI2NjU1NTEy"
            + "NzQ0NjYtazllNW91bjIxY2hlN3FhbW0yY3Q5Ym42MDNkc3M2NW4uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTQz"
            + "ODkyMTYwODI4ODY4Njc0NjAiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXpwIjoiNjY1NTUxMjc0NDY2LWs5ZTVvdW4yMWNoZTdxYW1t"
            + "MmN0OWJuNjAzZHNzNjVuLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiZW1haWwiOiJuam9obmhhbGVAZ21haWwuY29tIiwiaWF0"
            + "IjoxNDU2NTkxMzI2LCJleHAiOjE0NTY1OTQ5MjZ9.VX5oqY3OrnqLFaGaifu6JV_PWlgHmfBgE1c1o5cO9aNVoLxFFdjH523UvMwX1d7"
            + "VGkbvAety7KgWDNIftMrwV9OpyR0vGdwuxcjkb7ICOqAoQuSFFj5P-jd1r7KhCFo40e7NUHDNDBZoqjpsT0KGxui8PxfADVuhWNKjSK0"
            + "Fb7IjlDWEuPl8qJe58nqwCHFjhfQaOC4xTBazC_VdteDSsjnVLy3MFHK-uVQjl0pINt3mYco5sNvTpheWjKic9cwv8J_HDjy0eUv0-aF"
            + "GqJO_ADqGplVdpgzt_DrHHhlCyGVPfDwHsuMiGaK7MjSXnaCox5NBvy3kEcXBDDkYQihgEQ";
    */

    public void setUp() throws Exception {
        super.setUp();
        testObject = new SageClient();
        //isJavaWith = new File("C:\\Users\\Pat\\Desktop\\Sage\\TestClassWith.java");
        //isJavaWithout = new File ("C:\\Users\\Pat\\Desktop\\Sage\\TestClassWithout.java");
        //isNotJava = new File("C:\\Users\\Pat\\Desktop\\Sage\\TextFile.txt");
        classWithSageTask = new File("C:\\Users\\Pat\\Documents\\cs491\\sage-api\\src\\test\\java\\ClassWithSageTask.java");
    }

    public void tearDown() throws Exception {

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
    public void testPlaceJobOrder() throws IOException, InterruptedException {
        int orderId = testObject.placeJobOrder(100, 1000000,
                "SageTokenGarbage".getBytes(), classWithSageTask);
        if (orderId == -1) {
            System.out.println("Something went wrong!");
        }
        else {
            System.out.println("The orderID is: " + orderId);
        }
        assertNotSame(-1, orderId);
    }

    public void testGetJob() throws IOException, InterruptedException {
        try {
            Job job = testObject.getJob(199);
            if (job != null) {
                System.out.println(job.getJobId());
                System.out.println(job.getBounty());
                System.out.println(job.getOrdererId());
                System.out.println(job.getNodeId());
                System.out.println(job.getStatus());
                System.out.println(job.getTimeout());
                System.out.println(job.getEncodedDex());
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
            boolean completed = testObject.pollJob(199);
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

    /*public void testGoogleLogout() {
        testObject.googleLogout();
    }*/
}