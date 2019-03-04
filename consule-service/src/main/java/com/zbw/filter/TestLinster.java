package com.zbw.filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class TestLinster implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("Start to initialize servlet context");
        ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.setAttribute("content", "test");
    }
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
