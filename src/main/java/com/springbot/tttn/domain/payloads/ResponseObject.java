package com.springbot.tttn.domain.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseObject {
    private HttpStatusCode statusCode;
    private Result result;
}

