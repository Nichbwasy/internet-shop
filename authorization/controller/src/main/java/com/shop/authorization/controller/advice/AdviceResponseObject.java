package com.shop.authorization.controller.advice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdviceResponseObject {

    private LocalDateTime time;
    private String message;
    private String causes;
    private String contextPath;

    public AdviceResponseObject(String message, Exception e, WebRequest request) {
        this.time = LocalDateTime.now();
        this.message = message + e.getMessage();
        this.causes = e.getCause().toString();
        this.contextPath = request.getContextPath();
    }
}
