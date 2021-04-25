package ru.fomin.util;


import ru.fomin.domain.CodePair;

public interface Encoder {

    /**Encodes string.*/
    CodePair encode(String string);
    
    String encode(String value , String salt);

}
