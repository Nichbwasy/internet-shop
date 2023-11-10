package com.shop.common.utils.all.client;

import com.shop.common.utils.all.exception.client.CommonMicroserviceClientException;
import com.shop.common.utils.all.exception.client.FailedRequestMicroserviceClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
public abstract class MicroserviceCommonClient {

        protected final RestTemplate restTemplate = new RestTemplate();
        protected String MICROSERVICE_NAME;
        protected String MICROSERVICE_URL;

        public <T> T sendRequest(String path, HttpMethod method, Class<T> clazz, Map<String, String> headers, Object body) {
                String url = MICROSERVICE_URL + path;
                log.info("Microservice '{}' sending request to the '{}'...", MICROSERVICE_NAME, url);
                try {
                        HttpEntity<?> httpEntity = buildHttpEntity(headers, body);

                        ResponseEntity<T> response = restTemplate.exchange(url, method, httpEntity, clazz);
                        T responseBody = response.getBody();

                        if (responseBody == null) {
                                log.error("Failed request to '{}' from '{}'! Response body is null!", url, MICROSERVICE_NAME);
                                throw new FailedRequestMicroserviceClientException(
                                        String.format("Failed request to '%s' from '%s'! Response body is null!",
                                                url, MICROSERVICE_NAME)
                                );
                        }

                        log.info("Successful request from '{}' to to the '{}'.", MICROSERVICE_NAME, url);
                        return responseBody;
                } catch (Exception e) {
                        log.error("Exception while sending request to '{}' from '{}'! {}", url, MICROSERVICE_NAME, e.getMessage());
                        throw new CommonMicroserviceClientException(
                                String.format("Exception while sending request to '%s' from '%s'! %s",
                                        url, MICROSERVICE_NAME, e.getMessage())
                        );
                }
        }

        private HttpEntity<?> buildHttpEntity(Map<String, String> headers, Object body) {
                HttpHeaders httpHeaders = new HttpHeaders();

                if (headers != null) headers.forEach(httpHeaders::add);

                return new HttpEntity<>(body, httpHeaders);
        }

}
