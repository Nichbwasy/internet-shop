package com.shop.common.utils.all.test.data.builder;

import java.util.Random;

public abstract class TestDataBuilder<T> {
    protected final Random random = new Random();
    public abstract T build();
}
