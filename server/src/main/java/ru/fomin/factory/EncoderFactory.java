package ru.fomin.factory;

import ru.fomin.util.encoder.Encoder;
import ru.fomin.util.encoder.impl.EncoderSHA256;

public class EncoderFactory {

    public static Encoder getEncoder(){
        return  EncoderSHA256.getINSTANCE();
    }

}
