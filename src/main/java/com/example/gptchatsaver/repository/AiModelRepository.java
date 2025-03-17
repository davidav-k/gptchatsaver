package com.example.gptchatsaver.repository;

import com.example.gptchatsaver.entity.AIModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiModelRepository extends JpaRepository<AIModel, UUID> {
}
