package ru.fomin.services.encoder;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
/**Class for transfer encoded value and salt of it.*/
public class CodePair {

    String value;
    String salt;

}
