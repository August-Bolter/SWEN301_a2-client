package nz.ac.vuw.swen301.a2.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
            String date = formatDate(loggingEvent.timeStamp);
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
                postRequest.addHeader("Content-Type", "application/json");
                System.out.println("Posted:");
                System.out.println(newObj.toString());
                postRequest.setEntity(new StringEntity(newObj.toString()));
                HttpResponse resp = httpClient.execute(postRequest);

                URIBuilder builder = new URIBuilder();
                builder.setScheme("http").setHost("localhost").setPort(8080).setPath("/resthome4logs/logs")
                        .setParameter("level", "ALL").setParameter("limit", "4");
                URI uri = builder.build();

                HttpGet request = new HttpGet(uri);
                HttpResponse response = httpClient.execute(request);

                // this string is the unparsed web page (=html source code)
                String content = EntityUtils.toString(response.getEntity());
                System.out.println("Content:");
                System.out.println(content);
                System.out.println(response.getStatusLine().getStatusCode());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

    }

    private String formatDate(long timeStamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = new Date(timeStamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        StringBuilder output = new StringBuilder();
        output.append(calendar.get(Calendar.YEAR));
        output.append("-");
        if (calendar.get(Calendar.MONTH) >= 10) {
            output.append(calendar.get(Calendar.MONTH) + 1);
        }
        else {
            output.append("0").append(calendar.get(Calendar.MONTH) + 1);
        }
        output.append("-");
        if (calendar.get(Calendar.DAY_OF_MONTH) >= 10) {
            output.append(calendar.get(Calendar.DAY_OF_MONTH));
        }
        else {
            output.append("0").append(calendar.get(Calendar.DAY_OF_MONTH));
        }
        output.append("T");
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 10) {
            output.append(calendar.get(Calendar.HOUR_OF_DAY));
        }
        else {
            output.append("0").append(calendar.get(Calendar.HOUR_OF_DAY));
        }
        output.append(":");
        if (calendar.get(Calendar.MINUTE) >= 10) {
            output.append(calendar.get(Calendar.MINUTE));
        }
        else {
            output.append("0").append(calendar.get(Calendar.MINUTE));
        }
        output.append(":");
        if (calendar.get(Calendar.SECOND) >= 10) {
            output.append(calendar.get(Calendar.SECOND));
        }
        else {
            output.append("0").append(calendar.get(Calendar.SECOND));
        }
        output.append(".");
        if (calendar.get(Calendar.MILLISECOND) >= 100) {
            output.append(calendar.get(Calendar.MILLISECOND));
        }
        else {
            output.append("0").append(calendar.get(Calendar.MILLISECOND));
        }
        output.append("Z");
        return output.toString();
    }

    public void close() {

    }

    public boolean requiresLayout() {
        return false;
    }
}
