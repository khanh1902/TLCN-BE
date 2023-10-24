package com.springbot.tttn.domain.payloads;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class StudentReq {
    private String studentName;
    private String studentAddress;
    private String className;
}
