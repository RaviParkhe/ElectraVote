package com.electravote;

import com.electravote.admin.AdminDashboard;
import com.electravote.admin.ElectionPage;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Application.launch(AdminDashboard.class,args);
    }
}