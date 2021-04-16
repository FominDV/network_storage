package ru.fomin.services.encoder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Class for encoding by SHA256.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EncoderSHA256 implements Encoder {

    @Getter
    private final static EncoderSHA256 INSTANCE = new EncoderSHA256();

    private final MessageDigest encoder = getDigest();

    @Override
    public CodePair encode(String string) {

        MessageDigest cloneEncoder = getCloneDigest();

        byte[] input = string.getBytes(StandardCharsets.UTF_8);

        //salt for encoding
        byte[] salt = new byte[16];
        getSecureRandom().nextBytes(salt);
        cloneEncoder.update(salt);

        byte[] digest = encoder.digest(input);
        return CodePair.builder()
                .salt(DatatypeConverter.printHexBinary(salt))
                .value(DatatypeConverter.printHexBinary(digest))
                .build();

    }

    @Override
    public String encode(String value, String salt) {

        byte[] input = value.getBytes(StandardCharsets.UTF_8);
        byte[] saltBytes = salt.getBytes();

        MessageDigest cloneEncoder = getCloneDigest();
        cloneEncoder.update(saltBytes);

        byte[] digest = encoder.digest(input);
        return DatatypeConverter.printHexBinary(digest);
    }

    private MessageDigest getCloneDigest(){
        try {
            return (MessageDigest) encoder.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Encoder cloning error: " + e.getCause());
        }
    }

    private MessageDigest getDigest() {
        try {
            return  MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Encoder initialization error: " + e.getCause());
        }
    }

    private SecureRandom getSecureRandom() {
        try {
            return SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SecureRandom initialization error: " + e.getCause());
        }
    }

}
