package ru.fomin.services.encoder;


public interface Encoder {

    /**Encodes string.*/
    CodePair encode(String string);
    
    String encode(String value , String salt);

}
