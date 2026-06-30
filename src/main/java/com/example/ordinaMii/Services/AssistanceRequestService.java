package com.example.ordinaMii.Services;

import com.example.ordinaMii.Repository.AssistanceRequestRepository;
import org.springframework.stereotype.Service;

@Service
public class AssistanceRequestService {
    private final AssistanceRequestRepository assistanceRequestRepository;

    public AssistanceRequestService(AssistanceRequestRepository assistanceRequestRepository) {
        this.assistanceRequestRepository = assistanceRequestRepository;
    }
}
