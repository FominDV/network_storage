package ru.fomin.services.encoder;

public class EncoderFactory {

    public static Encoder getEncoder(){
        return  EncoderSHA256.getINSTANCE();
    }

}
