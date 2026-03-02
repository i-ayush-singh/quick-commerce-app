package com.grocery.backend.controller;

import com.grocery.backend.dto.UserInteractionRequest;
import com.grocery.backend.service.UserInteractionService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Data
@RestController
@RequestMapping("/api/interaction")
public class UserInteractionController {
    private final UserInteractionService userInteractionService;

    @PostMapping
    public ResponseEntity<?> trackInteraction(@RequestBody UserInteractionRequest request){
        try{
            log.info("Tracking user interaction");
            userInteractionService.trackInteraction(request);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
