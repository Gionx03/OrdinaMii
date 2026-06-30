package com.example.ordinaMii.Entity.Enum;

public enum PaymentStatus {
    NOT_PAID,        // ordine non ancora pagato
    PENDING,         // pagamento in attesa
    PAID,            // pagamento effettuato
    PAY_AT_COUNTER,  // pagamento richiesto alla cassa o al termine del servizio
    CANCELLED        // pagamento annullato
}
