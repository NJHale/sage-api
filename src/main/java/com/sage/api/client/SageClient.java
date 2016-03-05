package com.sage.api.client;

import com.sage.api.models.Goat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import javax.swing.*;
import javax.xml.bind.JAXBException;

public class SageClient {

    public static final String ENDPOINT_GOAT = "http://sage-ws.ddns.net:8080/sage/0.1/goats";

    public static void main(String[] args) throws IOException, JAXBException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(ENDPOINT_GOAT);

            String tempToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImNlZjUwNTEzNjVjMjBiNDkwODg2N2UyZjg1ZGUxZTU0MWM2Y2NkM2MifQ."
            + "eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXRfaGFzaCI6ImRJeUJhNGlid2tSOUdPeU4yZEUxTWciLCJhdWQiOiI2NjU1NTEy"
            + "NzQ0NjYtazllNW91bjIxY2hlN3FhbW0yY3Q5Ym42MDNkc3M2NW4uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTQz"
            + "ODkyMTYwODI4ODY4Njc0NjAiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXpwIjoiNjY1NTUxMjc0NDY2LWs5ZTVvdW4yMWNoZTdxYW1t"
            + "MmN0OWJuNjAzZHNzNjVuLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiZW1haWwiOiJuam9obmhhbGVAZ21haWwuY29tIiwiaWF0"
            + "IjoxNDU2NTkxMzI2LCJleHAiOjE0NTY1OTQ5MjZ9.VX5oqY3OrnqLFaGaifu6JV_PWlgHmfBgE1c1o5cO9aNVoLxFFdjH523UvMwX1d7"
            + "VGkbvAety7KgWDNIftMrwV9OpyR0vGdwuxcjkb7ICOqAoQuSFFj5P-jd1r7KhCFo40e7NUHDNDBZoqjpsT0KGxui8PxfADVuhWNKjSK0"
            + "Fb7IjlDWEuPl8qJe58nqwCHFjhfQaOC4xTBazC_VdteDSsjnVLy3MFHK-uVQjl0pINt3mYco5sNvTpheWjKic9cwv8J_HDjy0eUv0-aF"
            + "GqJO_ADqGplVdpgzt_DrHHhlCyGVPfDwHsuMiGaK7MjSXnaCox5NBvy3kEcXBDDkYQihgEQ";

            httpget.setHeader("Accept", "application/json");
            httpget.setHeader("GoogleToken", tempToken);
            httpget.setHeader("SageToken", "SageTokenGarbage");

            System.out.println("Executing request " + httpget.getRequestLine());

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
            String responseBody = httpclient.execute(httpget, responseHandler);
            System.out.println("----------------------------------------");

            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

            try {
                if (!responseBody.equals("null")) {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    Object goatObject = jsonObject.get("goat");
                    String JSONString = goatObject.toString();
                    List<Goat> goats = mapper.readValue(JSONString, new TypeReference<List<Goat>>() {
                    });
                    System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(goats));
                }
                else {
                    System.out.println("Empty Response");
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
        } finally {
            httpclient.close();
        }

        // Temporary way to choose a file in a window
        File file;
        do {
            file = chooseFileGUI();
        } while (file == null);

        // Encode the file to base64
        String encodedFile = fileToBase64String(file);
        if (encodedFile != null) {
            System.out.println(encodedFile);
        }
        else {
            System.out.println("\nFile was not a java file or did not implement the SageTask interface");
        }
    }

    public static String fileToBase64String(File file) throws IOException {
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

    public static boolean verifyImplementsSageTask(File file) throws IOException {
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

    public static File chooseFileGUI() {
        File file;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(new JPanel());
        if (result == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
        }
        else {
            file = null;
        }
        return file;
    }
}
