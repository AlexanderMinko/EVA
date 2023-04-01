package com.english.eva;

import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.english.eva.ui.frame.ApplicationFrame;
import com.english.eva.ui.panel.VocabularyPanel;
import com.formdev.flatlaf.FlatLightLaf;
import org.jdesktop.swingx.JXBusyLabel;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class EnglishVocabularyAssistantApplication {

  public static void main(String[] args) {
    FlatLightLaf.setup();
    SwingUtilities.invokeLater(() -> run(args));
  }

  private static void run(String[] args) {
    var frame = new ApplicationFrame();
    var busyLabel = new JXBusyLabel();
    busyLabel.setBusy(true);
    busyLabel.setText("Loading...");
    busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
    frame.add(busyLabel, BorderLayout.CENTER);
    var swingWorker = new SwingWorker<>() {
      protected Object doInBackground() {
        initApplicationContext(args);
        frame.remove(busyLabel);
        frame.add(initTabs(), BorderLayout.CENTER);
        frame.pack();
        return null;
      }
    };
    swingWorker.execute();
  }

  private static JTabbedPane initTabs() {
    var rootTab = new JTabbedPane();
    rootTab.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    var vocabularyPanel = new VocabularyPanel();
    vocabularyPanel.initComponents();
    rootTab.addTab("Vocabulary", vocabularyPanel);
    rootTab.setSelectedIndex(0);
    return rootTab;
  }

  private static ApplicationContext initApplicationContext(String[] args) {
    return new SpringApplicationBuilder(EnglishVocabularyAssistantApplication.class)
        .headless(false)
        .web(WebApplicationType.SERVLET)
        .run(args);
  }
}
