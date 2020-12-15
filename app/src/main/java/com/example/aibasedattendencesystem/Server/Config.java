package com.example.aibasedattendencesystem.Server;

public class Config {

    public static String hostUrl = "https://attendance-system-19.herokuapp.com";
    public static String authUrl = hostUrl + "/api/auth/";
    public static String addStudentUrl = hostUrl + "/api/students/";
    public static String allStudentsInCourse = hostUrl + "/api/courses/1/enrollments/";
    public static String attendenceUrl = hostUrl + "/api/courses/1/lectures/";
    public static String limit = "?limit=5";
    public static String attendenceHistoryUrl = hostUrl + "/api/courses/1/attendance/";

}
