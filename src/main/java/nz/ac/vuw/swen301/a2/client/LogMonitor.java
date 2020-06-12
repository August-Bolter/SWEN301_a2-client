package nz.ac.vuw.swen301.a2.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/** The user interface which shows the logs, filtered by level, limit and sorted by timestamp */
public class LogMonitor extends JFrame implements ActionListener {
    private Dimension screenSize;
    private JButton submit; //The fetch data button
    private JPanel overallPanel; //Contains all other components
    private GridBagConstraints c;
    private JComboBox<String> minLevel; //The minimum level drop down menu
    private JTextField limitField; //The limit text field

    public static void main(String[] args) {
        new LogMonitor();
    }

    /** Creates the user interface */
    public LogMonitor() {
        setTitle("Log Monitor");
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        overallPanelSetup();
        createFilterSection();
        createLogTable("ALL", "20"); //Default values, so when the user loads the user interface up for the first time it will show the first 20 logs (minimum level ALL)
        add(overallPanel);
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH); //Making user interface fullscreen
        setVisible(true);
        revalidate();
        repaint();
    }

    /** Initialises the overallPanel and set its layout */
    private void overallPanelSetup() {
        overallPanel = new JPanel();
        overallPanel.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
    }

    /** Creates the table which holds all of the logs
     * @param level The minimum level of logs shown in the table
     * @param limit The maximum amount of logs shown in the table */
    private void createLogTable(String level, String limit) {
        String[] columnNames = {"ID", "Message", "Timestamp", "Thread", "Logger", "Level", "Error Details"}; //Table headers
        String dataString = null;
        try {
            dataString = fetchData(level, limit); //Get the logs
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        Object[][] data = null;
        /* Only transform fetched data if it is valid */
        if (dataString != null && dataString.length() != 0 && (dataString.startsWith("{") || dataString.startsWith("["))) {
            data = transformString(dataString);
        }
        /* Making table not editable */
        DefaultTableModel logsLayout = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable logs = new JTable(logsLayout); //Creating the table
        logs.setFont(new Font("Serif", Font.PLAIN, 15));
        /* Formatting layout */
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 2; //Should take up more space than filterPanel
        JScrollPane pane = new JScrollPane(logs); //Need a JScrollPane since table needs to be scrollable
        overallPanel.add(pane, c); //Add scrollpane to UI with formatting
    }

    /** Transform logs Json String into a 2d array of Objects (Strings) that will be used to initialise the table
     * @param dataString The logs Json String
     * @return logs as a 2d array */
    private Object[][] transformString(String dataString) {
        Gson g = new Gson();
        JsonArray logArray = null;
        JsonObject logObj = null;
        int logNum;
        /* Determining the number of objects in the Json String */
        if (dataString.startsWith("[")) {
            logArray = g.fromJson(dataString, JsonArray.class);
            logNum = logArray.size();
        }
        else {
            logObj = g.fromJson(dataString, JsonObject.class);
            logNum = 1;
        }
        Object[][] data = new Object[logNum][7]; //Table should only be as big as the number of logs
        if (logArray != null) {  //For an array
            int row = 0;
            for (JsonElement log : logArray) { //Iterate through it, get each log of the array, convert it into a String and store it in the array
                JsonObject obj = log.getAsJsonObject();
                data[row][0] = obj.get("id").toString().substring(1, obj.get("id").toString().length()-1); //substring is to account for the encapsulating ""
                data[row][1] = obj.get("message").toString().substring(1, obj.get("message").toString().length()-1);
                data[row][2] = obj.get("timestamp").toString().substring(1, obj.get("timestamp").toString().length()-1);
                data[row][3] = obj.get("thread").toString().substring(1, obj.get("thread").toString().length()-1);
                data[row][4] = obj.get("logger").toString().substring(1, obj.get("logger").toString().length()-1);
                data[row][5] = obj.get("level").toString().substring(1, obj.get("level").toString().length()-1);
                if (obj.get("errorDetails") != null) { //Since errorDetails is optional
                    data[row][6] = obj.get("errorDetails").toString().substring(1, obj.get("errorDetails").toString().length()-1);
                }
                row++;
            }
        }
        else { //If its just one log then no need for iterating, just get String from the one json object.
            data[0][0] = logObj.get("id").toString().substring(1, logObj.get("id").toString().length()-1);
            data[0][1] = logObj.get("message").toString().substring(1, logObj.get("message").toString().length()-1);
            data[0][2] = logObj.get("timestamp").toString().substring(1, logObj.get("timestamp").toString().length()-1);
            data[0][3] = logObj.get("thread").toString().substring(1, logObj.get("thread").toString().length()-1);
            data[0][4] = logObj.get("logger").toString().substring(1, logObj.get("logger").toString().length()-1);
            data[0][5] = logObj.get("level").toString().substring(1, logObj.get("level").toString().length()-1);
            if (logObj.get("errorDetails") != null) {
                data[0][6] = logObj.get("errorDetails").toString().substring(1, logObj.get("errorDetails").toString().length()-1);
            }
        }
        return data;
    }

    /** Sends a Get request to the server to get the logs based on the level and limit parameters
     * @param level The minimum level of logs
     * @param limit limit The maximum number of logs */
    private String fetchData(String level, String limit) throws URISyntaxException, IOException {
        HttpClient httpClient = HttpClientBuilder.create().build(); //Creating the client
        /* Creating the URI */
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost("localhost").setPort(8080).setPath("/resthome4logs/logs")
                .setParameter("level", level).setParameter("limit", limit);
        URI uri = builder.build();

        /* Creating the get request and sending it from the client to the server */
        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);

        return EntityUtils.toString(response.getEntity());
    }

    /** Creates the filter section which contains the level and limit filters and the submit button */
    private void createFilterSection() {
        JPanel filterPanel = new JPanel();
        Dimension panelDim = new Dimension(screenSize.width, (int) Math.round(screenSize.height*0.06));
        filterPanel.setPreferredSize(panelDim);
        filterPanel.setMinimumSize(panelDim);
        filterPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        /* Creating min level drop down menu and label */
        JLabel minLevelLabel = new JLabel("Min Level:");
        String[] levels = {"ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF"};
        minLevel = new JComboBox<>(levels);
        /* Creating limit label and text field */
        JLabel limitLabel = new JLabel("Limit:");
        limitField = new JTextField("20");
        Dimension fieldDimension = new Dimension((int) Math.round(screenSize.width*0.06), (int) Math.round(screenSize.height*0.02));
        limitField.setPreferredSize(fieldDimension);
        limitField.setMinimumSize(fieldDimension);
        /* Creating submit button */
        submit = new JButton("Fetch Data");
        submit.addActionListener(this);
        /* Adding everything to filter panel */
        filterPanel.add(minLevelLabel);
        filterPanel.add(minLevel);
        filterPanel.add(limitLabel);
        filterPanel.add(limitField);
        filterPanel.add(submit);
        /* Adding filter panel to JFrame */
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        overallPanel.add(filterPanel, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(submit)) { //If submit button was clicked
            overallPanel.remove(1); //Remove table
            String level = (String) minLevel.getSelectedItem(); //Gets the level submitted
            String limit = limitField.getText(); //Gets the limit submitted
            createLogTable(level, limit); //Create a new table based on the level and limit
            revalidate();
            repaint();
        }
    }
}
