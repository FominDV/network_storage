package ru.fomin.factory;

import ru.fomin.util.encoder.Codable;
import ru.fomin.util.encoder.impl.EncoderSHA256;

public class EncoderFactory {

    public static Codable getEncoder(){
        return  EncoderSHA256.getInstance();
    }

}
