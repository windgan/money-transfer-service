package com.revolut.transfer.rest;

import com.revolut.transfer.exception.AccountException;
import com.revolut.transfer.hibernate.Account;
import com.revolut.transfer.rest.dto.ResponseDto;
import com.revolut.transfer.rest.dto.TransferMoneyDto;
import com.revolut.transfer.rest.util.Status;
import com.revolut.transfer.service.AccountService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static org.mockito.Mockito.*;

public class RestServiceTest extends JerseyTest {

    @Mock
    private AccountService service;

    @Override
    protected Application configure() {
        service = Mockito.mock(AccountService.class);
        RestController restService = new RestController(service);

        ResourceConfig config = new ResourceConfig();
        config.register(restService);

        return config;
    }

    @Test
    public void get() {
        Account stub = new Account(1L, BigDecimal.TEN);
        when(service.get(1L)).thenReturn(stub);

        Response response = target("/accounts/1").request().get();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        ResponseDto responseDto = response.readEntity(ResponseDto.class);
        Assert.assertEquals(Status.SUCCESS, responseDto.getStatus());
        Assert.assertEquals(stub, responseDto.getAccount());
    }

    @Test
    public void getFailed() {
        when(service.get(1L)).thenThrow(AccountException.createWithAccountNotFound(1L));

        Response response = target("/accounts/1").request().get();

        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        ResponseDto responseDto = response.readEntity(ResponseDto.class);
        Assert.assertEquals(Status.FAILED, responseDto.getStatus());
        Assert.assertNull(responseDto.getAccount());
    }

    @Test
    public void add() {
        Account stub = new Account(1L, BigDecimal.TEN);
        doNothing().when(service).add(stub);

        Response response = target("/accounts").request()
                .post(Entity.entity(stub, MediaType.APPLICATION_JSON));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        ResponseDto responseDto = response.readEntity(ResponseDto.class);
        Assert.assertEquals(Status.SUCCESS, responseDto.getStatus());
    }

    @Test
    public void addFailed() {
        Account stub = new Account(1L, BigDecimal.TEN);
        doThrow(RuntimeException.class).when(service).add(stub);

        Response response = target("/accounts").request()
                .post(Entity.entity(stub, MediaType.APPLICATION_JSON));

        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        ResponseDto responseDto = response.readEntity(ResponseDto.class);
        Assert.assertEquals(Status.FAILED, responseDto.getStatus());
    }

    @Test
    public void update() {
        Account stub = new Account(1L, BigDecimal.TEN);
        doNothing().when(service).update(stub);

        Response response = target("/accounts").request()
                .put(Entity.entity(stub, MediaType.APPLICATION_JSON));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        ResponseDto responseDto = response.readEntity(ResponseDto.class);
        Assert.assertEquals(Status.SUCCESS, responseDto.getStatus());
    }

    @Test
    public void updateFailed() {
        Account stub = new Account(1L, BigDecimal.TEN);
        doThrow(RuntimeException.class).when(service).update(stub);

        Response response = target("/accounts").request()
                .put(Entity.entity(stub, MediaType.APPLICATION_JSON));

        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        ResponseDto responseDto = response.readEntity(ResponseDto.class);
        Assert.assertEquals(Status.FAILED, responseDto.getStatus());
    }

    @Test
    public void delete() {
        doNothing().when(service).delete(1L);

        Response response = target("/accounts/1").request().delete();

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        ResponseDto responseDto = response.readEntity(ResponseDto.class);
        Assert.assertEquals(Status.SUCCESS, responseDto.getStatus());
    }

    @Test
    public void deleteFailed() {
        doThrow(RuntimeException.class).when(service).delete(1L);

        Response response = target("/accounts/1").request().delete();

        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        ResponseDto responseDto = response.readEntity(ResponseDto.class);
        Assert.assertEquals(Status.FAILED, responseDto.getStatus());
    }

    @Test
    public void transfer() {
        TransferMoneyDto stub = new TransferMoneyDto();
        stub.setSenderId(1L);
        stub.setReceiverId(2L);
        stub.setAmount(BigDecimal.ONE);

        doNothing()
                .when(service)
                .transfer(stub.getSenderId(), stub.getReceiverId(), stub.getAmount());

        Response response = target("/accounts/transfer").request()
                .put(Entity.entity(stub, MediaType.APPLICATION_JSON));

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        ResponseDto responseDto = response.readEntity(ResponseDto.class);
        Assert.assertEquals(Status.SUCCESS, responseDto.getStatus());
    }

    @Test
    public void transferFailed() {
        TransferMoneyDto stub = new TransferMoneyDto();
        stub.setSenderId(1L);
        stub.setReceiverId(2L);
        stub.setAmount(BigDecimal.ONE);

        doThrow(RuntimeException.class)
                .when(service)
                .transfer(stub.getSenderId(), stub.getReceiverId(), stub.getAmount());

        Response response = target("/accounts/transfer").request()
                .put(Entity.entity(stub, MediaType.APPLICATION_JSON));

        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

        ResponseDto responseDto = response.readEntity(ResponseDto.class);
        Assert.assertEquals(Status.FAILED, responseDto.getStatus());
    }
}