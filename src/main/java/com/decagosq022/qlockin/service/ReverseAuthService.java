package com.decagosq022.qlockin.service;

import com.decagosq022.qlockin.payload.response.AuthRegisterResponse;
import com.decagosq022.qlockin.payload.response.AuthVerifyResponseDTO;

public interface ReverseAuthService {

    AuthRegisterResponse registerAuthUser(String userName);

    boolean finishRegisterAuthUser(String username, String credential);

    AuthVerifyResponseDTO startLogin(String userName);

    boolean finishLogin(String userName, String credential);


}
