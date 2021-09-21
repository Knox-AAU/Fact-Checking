package com.trippple;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApp {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
        var app = ac.getBean(Application.class);
        app.main();
    }
}