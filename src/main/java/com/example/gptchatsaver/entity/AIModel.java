package com.example.gptchatsaver.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ai_models")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String version;
    private String provider;
}
