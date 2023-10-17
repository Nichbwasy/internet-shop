package com.shop.authorization.service.encoder;

import com.shop.authorization.service.exception.encoder.PasswordEncoderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
public class PasswordEncoder {

    private final static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encode(String string) {
        try {
            return encoder.encode(string);
        } catch (IllegalArgumentException e) {
            log.info("Unable encode password! Password can not be null!");
            throw new PasswordEncoderException("Unable encode password! Password can not be null!");
        } catch (Exception e) {
            log.info("Unable encode password! Exception while encoding! {}", e.getMessage());
            throw new PasswordEncoderException(
                    String.format("Unable encode password! Exception while encoding! %s", e.getMessage())
            );
        }
    }

    public static Boolean match(String rawString, String encodedString) {
        try {
            return encoder.matches(rawString, encodedString);
        } catch (IllegalArgumentException e) {
            log.info("Unable match passwords! Raw password can not be null!");
            throw new PasswordEncoderException("Unable encode password! Password can not be null!");
        } catch (Exception e) {
            log.info("Unable match passwords! Exception while passwords matching! {}", e.getMessage());
            throw new PasswordEncoderException(
                    String.format("Unable encode password! Exception while encoding! %s", e.getMessage())
            );
        }
    }

}
