package ru.fomin.factory;

import ru.fomin.util.Encoder;
import ru.fomin.services.encoder.EncoderSHA256;

public class EncoderFactory {

    public static Encoder getEncoder(){
        return  EncoderSHA256.getINSTANCE();
    }

}