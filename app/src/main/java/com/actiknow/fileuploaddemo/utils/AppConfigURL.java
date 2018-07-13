package com.actiknow.fileuploaddemo.utils;

public class AppConfigURL {
    public static String version = "v1.0";
    
//    private static String BASE_URL = "http://54.244.59.51/api/";
    private static String BASE_URL = "https://project-windcom-cammy92.c9users.io/api/" + version + "/";
    
    //public static String GET_SURVEY_DETAIL = "https://project-windcom-cammy92.c9users.io/api/v1.0/survey3";
    public static String GET_SURVEY_DETAIL = BASE_URL + "Survey";
    public static String LOGIN = BASE_URL + "User/Login";
    public static String FORGOT_PASSWORD = BASE_URL + "User/ForgetPassword";
    public static String CHANGE_PASSWORD = BASE_URL + "User/ChangePassword";
    
    
    public static String GET_JOBS = BASE_URL + "jobs";

    
    
    public static String UPLOAD_URL = BASE_URL + "upload";
    
    
}

