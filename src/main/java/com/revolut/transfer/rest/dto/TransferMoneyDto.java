package com.revolut.transfer.rest.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferMoneyDto {

    private long senderId;
    private long receiverId;
    private BigDecimal amount;
}
