package ru.netology.data;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class CreditEntity {
    private String id;
    private String bank_id;
    private Timestamp created;
    private String status;
}
