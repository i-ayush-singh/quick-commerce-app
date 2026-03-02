package com.grocery.backend.service;

import com.grocery.backend.dto.UserInteractionRequest;

public interface UserInteractionService {
 void trackInteraction(UserInteractionRequest request);
}
