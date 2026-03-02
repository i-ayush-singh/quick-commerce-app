package com.grocery.backend.dto;

import com.grocery.backend.entity.InteractionType;
import lombok.Data;

import java.util.UUID;

@Data
public class UserInteractionRequest {

    private UUID userId;
    private UUID productId;
    private InteractionType interactionType;
}

