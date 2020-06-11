package nz.ac.vuw.swen301.a2.client;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class Resthome4LogsAppender extends AppenderSkeleton {

    public String getLogServiceURL() {
        return logServiceURL;
    }

    public void setLogServiceURL(String logServiceURL) {
        this.logServiceURL = logServiceURL;
    }

    private String logServiceURL;

    public Resthome4LogsAppender() {
        setLogServiceURL("http://localhost:8080/resthome4logs/logs");
    }

    protected void append(LoggingEvent loggingEvent) {

    }

    public void close() {

    }

    public boolean requiresLayout() {
        return false;
    }
}
