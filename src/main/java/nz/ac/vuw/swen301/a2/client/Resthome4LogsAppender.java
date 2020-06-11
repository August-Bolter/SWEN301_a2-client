package nz.ac.vuw.swen301.a2.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Resthome4LogsAppender extends AppenderSkeleton {

    public URI getLogServiceURL() {
        return logServiceURL;
    }

    public void setLogServiceURL(URI logServiceURL) {
        this.logServiceURL = logServiceURL;
    }

    private URI logServiceURL;

    public Resthome4LogsAppender() {
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost("localhost").setPort(8080).setPath("/resthome4logs/logs");
        URI uri = null;
        try {
            uri = builder.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        setLogServiceURL(uri);
    }

    protected void append(LoggingEvent loggingEvent) {
        if (loggingEvent != null) { //Accounting for null
            JsonObject newObj = new JsonObject(); //Creating JSON object
            /* Assigning important attributes like logger, level etc. to JSON object */
            newObj.addProperty("id", UUID.randomUUID().toString());
            newObj.addProperty("message", loggingEvent.getRenderedMessage());
            Date date = new Date(loggingEvent.getTimeStamp());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.format(date);
            newObj.addProperty("timestamp", date.toString());
            newObj.addProperty("thread", loggingEvent.getThreadName());
            newObj.addProperty("logger", loggingEvent.getFQNOfLoggerClass());
            newObj.addProperty("level", loggingEvent.getLevel().toString());
            if (loggingEvent.getThrowableInformation() != null) {
                newObj.addProperty("errorDetails", loggingEvent.getThrowableInformation().toString());
            }
            Gson g = new Gson();
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost postRequest = new HttpPost(logServiceURL);
            try {
                postRequest.setEntity(new StringEntity(newObj.toString()));
                HttpResponse response = httpClient.execute(postRequest);
                System.out.println(EntityUtils.toString(response.getEntity()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void close() {

    }

    public boolean requiresLayout() {
        return false;
    }
}
