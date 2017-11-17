package com.example.chase.dontpaniceducational;

public class RestRequests {
    public RestRequests() {}

    public String authenticate() {
        return "http://www.panic-button.stream/api/v1/authenticate";
    }

    public String classrooms() {
        return  "http://www.panic-button.stream/api/v1/classrooms";
    }

    public String joinClassroom() {
        return "http://www.panic-button.stream/api/v1/classrooms/join";
    }

    public String website() {
        return "http://www.panic-button.stream/";
    }

    public String register() {
        return "http://www.panic-button.stream/register";
    }
}
