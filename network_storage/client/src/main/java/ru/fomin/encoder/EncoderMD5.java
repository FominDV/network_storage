package ru.fomin.encoder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.xml.bind.DatatypeConverter;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EncoderMD5 implements Encoder {

    @Getter(lazy = true)
    private static final Encoder INSTANCE = new EncoderMD5();

    private final static MessageDigest messageDigest = getMessageDigest();

    @Override
    public String encode(String encodingString) {
        byte[] input = encodingString.getBytes(StandardCharsets.UTF_8);
        byte[] output = messageDigest.digest(input);
        return DatatypeConverter.printHexBinary(output);
    }

    private static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Encoder MD5 error: " + e.getCause());
        }
    }
}
