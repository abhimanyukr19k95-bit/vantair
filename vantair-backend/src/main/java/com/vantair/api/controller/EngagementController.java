package com.vantair.api.controller;

import com.vantair.api.dto.Dtos.ContactRequest;
import com.vantair.api.dto.Dtos.MessageResponse;
import com.vantair.api.dto.Dtos.NewsletterRequest;
import com.vantair.api.dto.Dtos.PartnerRequest;
import com.vantair.api.service.EngagementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Engagement", description = "Newsletter, contact and partner enquiries")
public class EngagementController {

    private final EngagementService engagement;

    public EngagementController(EngagementService engagement) {
        this.engagement = engagement;
    }

    @PostMapping("/newsletter")
    public MessageResponse subscribe(@Valid @RequestBody NewsletterRequest req) {
        engagement.subscribe(req.email());
        return new MessageResponse("Subscribed.");
    }

    @PostMapping("/contact")
    public ResponseEntity<MessageResponse> contact(@Valid @RequestBody ContactRequest req) {
        engagement.submitContact(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Message received."));
    }

    @PostMapping("/partner")
    public ResponseEntity<MessageResponse> partner(@Valid @RequestBody PartnerRequest req) {
        engagement.submitPartner(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Enquiry received."));
    }
}
