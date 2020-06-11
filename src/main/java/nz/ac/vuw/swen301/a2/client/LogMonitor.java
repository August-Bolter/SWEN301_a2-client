package nz.ac.vuw.swen301.a2.client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogMonitor extends JFrame implements ActionListener {
    private Dimension screenSize;
    private JButton submit;
    JPanel overallPanel;
    GridBagConstraints c;
    private JPanel[][] panels;
    private int tablewidth = 7;
    private int tableheight = 10000;

    public static void main(String[] args) {
        LogMonitor monitor = new LogMonitor();
    }
    public LogMonitor() {
        setVisible(true);
        setTitle("Log Monitor");
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) Math.round(screenSize.width*0.80), (int) Math.round(screenSize.height*0.80));
        overallPanelSetup();
        createFilterSection();
        createLogTable();
        add(overallPanel);
        revalidate();
        repaint();
    }

    private void overallPanelSetup() {
        overallPanel = new JPanel();
        overallPanel.setLayout(new GridBagLayout());
        c = new GridBagConstraints();
    }

    private void createLogTable() {
        String[] columnNames = {"ID", "Message", "Timestamp", "Thread", "Logger", "Level", "Error Details"};
        Object[][] data = {
                {"test", "test", "test", "test", "test", "test", "test"}
        };
        DefaultTableModel logsLayout = new DefaultTableModel(data, columnNames);
        JTable logs = new JTable(logsLayout);
        Dimension tableDim = new Dimension((int) Math.round(screenSize.width*0.79), (int) Math.round(screenSize.height*0.74));
        logs.setPreferredSize(tableDim);
        logs.setMaximumSize(tableDim);
        logs.setMinimumSize(tableDim);
        panels = new JPanel[tableheight][tablewidth]; //Creating table structure
        for (int row = 0; row < 7; row++) { //Adding panels to table
            for (int col = 0; col < 7; col++) {
                JPanel p = new JPanel();
                panels[row][col] = p;
            }
        }
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 2;
        JScrollPane pane = new JScrollPane(logs);
        pane.setPreferredSize(tableDim);
        pane.setMaximumSize(tableDim);
        pane.setMinimumSize(tableDim);
        overallPanel.add(pane, c);
    }

    private void createFilterSection() {
        JPanel filterPanel = new JPanel();
        Dimension panelDim = new Dimension((int) Math.round(screenSize.width), (int) Math.round(screenSize.height*0.06));
        filterPanel.setPreferredSize(panelDim);
        filterPanel.setMaximumSize(panelDim);
        filterPanel.setMinimumSize(panelDim);
        filterPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        /* Creating min level drop down menu and label */
        JLabel minLevelLabel = new JLabel("Min Level:");
        String[] levels = {"OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL"};
        JComboBox minLevel = new JComboBox(levels);
        /* Creating limit label and text field */
        JLabel limitLabel = new JLabel("Limit:");
        JTextField limitField = new JTextField();
        Dimension fieldDimension = new Dimension((int) Math.round(screenSize.width*0.06), (int) Math.round(screenSize.height*0.02));
        limitField.setPreferredSize(fieldDimension);
        limitField.setMaximumSize(fieldDimension);
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
            //HTTP GET
        }
    }
}
