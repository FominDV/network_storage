package ru.fomin.factory;

import lombok.extern.log4j.Log4j2;
import ru.fomin.encoder.Encoder;
import ru.fomin.encoder.EncoderMD5;
import ru.fomin.network.Connection;
import ru.fomin.network.ResponseSandler;
import ru.fomin.network.impl.NetworkConnection;
import ru.fomin.services.*;
import ru.fomin.services.impl.ResponseService;
import ru.fomin.services.impl.RequestService;

@Log4j2
public class Factory {

    //NetworkConnection
    public static Connection getConnection() {
        return NetworkConnection.getInstance();
    }

    public static ResponseSandler getResponseSandler() {
        return NetworkConnection.getInstance();
    }

    //RequestService
    public static MainPanelService getMainPanelRequest() {
        return RequestService.getInstance();
    }

    public static AuthenticationService getAuthenticationRequest() {
        return RequestService.getInstance();
    }

    public static RegistrationService getRegistrationRequest() {
        return RequestService.getInstance();
    }

    public static ChangingPasswordService getChangingPasswordRequest() {
        return RequestService.getInstance();
    }

    //ResponseService
    public static ResponseProcessor getResponseProcessor() {
        return ResponseService.getInstance();
    }

    public static NetworkConnectionService getNetworkConnectionService() {
        return ResponseService.getInstance();
    }

    //Encoder
    public static Encoder getEncoder(){
        return EncoderMD5.getINSTANCE();
    }

}
