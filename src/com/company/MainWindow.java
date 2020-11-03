package com.company;

import javax.swing.*;

public class MainWindow extends JFrame {
    public MainWindow() {
        JPanel mp = new DrawPanel();
        this.add(mp);
    }
    /*private DrawPanel dp;

    public MainWindow() throws HeadlessException {
        dp = new DrawPanel();
        this.add(dp);
    }*/
}
