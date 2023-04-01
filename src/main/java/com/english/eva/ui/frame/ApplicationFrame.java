package com.english.eva.ui.frame;

import java.awt.BorderLayout;
import javax.swing.JFrame;

public class ApplicationFrame extends JFrame {

  public ApplicationFrame() {
    super("English Vocabulary Assistant");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(600, 600);
    setLocationRelativeTo(null);
    setVisible(true);
    setLayout(new BorderLayout());
  }

}

