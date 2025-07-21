package com.dev.util;

import java.io.File;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener{
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        ServletContext context = se.getSession().getServletContext();
        String tmpPath = context.getRealPath("/resources/tmp");

        File tmpFolder = new File(tmpPath);
        if (tmpFolder.exists() && tmpFolder.isDirectory()) {
            File[] files = tmpFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }

        System.out.println("🧹 Session expired - cleaned up temporary folder.");
    }
}
