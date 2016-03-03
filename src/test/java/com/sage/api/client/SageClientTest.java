package com.sage.api.client;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

/**
 * Created by Pat on 3/2/2016.
 */
public class SageClientTest extends TestCase {

    private File file;
    private SageClient testObject;

    public void setUp() throws Exception {
        super.setUp();
        testObject = new SageClient();
        file = new File("C:\\Users\\Pat\\Documents\\cs491\\TestClass.java");
    }

    public void tearDown() throws Exception {

    }

    public void testVerifyImplementsSageTask() throws Exception {
        boolean result = testObject.verifyImplementsSageTask(file);
        assertEquals(true, result);
    }

    // Test if String object is null
    public void testFileToBase64String1() throws IOException {
        String result = testObject.fileToBase64String(file);
        assertNotNull(result);
    }
}