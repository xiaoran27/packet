package com.lj.utils;


import java.awt.*;
import javax.swing.*;
import java.net.URL;
import java.net.MalformedURLException;
import org.jdesktop.jdic.browser.*;

public class JWebBrowser {
	public static void main(String[] args){
		openUrl("http://www.baidu.com");
	}
	
	public static void openUrl(String url) {
		openUrl(url,"JDIC - JWebBrowser");
	}
    public static void openUrl(String url, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        WebBrowser webBrowser = new WebBrowser();

        //Use below code to check the status of the navigation process,
        //or register a listener for the notification events.
        webBrowser.addWebBrowserListener(
            new WebBrowserListener() {
            public void downloadStarted(WebBrowserEvent event) {;}
            public void downloadCompleted(WebBrowserEvent event) {;}
            public void downloadProgress(WebBrowserEvent event) {;}
            public void downloadError(WebBrowserEvent event) {;}
            public void documentCompleted(WebBrowserEvent event) {;}
            public void titleChange(WebBrowserEvent event) {;}
            public void statusTextChange(WebBrowserEvent event) {;}

            public void windowClose(WebBrowserEvent webBrowserEvent) {
            }
        });

        try {
            webBrowser.setURL(new URL(url));
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(700, 500));
        panel.add(webBrowser, BorderLayout.CENTER);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}