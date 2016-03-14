package com.sage.api.client;

import com.sage.api.models.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SageClient {

    private static final String ENDPOINT_ROOT = "http://sage-ws.ddns.net:8080/sage/";
    private static final String ENDPOINT_GOAT = ENDPOINT_ROOT + "alpaca/goats";
    private static final String ENDPOINT_PLACE_JOBORDER = ENDPOINT_ROOT + "alpaca/jobOrders";
    private static final String ENDPOINT_ANDROID_NODES = ENDPOINT_ROOT + "alpaca/androidNodes";

    public List<Goat> requestGoats(Map<String, String> map, String googleToken, String sageToken) throws IOException {
        List<Goat> goatList = new ArrayList<Goat>();
        String responseJSON = executeHttpRequest(ENDPOINT_GOAT, "GET", map, googleToken, sageToken, null);
        List<Object> objectList = buildObjectsFromJSON(responseJSON, "goat");
        if (objectList != null) {
            for (Object object : objectList) {
                goatList.add((Goat)object);
            }
        }
        return goatList;
    }

    public int placeJobOrder(String googleToken, String sageToken, int bounty, long timeOut, byte[] data,
                             File javaFile) throws IOException {
        int orderId = -1;
        String encodedJava = fileToBase64String(javaFile);
        if (encodedJava != null) {
            JobOrder jobOrder = new JobOrder(bounty, timeOut, data, encodedJava);
            ObjectMapper mapper = new ObjectMapper();
            String jobOrderJSON = mapper.writeValueAsString(jobOrder);
            String responseJSON = executeHttpRequest(ENDPOINT_PLACE_JOBORDER, "POST", null, googleToken, sageToken, jobOrderJSON);
            if (!responseJSON.equals("null")) {
                orderId = Integer.parseInt(responseJSON);
            }
        }
        return orderId;
    }

    private String executeHttpRequest(String endpoint, String requestType, Map<String,String> params,
                                      String googleToken, String sageToken, String content) throws IOException {
        String responseBody;


        if (requestType.toLowerCase().equals("get")) {
            if (params != null && !params.isEmpty()) {
                endpoint = endpoint.concat("?");
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    endpoint = endpoint.concat(entry.getKey().concat("=").concat(entry.getValue()).concat("&"));
                }
                endpoint = endpoint.substring(0, endpoint.length()-1);
            }


        }

        CloseableHttpClient httpclient = HttpClients.createDefault();

        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

            public String handleResponse(
                    final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };

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
            System.out.println("Executing request " + httpRequest.getRequestLine());
            responseBody = httpclient.execute(httpRequest, responseHandler);
            httpclient.close();
        }
        else {
            return "";
        }
        return responseBody;
    }

    private List<Object> buildObjectsFromJSON(String JSON, String identifier) {
        List<Object> objects = new ArrayList<Object>();

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        try {
            if (!JSON.equals("null")) {
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
                else if (identifier.equals("joborder")) {
                    objects = mapper.readValue(JSON, new TypeReference<List<JobOrder>>() {
                    });
                }
                else if (identifier.equals("user")) {
                    objects = mapper.readValue(JSON, new TypeReference<List<User>>() {
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

    /*
        TODO: Comment this method to explain what's going on.
     */
    public boolean verifyImplementsSageTask(File file) throws IOException {
        if (file.getName().split("\\.")[1].toLowerCase().equals("java")) {
            Scanner scanner = new Scanner(file);
            String line = "";
            boolean foundPackage = false;
            boolean foundImplements = false;
            boolean multilineMode = false;
            boolean redoLine = false;

            // Read each line of file, ignoring comments, until the implements and package import statements are found
            while (scanner.hasNextLine()) {
                if (!redoLine) {
                    line = scanner.nextLine();
                }
                else {
                    redoLine = false;
                }
                if (multilineMode) {
                    if (line.contains("*/")) {
                        multilineMode = false;
                        redoLine = true;
                        line = line.substring(line.indexOf("*/")+2,line.length());
                    }
                }
                else {
                    if (line.contains("//") || line.contains("/*")) {
                        if ((line.contains("//") && line.contains("/*") && line.indexOf("*/") > line.indexOf("//")) || (line.contains("//") && !line.contains("/*"))) {
                            // Single line comment logic
                            line = line.substring(0,line.indexOf("//"));
                            if (!foundPackage && line.contains("import com.sage.task.SageTask;")) {
                                foundPackage = true;
                            }
                            if (!foundImplements && line.contains("implements SageTask")) {
                                foundImplements = true;
                            }
                            if (foundPackage && foundImplements) {
                                return true;
                            }
                        }
                        else {
                            // Multiline comment logic
                            if (line.contains("*/") && line.indexOf("*/") > line.indexOf(("/*"))+1) {
                                line = line.substring(0,line.indexOf("/*")) + line.substring(line.indexOf("*/")+2,line.length());
                                redoLine = true;
                            }
                            else {
                                if (!foundPackage && line.substring(0,line.indexOf("/*")).contains("import com.sage.task.SageTask;")) {
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
                    else {
                        if (!foundPackage && line.contains("import com.sage.task.SageTask;")) {
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