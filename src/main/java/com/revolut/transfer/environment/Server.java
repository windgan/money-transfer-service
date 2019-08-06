package com.revolut.transfer.environment;

import com.google.inject.Guice;
import com.google.inject.Module;
import io.logz.guice.jersey.JerseyModule;
import io.logz.guice.jersey.JerseyServer;
import io.logz.guice.jersey.configuration.JerseyConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class Server {

    private static final int SERVER_PORT = 8080;
    private final Class resourceClass;

    private JerseyServer server;

    public void start() throws Exception {
        init();
        server.start();
        log.info("REST server started");
    }

    public void stop() {
        try {
            server.stop();
            log.info("REST server stopped");
        } catch (Exception e) {
            log.error("Error during server stop", e);
            throw new RuntimeException(e);
        }
    }

    private void init() {
        log.trace("initializing server");
        JerseyConfiguration configuration = JerseyConfiguration.builder()
                .addResourceClass(resourceClass)
                .addResourceClass(io.swagger.jaxrs.listing.ApiListingResource.class)
                .addResourceClass(io.swagger.jaxrs.listing.SwaggerSerializers.class)
                .addPort(SERVER_PORT)
                .build();

        List<Module> modules = Collections.singletonList(new JerseyModule(configuration));

        server = Guice.createInjector(modules).getInstance(JerseyServer.class);
    }
}
