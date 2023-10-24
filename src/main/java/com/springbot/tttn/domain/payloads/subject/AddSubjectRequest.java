package com.springbot.tttn.domain.payloads.subject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddSubjectRequest {
    List<Long> classId;
    List<String> studentId;
}
