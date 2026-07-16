package com.example.ordinaMii.Services;

import com.example.ordinaMii.Exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Service
public class FakePaymentService {

    public FakePaymentReceipt pay(UUID orderId, BigDecimal amount) {

        if (amount == null || amount.signum() <= 0) {
            throw new BadRequestException(
                    "Non è possibile pagare un ordine con importo non valido"
            );
        }

        FakePaymentReceipt receipt = new FakePaymentReceipt(
                UUID.randomUUID(),
                orderId,
                amount,
                LocalDateTime.now()
        );

        log.info(
                "Pagamento simulato completato. orderId={}, transactionId={}, amount={}",
                orderId,
                receipt.transactionId(),
                amount
        );

        return receipt;
    }

    public record FakePaymentReceipt(
            UUID transactionId,
            UUID orderId,
            BigDecimal amount,
            LocalDateTime paidAt
    ) {
    }
}
