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

public class LogMonitor extends JFrame implements ActionListener {
    private Dimension screenSize;
    private JButton submit;
    private JPanel overallPanel;
    private GridBagConstraints c;
    private JComboBox<String> minLevel;
    private JTextField limitField;

    public static void main(String[] args) {
        new LogMonitor();
    }
    public LogMonitor() {
        setTitle("Log Monitor");
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        overallPanelSetup();
        createFilterSection();
        createLogTable("ALL", "20");
        add(overallPanel);
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
        revalidate();
        repaint();
    }

    private void overallPanelSetup() {
        overallPanel = new JPanel();
        overallPanel.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
    }

    private void createLogTable(String level, String limit) {
        String[] columnNames = {"ID", "Message", "Timestamp", "Thread", "Logger", "Level", "Error Details"};
        String dataString = null;
        try {
            dataString = fetchData(level, limit);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        Object[][] data = null;
        if (dataString != null && dataString.length() != 0 && (dataString.startsWith("{") || dataString.startsWith("["))) {
            data = transformString(dataString);
        }
        DefaultTableModel logsLayout = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable logs = new JTable(logsLayout);
        logs.setFont(new Font("Serif", Font.PLAIN, 15));
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 2;
        JScrollPane pane = new JScrollPane(logs);
        overallPanel.add(pane, c);
    }

    private Object[][] transformString(String dataString) {
        Gson g = new Gson();
        JsonArray logArray = null;
        JsonObject logObj = null;
        int logNum;
        if (dataString.startsWith("[")) {
            logArray = g.fromJson(dataString, JsonArray.class);
            logNum = logArray.size();
        }
        else {
            logObj = g.fromJson(dataString, JsonObject.class);
            logNum = 1;
        }
        Object[][] data = new Object[logNum][7];
        if (logArray != null) {
            int row = 0;
            for (JsonElement log : logArray) {
                JsonObject obj = log.getAsJsonObject();
                data[row][0] = obj.get("id").toString().substring(1, obj.get("id").toString().length()-1);
                data[row][1] = obj.get("message").toString().substring(1, obj.get("message").toString().length()-1);
                data[row][2] = obj.get("timestamp").toString().substring(1, obj.get("timestamp").toString().length()-1);
                data[row][3] = obj.get("thread").toString().substring(1, obj.get("thread").toString().length()-1);
                data[row][4] = obj.get("logger").toString().substring(1, obj.get("logger").toString().length()-1);
                data[row][5] = obj.get("level").toString().substring(1, obj.get("level").toString().length()-1);
                if (obj.get("errorDetails") != null) {
                    data[row][6] = obj.get("errorDetails").toString().substring(1, obj.get("errorDetails").toString().length()-1);
                }
                row++;
            }
        }
        else {
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

    private String fetchData(String level, String limit) throws URISyntaxException, IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost("localhost").setPort(8080).setPath("/resthome4logs/logs")
                .setParameter("level", level).setParameter("limit", limit);
        URI uri = builder.build();

        HttpGet request = new HttpGet(uri);
        HttpResponse response = httpClient.execute(request);

        // this string is the unparsed web page (=html source code)
        return EntityUtils.toString(response.getEntity());
    }

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
        if (e.getSource().equals(submit)) {
            overallPanel.remove(1);
            String level = (String) minLevel.getSelectedItem();
            String limit = limitField.getText();
            createLogTable(level, limit);
            revalidate();
            repaint();
        }
    }
}
