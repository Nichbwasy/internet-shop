package com.shop.seller.common.test.data.builder;

import com.shop.common.utils.all.test.data.builder.TestDataBuilder;
import com.shop.seller.dto.control.RegisterNewSellerForm;

import java.util.function.Consumer;

public class RegisterNewSellerFormBuilder extends TestDataBuilder<RegisterNewSellerForm> {

    private Long userId = random.nextLong(1, 1000);

    private RegisterNewSellerFormBuilder() {
    }
    private RegisterNewSellerFormBuilder(RegisterNewSellerFormBuilder builder) {
        this.userId = builder.userId;
    }
    public static RegisterNewSellerFormBuilder registerNewSellerForm() {
        return new RegisterNewSellerFormBuilder();
    }

    public RegisterNewSellerFormBuilder userId(Long userId) {
        return copyWith(b -> b.userId = userId);
    }

    @Override
    public RegisterNewSellerForm build() {
        RegisterNewSellerForm form = new RegisterNewSellerForm();
        form.setUserId(userId);
        return form;
    }

    private RegisterNewSellerFormBuilder copyWith(Consumer<RegisterNewSellerFormBuilder> consumer) {
        RegisterNewSellerFormBuilder registerNewSellerFormBuilder = new RegisterNewSellerFormBuilder(this);
        consumer.accept(registerNewSellerFormBuilder);
        return registerNewSellerFormBuilder;
    }

}
