package com.example.ordinaMii.Services;

import com.example.ordinaMii.Repository.AssistanceRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class AssistanceRequestService {
    private final AssistanceRequestRepository assistanceRequestRepository;

    public AssistanceRequestService(AssistanceRequestRepository assistanceRequestRepository) {
        this.assistanceRequestRepository = assistanceRequestRepository;
    }
}
