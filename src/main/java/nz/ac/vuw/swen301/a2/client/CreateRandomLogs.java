package nz.ac.vuw.swen301.a2.client;

import com.google.gson.JsonObject;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CreateRandomLogs {

    public static void main(String[] args) throws InterruptedException {
        Resthome4LogsAppender appender = new Resthome4LogsAppender();

        String[] loggerNames = {"foo", "bar", "baz", "rez", "pez"};
        String[] messageNames = {"This is a log", "This log contains info", "Something has happened", "This log is useful"};
        String[] threadNames = {"main", "daemon", "concurrent"};
        String[] throwableInfoNames = {"An exception has occurred", "Everything is working well", "Something has gone wrong", "An issue has occurred"};

        while (true) {
            Random rand = new Random();
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
            LoggingEvent event;
            long bound = 1591963200L*1000L;
            int errorDetailsPresent = rand.nextInt(2);
            if (errorDetailsPresent == 0) {
                String[] info = new String[1];
                info[0] = throwableInfoNames[rand.nextInt(4)];
                ThrowableInformation throwable = new ThrowableInformation(info);
                Logger logger = Logger.getLogger(loggerNames[rand.nextInt(5)]);
                event = new LoggingEvent(logger.getName(), logger, ThreadLocalRandom.current().nextLong(bound), level, messageNames[rand.nextInt(4)], threadNames[rand.nextInt(3)], throwable, null, null, null);
            }
            else {
                event = new LoggingEvent("foo", Logger.getLogger(JsonObject.class), ThreadLocalRandom.current().nextLong(bound), level, messageNames[rand.nextInt(4)], threadNames[rand.nextInt(3)], null, null, null, null);
            }
            appender.append(event);
            Thread.sleep(1000);
        }
    }
}
