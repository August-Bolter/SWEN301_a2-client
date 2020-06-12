package nz.ac.vuw.swen301.a2.client;

import com.google.gson.JsonObject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/** Creates random logs once per second. ALl aspects of the log are random (or pseudo random) */
public class CreateRandomLogs {

    public static void main(String[] args) throws InterruptedException {
        Resthome4LogsAppender appender = new Resthome4LogsAppender(); //Creating the appender

        /* Possible options for the log aspects. One option for each aspect will be chosen randomly */
        String[] loggerNames = {"foo", "bar", "baz", "rez", "pez"};
        String[] messageNames = {"This is a log", "This log contains info", "Something has happened", "This log is useful"};
        String[] threadNames = {"main", "daemon", "concurrent"};
        String[] throwableInfoNames = {"An exception has occurred", "Everything is working well", "Something has gone wrong", "An issue has occurred"};

        /* Make logs continuously (forever) */
        while (true) {
            Random rand = new Random();
            /* Assigning a random level */
            int randomNum = rand.nextInt(8);
            Level level = null;
            switch(randomNum) {
                case 0 :
                    level = Level.OFF;
                    break;
                case 1 :
                    level = Level.FATAL;
                    break;
                case 2 :
                    level = Level.ERROR;
                    break;
                case 3 :
                    level = Level.WARN;
                    break;
                case 4 :
                    level = Level.INFO;
                    break;
                case 5 :
                    level = Level.DEBUG;
                    break;
                case 6 :
                    level = Level.TRACE;
                    break;
                case 7 :
                    level = Level.ALL;
            }
            LoggingEvent event; //The log that will be sent to the appender
            long bound = 1591963200L*1000L; //This is the maximum value for timestamp that can be randomly generated. This value is the difference in miliseconds between 1970-01-01 and the 12th June 2020
            int errorDetailsPresent = rand.nextInt(2); //Decides whether log will have errorDetails or not
            if (errorDetailsPresent == 0) { //Log will have errorDetails
                /* Pick errorDetails message randomly */
                String[] info = new String[1];
                info[0] = throwableInfoNames[rand.nextInt(4)];
                ThrowableInformation throwable = new ThrowableInformation(info); //Creating ThrowableInformation which contains this message
                Logger logger = Logger.getLogger(loggerNames[rand.nextInt(5)]); //Creating a logger with a random logger name
                event = new LoggingEvent(logger.getName(), logger, ThreadLocalRandom.current().nextLong(bound), level, messageNames[rand.nextInt(4)], threadNames[rand.nextInt(3)], throwable, null, null, null); //Creating the log
            }
            else { //Log will not have errorDetails
                event = new LoggingEvent("foo", Logger.getLogger(JsonObject.class), ThreadLocalRandom.current().nextLong(bound), level, messageNames[rand.nextInt(4)], threadNames[rand.nextInt(3)], null, null, null, null); //Creating the log
            }
            appender.append(event); //Send logging event to appender
            Thread.sleep(1000); //Wait 1 second
        }
    }
}
