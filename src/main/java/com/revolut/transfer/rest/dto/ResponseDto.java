package com.revolut.transfer.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.revolut.transfer.hibernate.Account;
import com.revolut.transfer.rest.util.Status;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto {

    private Status status;
    private String msg;
    private Account account;
    private List<Account> accounts;
}
