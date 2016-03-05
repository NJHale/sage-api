package com.sage.api.client;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

/**
 * Created by Pat on 3/2/2016.
 */
public class SageClientTest extends TestCase {

    private File isNotJava, isJavaWith, isJavaWithout;
    private SageClient testObject;

    public void setUp() throws Exception {
        super.setUp();
        testObject = new SageClient();
        isJavaWith = new File("C:\\Users\\Pat\\Desktop\\Sage\\TestClassWith.java");
        isJavaWithout = new File ("C:\\Users\\Pat\\Desktop\\Sage\\TestClassWithout.java");
        isNotJava = new File("C:\\Users\\Pat\\Desktop\\Sage\\TextFile.txt");
    }

    public void tearDown() throws Exception {

    }

    // Tests that a Java file implementing the interface SageTask passed into verifyImplementsSageTask will return true
    public void testVerifyImplementsSageTaskWithInterface() throws Exception {
        boolean result = testObject.verifyImplementsSageTask(isJavaWith);
        assertEquals(true, result);
    }

    // Tests that a Java file not implementing the interface SageTask passed into verifyImplementsSageTask will return false
    public void testVerifyImplementsSageTaskWithoutInterface() throws Exception {
        boolean result = testObject.verifyImplementsSageTask(isJavaWithout);
        assertEquals(false, result);
    }

    // Tests that a non Java file passed into verifyImplementsSageTask will return false
    public void testVerifyImplementsSageTaskNotJava() throws Exception {
        boolean result = testObject.verifyImplementsSageTask(isNotJava);
        assertEquals(false, result);
    }

    // Tests that a Java file implementing the interface SageTask passed into fileToBase64String will return non null
    public void testFileToBase64StringWithInterface() throws IOException {
        String result = testObject.fileToBase64String(isJavaWith);
        assertNotNull(result);
    }

    // Tests that a Java file not implementing the interface SageTask passed into fileToBase64String will return null
    public void testFileToBase64StringWithoutInterface() throws IOException {
        String result = testObject.fileToBase64String(isJavaWithout);
        assertNull(result);
    }

    // Tests that a non Java file passed into fileToBase64String will return null
    public void testFileToBase64StringNotJava() throws IOException {
        String result = testObject.fileToBase64String(isNotJava);
        assertNull(result);
    }

    // Tests that the Base64 encoded file name that is returned contains just the file name without the extension
    public void testFileToBase64StringFileNameWithoutExtension() throws IOException {
        String result = new String(Base64.decode(testObject.fileToBase64String(isJavaWith).split("\\.")[0]), "UTF-8");
        assertEquals(isJavaWith.getName().split("\\.")[0], result);
    }
}