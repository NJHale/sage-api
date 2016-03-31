package com.sage.api.client;

import com.sage.api.models.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.sun.deploy.net.URLEncoder;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

public class SageClient {

    private static final String ENDPOINT_ROOT = "http://sage-ws.ddns.net:8080/sage/";
    private static final String ENDPOINT_GOAT = ENDPOINT_ROOT + "alpaca/goats";
    private static final String ENDPOINT_PLACE_JOBORDER = ENDPOINT_ROOT + "alpaca/jobOrders";
    private static final String ENDPOINT_GET_JOB = ENDPOINT_ROOT + "alpaca/jobs";
    private static final String ENDPOINT_POLL_JOB = ENDPOINT_ROOT + "alpaca/jobs/jobid/status";
    private static final String ENDPOINT_ANDROID_NODES = ENDPOINT_ROOT + "alpaca/androidNodes";

    private static final String CLIENT_ID = "304221060563-b5mrhqtkl8adrpo42kb4inb9s20po7pb.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "le1PtSeQiLzfdTgftwf6qIZy";

    private static final String ENDPOINT_GOOGLE_AUTH = "https://accounts.google.com/o/oauth2/device/code";
    private static final String ENDPOINT_GOOGLE_TOKEN = "https://www.googleapis.com/oauth2/v4/token";

    public List<Goat> requestGoats(Map<String, String> map) throws IOException, InterruptedException {
        List<Goat> goatList = new ArrayList<Goat>();
        String responseJSON = executeHttpRequest(ENDPOINT_GOAT, "GET", map, getGoogleId(), "SageTokenGarbage", null);
        List<Object> objectList = buildObjectsFromJSON(responseJSON, "goat");
        if (objectList != null) {
            for (Object object : objectList) {
                goatList.add((Goat)object);
            }
        }
        return goatList;
    }

    public int placeJobOrder(int bounty, long timeOut, byte[] data, File javaFile)
            throws IOException, InterruptedException {
        int orderId = -1;
        String encodedJava = fileToBase64String(javaFile);
        if (encodedJava != null) {
            JobOrder jobOrder = new JobOrder(bounty, timeOut, data, encodedJava);
            ObjectMapper mapper = new ObjectMapper();
            String jobOrderJSON = mapper.writeValueAsString(jobOrder);
            String responseJSON = executeHttpRequest(ENDPOINT_PLACE_JOBORDER, "POST", null, getGoogleId(),
                    "SageTokenGarbage", jobOrderJSON);
            if (!responseJSON.equals("null")) {
                orderId = Integer.parseInt(responseJSON);
            }
        }
        return orderId;
    }

    public Job getJob(int jobId) throws IOException, InterruptedException {
        Job job = null;
        String responseJSON = executeHttpRequest(ENDPOINT_GET_JOB + "/" + jobId, "GET", null, getGoogleId(),
                "SageTokenGarbage", null);
        System.out.println(responseJSON);
        List<Object> objectList = buildObjectsFromJSON(responseJSON, "job");
        if (objectList.size() > 0) {
            job = (Job)objectList.get(0);
        }
        return job;
    }

    public boolean pollJob(int jobId) throws IOException, InterruptedException {
        String responseJSON = executeHttpRequest(ENDPOINT_POLL_JOB.replaceAll("jobid",Integer.toString(jobId)),
                "GET",null,"","SageTokenGarbage","");
        System.out.println(ENDPOINT_POLL_JOB.replaceAll("jobid",Integer.toString(jobId)));
        System.out.println(responseJSON);
        List<Object> objectList = buildObjectsFromJSON(responseJSON, "jobstatus");
        JobStatus jobStatus = null;
        if (objectList.size() > 0) {
            jobStatus = (JobStatus)objectList.get(0);
        }

        return jobStatus == null || jobStatus == JobStatus.DONE || jobStatus == JobStatus.ERROR || jobStatus == JobStatus.TIMED_OUT;
    }

    public void googleLogout() {
        Preferences userPreferences = Preferences.userNodeForPackage(getClass());
        userPreferences.remove("SAGE_GOOGLEID");
        userPreferences.remove("SAGE_GOOGLEACCESS");
        userPreferences.remove("SAGE_GOOGLEREFRESH");
        userPreferences.remove("SAGE_GOOGLEEXPIRE");
    }

    private String getGoogleId() throws IOException, InterruptedException {
        Preferences userPreferences = Preferences.userNodeForPackage(getClass());
        String googleId = userPreferences.get("SAGE_GOOGLEID", "EMPTY");
        String googleAccessToken = userPreferences.get("SAGE_GOOGLEACCESS", "EMPTY");
        String googleRefreshToken = userPreferences.get("SAGE_GOOGLEREFRESH", "EMPTY");
        long expiredTime = userPreferences.getLong("SAGE_GOOGLEEXPIRE", 0);
        if (!googleId.equals("EMPTY") && !googleAccessToken.equals("EMPTY")
                && expiredTime > System.currentTimeMillis()) {
            return googleId;
        }

        if (!googleRefreshToken.equals("EMPTY")) {
            // Attempt to use refresh token to get new access token.
            Map<String,String> refreshTokenParams = new HashMap<String, String>();
            refreshTokenParams.put("client_id",CLIENT_ID);
            refreshTokenParams.put("client_secret",CLIENT_SECRET);
            refreshTokenParams.put("refresh_token",googleRefreshToken);
            refreshTokenParams.put("grant_type","refresh_token");
            String responseJSON = executeGoogleHttpRequest(ENDPOINT_GOOGLE_TOKEN,refreshTokenParams);
            JSONObject JSON = new JSONObject(responseJSON);
            if (!JSON.has("error")) {
                userPreferences.put("SAGE_GOOGLEACCESS", JSON.getString("access_token"));
                userPreferences.put("SAGE_GOOGLEID", JSON.getString("id_token"));
                userPreferences.putLong("SAGE_GOOGLEEXPIRE", JSON.getLong("expires_in")*1000
                        + System.currentTimeMillis());
                return userPreferences.get("SAGE_GOOGLEID","EMPTY");
            }
        }

        newGoogleAuth();
        return userPreferences.get("SAGE_GOOGLEID","EMPTY");
    }

    private void newGoogleAuth() throws IOException, InterruptedException {
        Preferences userPreferences = Preferences.userNodeForPackage(getClass());
        Map<String,String> newAuthParams = new HashMap<String,String>();
        Map<String,String> pollParams = new HashMap<String,String>();
        String responseJSON;
        JSONObject JSON;
        newAuthParams.put("client_id",CLIENT_ID);
        newAuthParams.put("scope","email profile");
        responseJSON = executeGoogleHttpRequest(ENDPOINT_GOOGLE_AUTH,newAuthParams);
        JSON = new JSONObject(responseJSON);
        String deviceCode = JSON.getString("device_code");
        String userCode = JSON.getString("user_code");
        String verificationURL = JSON.getString("verification_url");
        long expiresAt = JSON.getInt("expires_in")*1000 + System.currentTimeMillis();
        int interval = JSON.getInt("interval");
        System.out.println("Please enter " + verificationURL + " into a browser and use the code "
                + userCode + " to log in");
        pollParams.put("client_id",CLIENT_ID);
        pollParams.put("client_secret",CLIENT_SECRET);
        pollParams.put("code",deviceCode);
        pollParams.put("scope","email profile");
        pollParams.put("grant_type","http://oauth.net/grant_type/device/1.0");
        boolean authenticated = false;
        while (!authenticated) {
            if (System.currentTimeMillis() >= expiresAt) {
                // Have to get new user code, old code expired
                System.out.println("Last url and code are now expired.");
                responseJSON = executeGoogleHttpRequest(ENDPOINT_GOOGLE_AUTH,newAuthParams);
                JSON = new JSONObject(responseJSON);
                deviceCode = JSON.getString("device_code");
                userCode = JSON.getString("user_code");
                verificationURL = JSON.getString("verification_url");
                expiresAt = JSON.getInt("expires_in")*1000 + System.currentTimeMillis();
                interval = JSON.getInt("interval");
                pollParams.put("code",deviceCode);
                System.out.println("Please enter " + verificationURL + " into a browser and use the code "
                        + userCode + " to log in");
            }
            else {
                // Poll and check if the user authorized
                responseJSON = executeGoogleHttpRequest(ENDPOINT_GOOGLE_TOKEN,pollParams);
                JSON = new JSONObject(responseJSON);
                if (JSON.has("error")) {
                    // Not yet authenticated, wait "interval" seconds and then try again
                    try {
                        TimeUnit.SECONDS.sleep(interval);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    // User has authenticated, get access token, refresh token, id.  Mark authenticated true.
                    authenticated = true;
                    userPreferences.put("SAGE_GOOGLEACCESS", JSON.getString("access_token"));
                    userPreferences.put("SAGE_GOOGLEREFRESH", JSON.getString("refresh_token"));
                    userPreferences.put("SAGE_GOOGLEID", JSON.getString("id_token"));
                    userPreferences.putLong("SAGE_GOOGLEEXPIRE",
                            JSON.getInt("expires_in")*1000 + System.currentTimeMillis());
                }
            }
        }
    }

    private String executeGoogleHttpRequest(String endpoint, Map<String,String> params) throws IOException {
        String responseBody;

        CloseableHttpClient httpclient = HttpClients.createDefault();
        ResponseHandler<String> responseHandler = createGoogleResponseHandler();

        HttpPost httpRequest = new HttpPost(endpoint);
        httpRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        StringEntity messageBody = new StringEntity(queryStringBuilder(params));
        httpRequest.setEntity(messageBody);
        //System.out.println("Executing request " + httpRequest.getRequestLine());
        responseBody = httpclient.execute(httpRequest, responseHandler);
        httpclient.close();

        return responseBody;
    }

    private String executeHttpRequest(String endpoint, String requestType, Map<String,String> params,
                                      String googleToken, String sageToken, String content) throws IOException {
        String responseBody;

        if (requestType.toLowerCase().equals("get") && params != null && !params.isEmpty()) {
            endpoint = endpoint.concat("?").concat(queryStringBuilder(params));
        }

        CloseableHttpClient httpclient = HttpClients.createDefault();
        ResponseHandler<String> responseHandler = createResponseHandler();

        if (requestType.toLowerCase().equals("get")) {
            HttpGet httpRequest = new HttpGet(endpoint);
            httpRequest.setHeader("Accept", "application/json");
            httpRequest.setHeader("GoogleToken", googleToken);
            httpRequest.setHeader("SageToken", sageToken);
            //System.out.println("Executing request " + httpRequest.getRequestLine());
            responseBody = httpclient.execute(httpRequest, responseHandler);
            httpclient.close();
        }
        else if (requestType.toLowerCase().equals("post")) {
            HttpPost httpRequest = new HttpPost(endpoint);
            httpRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            httpRequest.setHeader("Accept", "application/json");
            httpRequest.setHeader("GoogleToken", googleToken);
            httpRequest.setHeader("SageToken", sageToken);
            StringEntity entity = new StringEntity(content);
            httpRequest.setEntity(entity);
            //System.out.println("Executing request " + httpRequest.getRequestLine());
            responseBody = httpclient.execute(httpRequest, responseHandler);
            httpclient.close();
        }
        else {
            return "";
        }
        return responseBody;
    }

    private ResponseHandler<String> createGoogleResponseHandler() {
        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            public String handleResponse(
                    final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                if ((status >= 200 && status < 300) || status == 400) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
        return responseHandler;
    }

    private ResponseHandler<String> createResponseHandler() {
        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            public String handleResponse(
                    final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 /*&& status < 300*/) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };
        return responseHandler;
    }

    private String queryStringBuilder(Map<String,String> params) {
        String queryString = "";
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                queryString = queryString.concat(entry.getKey().concat("=").concat(entry.getValue()).concat("&"));
            }
            queryString = queryString.substring(0, queryString.length()-1);
        }
        return queryString;
    }

    private List<Object> buildObjectsFromJSON(String JSON, String identifier) {
        List<Object> objects = new ArrayList<Object>();

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        try {
            if (JSON != null && !JSON.equals("null") &&  JSON.length() > 0) {
                //JSONObject jsonObject = new JSONObject(JSON);
                //String objectKey = jsonObject.keys().next();
                //Object objectFromJSON = jsonObject.get(objectKey);
                //String JSONString = objectFromJSON.toString();

                if (identifier.equals("goat")) {
                    objects = mapper.readValue(JSON, new TypeReference<List<Goat>>() {
                    });
                }
                else if (identifier.equals("androidnode")) {
                    objects = mapper.readValue(JSON, new TypeReference<List<AndroidNode>>() {
                    });
                }
                else if (identifier.equals("job")) {
                    objects = mapper.readValue(JSON, new TypeReference<List<Job>>() {
                    });
                }
                else if (identifier.equals("user")) {
                    objects = mapper.readValue(JSON, new TypeReference<List<User>>() {
                    });
                }
                else if (identifier.equals("jobstatus")) {
                    objects = mapper.readValue(JSON, new TypeReference<List<JobStatus>>() {
                    });
                }
                else {
                    return objects;
                }
            }
        }
        catch (JsonGenerationException e) {
            e.printStackTrace();
        }
        catch (JsonMappingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return objects;
    }

    private String fileToBase64String(File file) throws IOException {
        if (verifyImplementsSageTask(file)) {
            String encodedFile = null;
            try {
                String encodedFileName = Base64.encode(file.getName().split("\\.")[0].getBytes());
                String encodedFileContents = Base64.encode(Files.readAllBytes(file.toPath()));
                encodedFile = encodedFileName.concat(".").concat(encodedFileContents);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return encodedFile;
        }
        else {
            return null;
        }
    }

    private boolean verifyImplementsSageTask(File file) throws IOException {
        if (file.getName().split("\\.")[1].toLowerCase().equals("java")) {  // First check if the file is a Java file
            Scanner scanner = new Scanner(file);                            // Scanner for reading the file
            String line = "";                                               // Initialize line holder
            boolean foundPackage = false;                                   // Flag: If SageTask package has been found
            boolean foundImplements = false;                                // Flag: If implement SageTask has been found
            boolean multilineMode = false;                                  // Flag: Tracking if currently in ml mode
            boolean redoLine = false;                                       // Flag: If new line will be read from file

            while (scanner.hasNextLine()) {                                 // While there are more lines in the file
                if (!redoLine) {                                            // If flag is not set to re-test last line
                    line = scanner.nextLine();                              // Read in next line from file
                }
                else {                                                      // Otherwise
                    redoLine = false;                                       // Set flag to false, and re-test line
                }
                if (multilineMode) {                                        // If we are currently in multiline mode
                    if (line.contains("*/")) {                              // Check for end of multiline comment
                        multilineMode = false;                              // Disable multiline mode
                        redoLine = true;                                    // Flag so this line will be re-tested
                        line = line.substring(line.indexOf("*/")+2,line.length());  // Cut out multiline comment content
                    }
                }
                else {                                                      // If not in multiline mode
                    if (line.contains("//") || line.contains("/*")) {       // If the line contains a comment
                        if ((line.contains("//") && line.contains("/*") && line.indexOf("*/") > line.indexOf("//"))
                                || (line.contains("//") && !line.contains("/*"))) {     // If single line comment
                            // Single line comment logic
                            line = line.substring(0,line.indexOf("//"));    // Set line to part before comment
                            if (!foundPackage && line.contains("import com.sage.task.SageTask")) { // Check for package
                                foundPackage = true;                        // Package statement is found
                            }
                            if (!foundImplements && line.contains("implements SageTask")) { // Check for implements
                                foundImplements = true;                     // Implements statement is found
                            }
                            if (foundPackage && foundImplements) {          // If both package and implements are found
                                return true;                                // File is verified, return true
                            }
                        }
                        else {                                              // Otherwise, it's a multiline comment
                            // Multiline comment logic
                            if (line.contains("*/") && line.indexOf("*/") >
                                    line.indexOf(("/*"))+1) {               // If comment ends on the same line
                                line = line.substring(0,line.indexOf("/*")) +
                                        line.substring(line.indexOf("*/")+2,line.length()); // Cut out the ml comment
                                redoLine = true;                            // Mark line for re-testing
                            }
                            else {                                          // Otherwise, comment ends on future line
                                // Check part of line before comment for statements and enable ml mode if not found
                                if (!foundPackage && line.substring(0,line.indexOf("/*")).contains("import com.sage.task.SageTask")) {
                                    foundPackage = true;
                                }
                                if (!foundImplements && line.substring(0,line.indexOf("/*")).contains("implements SageTask")) {
                                    foundImplements = true;
                                }
                                if (foundPackage && foundImplements) {
                                    return true;
                                }
                                else {
                                    multilineMode = true;
                                }
                            }
                        }
                    }
                    else {                                                  // Otherwise line does not have a comment
                        // Check line for statements
                        if (!foundPackage && line.contains("import com.sage.task.SageTask")) {
                            foundPackage = true;
                        }
                        if (!foundImplements && line.contains("implements SageTask")) {
                            foundImplements = true;
                        }
                        if (foundPackage && foundImplements) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
        else {
            return false;
        }
    }
}