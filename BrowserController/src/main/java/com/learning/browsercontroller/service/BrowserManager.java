package com.learning.browsercontroller.service;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class BrowserManager {
    public synchronized String startBrowser(String browser, String url) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "start", browser, url);
        processBuilder.inheritIO();
        processBuilder.start();
        return browser + " started with URL: " + url;
    }

    public synchronized String stopBrowser(String browser) throws IOException
    {
        ProcessBuilder pb = new ProcessBuilder("taskkill", "/F", "/IM", browser + ".exe");
        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();

        System.out.println("Stopped browser: " + browser);
        return browser + "closed";
    }

    public synchronized String getActiveUrl(String browser) throws IOException {
        String command = "powershell Get-Process -Name " + browser + " | Select-Object -First 1";

        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", command);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String activeTabURL = reader.readLine();

        reader.close();
        return activeTabURL;
    }
}
