package com.decagosq022.qlockin.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {



    @Value("${app.url.verification}")
    private  String verificationUrl;

    @Value("${app.url.login}")
    private  String loginUrl;

    @Value("${app.url.reset-password}")
    private  String resetUrl;

    public  String getVerificationUrl( String token){
        return verificationUrl + token;
    }



    public  String getLoginUrl() {
        return loginUrl;
    }


    public  String getResetUrl(String token){
        return resetUrl + token;
    }
}
