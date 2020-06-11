package nz.ac.vuw.swen301.a2.client;

import com.google.gson.JsonObject;
import com.sun.javafx.util.Logging;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class CreateRandomLogs {

    public static void main(String[] args) throws InterruptedException {
        //Test code
        Resthome4LogsAppender appender = new Resthome4LogsAppender();

        while (true) {
            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();

            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            String message = generatedString;

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
            long limit = Integer.MAX_VALUE;
            if (level != Level.ERROR) {
                event = new LoggingEvent("foo", Logger.getLogger(JsonObject.class), ThreadLocalRandom.current().nextLong(limit), level, message, "main", null, "", null, null);
            }
            else {
                String[] info = new String[4];
                info[1] = "AHHH There is an error";
                ThrowableInformation throwable = new ThrowableInformation(info);
                event = new LoggingEvent("foo", Logger.getLogger(JsonObject.class), ThreadLocalRandom.current().nextLong(limit), level, message, "main", throwable, "", null, null);
            }
            //Test code
            appender.append(event);
            Thread.sleep(1000);
        }
    }
}
