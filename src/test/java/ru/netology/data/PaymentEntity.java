package ru.netology.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class PaymentEntity {
    private String id;
    private int amount;
    private Timestamp created;
    private String status;
    private String transaction_id;
}
