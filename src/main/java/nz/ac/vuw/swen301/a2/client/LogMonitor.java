package nz.ac.vuw.swen301.a2.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogMonitor extends JFrame implements ActionListener {
    private Dimension screenSize;
    private JButton submit;
    private JPanel[][] panels;

    public static void main(String[] args) {
        LogMonitor monitor = new LogMonitor();
    }
    public LogMonitor() {
        setVisible(true);
        setTitle("Log Monitor");
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int) Math.round(screenSize.width*0.80), (int) Math.round(screenSize.height*0.80));
        createFilterSection();
        createLogTable();
        revalidate();
        repaint();
    }

    private void createLogTable() {
        JPanel logsPanel = new JPanel();
        logsPanel.setLayout(new GridLayout(7, 7));
        panels = new JPanel[7][100000000]; //Creating table structure
        for (int row = 0; row < 7; row++) { //Adding panels to table
            for (int col = 0; col < 7; col++) {
                panels[col][row] = new JPanel();
            }
        }
    }

    private void createFilterSection() {
        JPanel filterPanel = new JPanel();
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
        add(filterPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(submit)) {
            //HTTP GET
        }
    }
}
