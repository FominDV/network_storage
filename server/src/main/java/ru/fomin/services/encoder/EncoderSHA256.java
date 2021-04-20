package ru.fomin.services.encoder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Class for encoding by SHA256.
 */
@Log4j2
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

        byte[] digest = cloneEncoder.digest(input);
        log.info("String was encoded and salt was created");
        return CodePair.builder()
                .salt(DatatypeConverter.printHexBinary(salt))
                .value(DatatypeConverter.printHexBinary(digest))
                .build();

    }

    @Override
    public String encode(String value, String salt) {

        byte[] input = value.getBytes(StandardCharsets.UTF_8);
        byte[] saltBytes = hexStringToByteArray(salt);

        MessageDigest cloneEncoder = getCloneDigest();
        cloneEncoder.update(saltBytes);

        byte[] digest = cloneEncoder.digest(input);
        log.info("String was encoded by gotten salt");
        return DatatypeConverter.printHexBinary(digest);
    }

    /**
     * Converts string of hex digit to byte array.
     */
    private byte[] hexStringToByteArray(String hexString) {
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            int index = i * 2;
            int tempValue = Integer.parseInt(hexString.substring(index, index + 2), 16);
            bytes[i] = (byte) tempValue;
        }
        return bytes;
    }

    private MessageDigest getCloneDigest() {
        try {
            return (MessageDigest) encoder.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Encoder cloning error: " + e.getCause());
        }
    }

    private MessageDigest getDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
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
