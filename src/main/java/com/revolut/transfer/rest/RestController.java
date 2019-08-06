package com.revolut.transfer.rest;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.revolut.transfer.environment.DependencyInjectionConfig;
import com.revolut.transfer.hibernate.Account;
import com.revolut.transfer.rest.dto.ResponseDto;
import com.revolut.transfer.rest.dto.TransferMoneyDto;
import com.revolut.transfer.rest.util.ResponseUtil;
import com.revolut.transfer.rest.util.Status;
import com.revolut.transfer.service.AccountService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/accounts")
@Produces({MediaType.APPLICATION_JSON})
@Slf4j
@Api("/accounts")
public class RestController {

    private final AccountService service;

    public RestController() {
        this.service = Guice.createInjector(new DependencyInjectionConfig()).getInstance(AccountService.class);
    }

    @Inject
    public RestController(AccountService service) {
        this.service = service;
    }

    @GET
    @Path("/{accountId}")
    @ApiOperation("Get account by id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Account was found"),
            @ApiResponse(code = 500, message = "Error while performing operation")
    })
    public Response get(@ApiParam("transfer id") @PathParam("accountId") long accountId) {
        log.info("processing get for id={}", accountId);
        ResponseDto responseDto = ResponseUtil.createResponseWithData(() -> service.get(accountId), "Account found");
        return createResponse(responseDto);
    }

    @GET
    @ApiOperation("Get all accounts")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Accounts were found"),
            @ApiResponse(code = 500, message = "Error while performing operation")
    })
    public Response getAll() {
        log.info("processing get for all accounts");
        ResponseDto responseDto = ResponseUtil.createResponseWithListData(service::getAll, "All found accounts");
        return createResponse(responseDto);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("Create account")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Account was created"),
            @ApiResponse(code = 500, message = "Error while performing operation")
    })
    public Response add(@ApiParam("Account with id and balance") Account account) {
        log.info("processing add for transfer={}", account);
        ResponseDto responseDto = ResponseUtil.createResponse(() -> service.add(account), "Account added");
        return createResponse(responseDto);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("Update account")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Account was updated"),
            @ApiResponse(code = 500, message = "Error while performing operation")
    })
    public Response update(@ApiParam("Account with id and balance") Account account) {
        log.info("processing update for transfer={}", account);
        ResponseDto responseDto = ResponseUtil.createResponse(() -> service.update(account), "Account updated");
        return createResponse(responseDto);
    }

    @DELETE
    @Path("/{accountId}")
    @ApiOperation("Delete account")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Account was deleted"),
            @ApiResponse(code = 500, message = "Error while performing operation")
    })
    public Response delete(@ApiParam("transfer id") @PathParam("accountId") long accountId) {
        log.info("processing delete for id={}", accountId);
        ResponseDto responseDto = ResponseUtil.createResponse(() -> service.delete(accountId), "Account deleted");
        return createResponse(responseDto);
    }

    @PUT
    @Path("/transfer")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation("Transfer money between accounts")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Money was successfully transferred"),
            @ApiResponse(code = 500, message = "Error while performing operation")
    })
    public Response transfer(@ApiParam("TransferMoneyDto with senderId, receiverId and amount to be transferred") TransferMoneyDto transferMoneyDto) {
        log.info("processing transfer for {}", transferMoneyDto);
        ResponseDto responseDto = ResponseUtil.createResponse(
                () -> service.transfer(transferMoneyDto.getSenderId(), transferMoneyDto.getReceiverId(), transferMoneyDto.getAmount()),
                "Transfer executed"
        );
        return createResponse(responseDto);
    }

    private Response createResponse(ResponseDto responseDto) {
        Response response;
        if (responseDto.getStatus() == Status.SUCCESS) {
            response = Response.ok(responseDto).build();
        } else {
            response = Response.serverError().entity(responseDto).build();
        }
        log.info("Response status: {}, response: {}", response.getStatus(), response.getEntity());
        return response;
    }
}
