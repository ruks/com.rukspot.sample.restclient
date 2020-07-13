/*
 *   Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package com.rukspot.sample.backend;


import com.rukspot.sample.backend.Exceptions.CustomerNotFoundException;
import com.rukspot.sample.backend.dto.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StoreDAO {
    private Map<String, Customer> customerMap = new HashMap();
    private static StoreDAO storeDAO = new StoreDAO();

    private StoreDAO() {
        Customer customer = new Customer();
        customer.setName("Peter");
        customer.setUuid("1111");
        customerMap.put(customer.getUuid(), customer);
//        addCustomer(customer);
    }

    public static StoreDAO getInstance() {
        return storeDAO;
    }

    public List<Customer> getCustomers() {
        return new ArrayList<>(customerMap.values());
    }

    public Customer getCustomerByUUID(String uuid) throws CustomerNotFoundException {
        if(customerMap.containsKey(uuid)) {
            return customerMap.get(uuid);
        } else {
            throw new CustomerNotFoundException();
        }

    }

    public Customer addCustomer(Customer customer) {
        customer.setUuid(UUID.randomUUID().toString());
        customerMap.put(customer.getUuid(), customer);
        return customerMap.get(customer.getUuid());
    }

    public Customer updateCustomer(String uuid, Customer customer) throws CustomerNotFoundException {
        if(!customerMap.containsKey(uuid)) {
            throw new CustomerNotFoundException();
        }
        return customerMap.put(uuid, customer);
    }
}
