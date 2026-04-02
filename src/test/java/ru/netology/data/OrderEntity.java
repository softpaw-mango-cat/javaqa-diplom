package ru.netology.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class OrderEntity {
    private String id;
    private Timestamp created;
    private String credit_id;
    private String payment_id;
}
