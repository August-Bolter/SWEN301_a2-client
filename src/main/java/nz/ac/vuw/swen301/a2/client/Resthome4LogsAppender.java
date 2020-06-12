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

/** This is a log4j appender that appends the log it receives to an Http Server. */
public class Resthome4LogsAppender extends AppenderSkeleton {

    /** Gets the Http Service URL
     * @return The Http Service URL */
    public URI getLogServiceURL() {
        return logServiceURL;
    }

    /** Sets the Http Service URL
     * @param logServiceURL The HTTP Service URL */
    public void setLogServiceURL(URI logServiceURL) {
        this.logServiceURL = logServiceURL;
    }

    private URI logServiceURL; //The Http Service URL used for connecting the client to the server

    /** Creates a Resthome4LogsAppender and it's logServiceURl */
    public Resthome4LogsAppender() {
        PropertyConfigurator.configure("src/main/resources/log4j.properties"); //Configure appender to these properties
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

    /** Appends a log to the Http Server
     * @param loggingEvent The log being appended */
    protected void append(LoggingEvent loggingEvent) {
        if (loggingEvent != null) { //Avoiding NullPointerException
            JsonObject newObj = new JsonObject(); //Creating JSON object
            /* Assigning attributes obtained from log like logger, level etc. to JSON object */
            newObj.addProperty("id", UUID.randomUUID().toString());
            newObj.addProperty("message", loggingEvent.getRenderedMessage());
            Instant time = Instant.ofEpochMilli(loggingEvent.timeStamp); //Calculates date based on 1970-01-01 + milliseconds (loggingEvent.timeStamp)
            newObj.addProperty("timestamp", time.toString());
            newObj.addProperty("thread", loggingEvent.getThreadName());
            newObj.addProperty("logger", loggingEvent.getFQNOfLoggerClass());
            newObj.addProperty("level", loggingEvent.getLevel().toString());
            /* Assigning Throwable Information attribute obtained from log to JSON object */
            if (loggingEvent.getThrowableInformation() != null) {
                String[] errorInfo = loggingEvent.getThrowableInformation().getThrowableStrRep();
                newObj.addProperty("errorDetails", errorInfo[0]);
            }
            /* Create the client and the post request */
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost postRequest = new HttpPost(logServiceURL);
            try {
                postRequest.addHeader("Content-Type", "application/json"); //Adding content type header
                postRequest.setEntity(new StringEntity(newObj.toString())); //Setting entity (body content) for the request
                httpClient.execute(postRequest); //Send the request to the server using the httpClient
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /** Closes the appender so that for any other process trying to append using this appender, appending will not work */
    public void close() {
    }

    /** Determines whether this appender needs a layout
     * @return whether the appender needs a layout */
    public boolean requiresLayout() {
        return false;
    }
}
