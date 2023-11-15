package com.shop.common.utils.all.generator;

import java.util.Random;

public class StringGenerator {

    private final static Random random = new Random();
    private final static String BASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";

    public static String generate(int length) {
        return generateString(length, BASE_CHARS);
    }

    public static String generate(int length, String chars) {
        return generateString(length, chars);
    }

    private static String generateString(int length, String chars) {
        if (length < 0) throw new IllegalArgumentException("Unable generate a string! Length cant be lesser than 0!");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(chars.charAt(random.nextInt(0, chars.length())));
        }

        return builder.toString();
    }

}
