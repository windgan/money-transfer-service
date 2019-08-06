package com.revolut.transfer.rest.util;

import com.revolut.transfer.hibernate.Account;
import com.revolut.transfer.rest.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
public class ResponseUtil {

    public static ResponseDto createResponse(Runnable runnable, String msg) {
        try {
            runnable.run();

            return createSuccessResponse(msg);
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    public static ResponseDto createResponseWithData(Callable<Account> callable, String msg) {
        try {
            Account account = callable.call();

            ResponseDto response = createSuccessResponse(msg);
            response.setAccount(account);

            return response;
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    public static ResponseDto createResponseWithListData(Callable<List<Account>> callable, String msg) {
        try {
            List<Account> accounts = callable.call();

            ResponseDto response = createSuccessResponse(msg);
            response.setAccounts(accounts);

            return response;
        } catch (Exception e) {
            return createErrorResponse(e);
        }
    }

    private static ResponseDto createSuccessResponse(String msg) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatus(Status.SUCCESS);
        responseDto.setMsg(msg);
        return responseDto;
    }

    private static ResponseDto createErrorResponse(Exception e) {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setStatus(Status.FAILED);
        responseDto.setMsg(e.getMessage());
        return responseDto;
    }
}
