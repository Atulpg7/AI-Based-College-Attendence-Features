package com.example.aibasedattendencesystem.Server;

public class Config {

    public static String hostUrl = "https://attendance-system-19.herokuapp.com";
    public static String authUrl = hostUrl + "/api/auth/";
    public static String addStudentUrl = hostUrl + "/api/students/";
    public static String allStudentsInCourse = hostUrl + "/api/courses/1/enrollments/";
    public static String attendenceUrl = hostUrl + "/api/courses/1/lectures/";
    public static String msgUrl = "https://api.twilio.com/2010-04-01/Accounts/ACb0ca8c3fba4941aac720da24f1fddeda/Messages.json";
}
