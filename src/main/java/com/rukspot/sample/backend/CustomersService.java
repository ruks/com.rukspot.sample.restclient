/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rukspot.sample.backend;

import com.google.gson.JsonObject;
import com.rukspot.sample.backend.dto.Customer;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

//curl http://localhost:9090/inventory/customers/{code}

/**
 * This is the Microservice resource class.
 * See <a href="https://github.com/wso2/msf4j#getting-started">https://github.com/wso2/msf4j#getting-started</a>
 * for the usage of annotations.
 *
 * @since 0.1-SNAPSHOT
 */
@Path("/inventory/customers")
public class CustomersService {

    @GET
    @Path("/{code}")
    @Produces({"application/json"})
    public Response getCode(@PathParam("code") int code) {
        System.out.println("GET invoked " + code);
        Response.Status status = Response.Status.fromStatusCode(code);
        if(status == null) {
            status = Response.Status.INTERNAL_SERVER_ERROR;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", status.getStatusCode());
        jsonObject.addProperty("reason", status.getReasonPhrase());
        return Response.status(status).entity(jsonObject).build();
    }

    @GET
    @Path("/")
    @Produces({"application/json"})
    public Response get() {
        List<Customer> customers = StoreDAO.getInstance().getCustomers();
        return Response.status(Response.Status.OK).entity(customers).build();
    }

    @POST
    @Path("/")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response post(Customer customer) {
        Customer newCustomer = StoreDAO.getInstance().addCustomer(customer);
        return Response.status(Response.Status.OK).entity(newCustomer).build();
    }

    @PUT
    @Path("/")
    public void put() {
        // TODO: Implementation for HTTP PUT request
        System.out.println("PUT invoked");
    }

    @DELETE
    @Path("/")
    public void delete() {
        // TODO: Implementation for HTTP DELETE request
        System.out.println("DELETE invoked");
    }
}
