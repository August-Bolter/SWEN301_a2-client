package nz.ac.vuw.swen301.a2.client;

import com.google.gson.JsonObject;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
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
        PropertyConfigurator.configure("src/main/resources/log4j.properties");
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
            Instant time = Instant.ofEpochMilli(loggingEvent.timeStamp);
            newObj.addProperty("timestamp", time.toString());
            newObj.addProperty("thread", loggingEvent.getThreadName());
            newObj.addProperty("logger", loggingEvent.getFQNOfLoggerClass());
            newObj.addProperty("level", loggingEvent.getLevel().toString());
            if (loggingEvent.getThrowableInformation() != null) {
                String[] errorInfo = loggingEvent.getThrowableInformation().getThrowableStrRep();
                newObj.addProperty("errorDetails", errorInfo[0]);
            }
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost postRequest = new HttpPost(logServiceURL);
            try {
                postRequest.addHeader("Content-Type", "application/json");
                postRequest.setEntity(new StringEntity(newObj.toString()));
                httpClient.execute(postRequest);
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
