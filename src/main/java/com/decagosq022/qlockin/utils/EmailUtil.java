package com.decagosq022.qlockin.utils;

public class EmailUtil {

    public static String getVerificationUrl( String token){
        return  "http://localhost:8080/api/auth/confirm?token=" + token ;
    }

    public static String getLoginUrl() {
        return "http://127.0.0.1:8080/swagger-ui/index.html#/auth-controller/loginUser";
    }
}
