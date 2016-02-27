package com.sage.rest.client;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.sage.rest.models.Goat;
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
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.JAXBException;

public class GoatHttpClientTest {
    public static void main(String[] args) throws IOException, JAXBException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet("http://sage-ws.ddns.net:8080/sage-ws/0.1/goats");

            String tempToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImNlZjUwNTEzNjVjMjBiNDkwODg2N2UyZjg1ZGUxZTU0MWM2Y2NkM2MifQ."
            + "eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXRfaGFzaCI6ImRJeUJhNGlid2tSOUdPeU4yZEUxTWciLCJhdWQiOiI2NjU1NTEy"
            + "NzQ0NjYtazllNW91bjIxY2hlN3FhbW0yY3Q5Ym42MDNkc3M2NW4uYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTQz"
            + "ODkyMTYwODI4ODY4Njc0NjAiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiYXpwIjoiNjY1NTUxMjc0NDY2LWs5ZTVvdW4yMWNoZTdxYW1t"
            + "MmN0OWJuNjAzZHNzNjVuLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiZW1haWwiOiJuam9obmhhbGVAZ21haWwuY29tIiwiaWF0"
            + "IjoxNDU2NTkxMzI2LCJleHAiOjE0NTY1OTQ5MjZ9.VX5oqY3OrnqLFaGaifu6JV_PWlgHmfBgE1c1o5cO9aNVoLxFFdjH523UvMwX1d7"
            + "VGkbvAety7KgWDNIftMrwV9OpyR0vGdwuxcjkb7ICOqAoQuSFFj5P-jd1r7KhCFo40e7NUHDNDBZoqjpsT0KGxui8PxfADVuhWNKjSK0"
            + "Fb7IjlDWEuPl8qJe58nqwCHFjhfQaOC4xTBazC_VdteDSsjnVLy3MFHK-uVQjl0pINt3mYco5sNvTpheWjKic9cwv8J_HDjy0eUv0-aF"
            + "GqJO_ADqGplVdpgzt_DrHHhlCyGVPfDwHsuMiGaK7MjSXnaCox5NBvy3kEcXBDDkYQihgEQ";

            httpget.setHeader("IdToken", tempToken);
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
    }
}
