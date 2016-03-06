package com.sage.api.client;

import com.sage.api.models.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

public class SageClient {

    public static final String ENDPOINT_GOAT = "http://sage-ws.ddns.net:8080/sage/0.1/goats";

    public List<Goat> requestGoats(Map<String, String> map, String googleToken, String sageToken) throws IOException {
        List<Goat> goatList = new ArrayList<Goat>();
        String responseJSON = executeHttpRequest(ENDPOINT_GOAT, "GET", map, googleToken, sageToken);
        List<Object> objectList = buildObjectsFromJSON(responseJSON);
        if (objectList != null) {
            for (Object object : objectList) {
                goatList.add((Goat)object);
            }
        }
        return goatList;
    }

    public String executeHttpRequest(String endpoint, String requestType, Map<String,String> params,
                                            String googleToken, String sageToken) throws IOException {
        String responseBody;

        if (params != null && !params.isEmpty()) {
            endpoint = endpoint.concat("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                endpoint = endpoint.concat(entry.getKey().concat("=").concat(entry.getValue()).concat("&"));
            }
            endpoint = endpoint.substring(0, endpoint.length()-1);
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
            httpRequest.setHeader("Accept", "application/json");
            httpRequest.setHeader("GoogleToken", googleToken);
            httpRequest.setHeader("SageToken", sageToken);
            //System.out.println("Executing request " + httpRequest.getRequestLine());
            responseBody = httpclient.execute(httpRequest, responseHandler);
            httpclient.close();
        }
        else {
            return "";
        }
        return responseBody;
    }

    public List<Object> buildObjectsFromJSON(String JSON) {
        List<Object> objects = null;

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        try {
            if (!JSON.equals("null")) {
                JSONObject jsonObject = new JSONObject(JSON);
                String objectKey = jsonObject.keys().next();
                Object objectFromJSON = jsonObject.get(objectKey);
                String JSONString = objectFromJSON.toString();

                if (objectKey.equals("goat")) {
                    objects = mapper.readValue(JSONString, new TypeReference<List<Goat>>() {
                    });
                }
                else if (objectKey.equals("androidnode")) {
                    objects = mapper.readValue(JSONString, new TypeReference<List<AndroidNode>>() {
                    });
                }
                else if (objectKey.equals("job")) {
                    objects = mapper.readValue(JSONString, new TypeReference<List<Job>>() {
                    });
                }
                else if (objectKey.equals("joborder")) {
                    objects = mapper.readValue(JSONString, new TypeReference<List<JobOrder>>() {
                    });
                }
                else if (objectKey.equals("user")) {
                    objects = mapper.readValue(JSONString, new TypeReference<List<User>>() {
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

    public String fileToBase64String(File file) throws IOException {
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

    public boolean verifyImplementsSageTask(File file) throws IOException {
        // Verify the file is a Java file
        if (file.getName().split("\\.")[1].equals("java")) {
            Scanner scanner = new Scanner(file);

            // Read each line of file until "implements SageTask" is found
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains("implements SageTask")) {
                    return true;
                }
            }
            return false;
        }
        else {
            return false;
        }
    }
}
