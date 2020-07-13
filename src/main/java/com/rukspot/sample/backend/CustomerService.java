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

import com.rukspot.sample.backend.Exceptions.CustomerNotFoundException;
import com.rukspot.sample.backend.dto.Customer;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

//curl http://localhost:8080/hello/wso2

/**
 * This is the Microservice resource class.
 * See <a href="https://github.com/wso2/msf4j#getting-started">https://github.com/wso2/msf4j#getting-started</a>
 * for the usage of annotations.
 *
 * @since 0.1-SNAPSHOT
 */
@Path("/inventory/customer")
public class CustomerService {
    @GET
    @Path("/")
    @Produces({"application/json"})
    public Response get(@QueryParam("uuid") String uuid) {
        try {
            Customer customer = StoreDAO.getInstance().getCustomerByUUID(uuid);
            return Response.status(Response.Status.OK).entity(customer).build();
        } catch (CustomerNotFoundException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
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
