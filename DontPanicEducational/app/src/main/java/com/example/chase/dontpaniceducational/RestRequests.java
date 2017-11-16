package com.example.chase.dontpaniceducational;

public class RestRequests {
    public RestRequests() {}

    public String authenticate() {
        return "https://dyla.ngrok.io/api/v1/authenticate";
    }

    public String classrooms() {
        return  "https://dyla.ngrok.io/api/v1/classrooms";
    }

    public String joinClassroom() {
        return "https://dyla.ngrok.io/api/v1/classrooms/join";
    }

    public String website() {
        return "https://dyla.ngrok.io/";
    }

    public String register() {
        return "https://dyla.ngrok.io/register";
    }
}
