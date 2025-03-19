package com.example.gptchatsaver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIModelDTO {
    private Long id;
    private String name;
    private String version;
    private String provider;
}

