package ru.fomin.util.encoder;


import ru.fomin.domain.CodePair;

public interface Encoder {

    /**Encodes string.*/
    CodePair encode(String string);
    
    String encode(String value , String salt);

}
