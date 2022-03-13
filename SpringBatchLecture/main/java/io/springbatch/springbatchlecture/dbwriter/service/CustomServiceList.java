package io.springbatch.springbatchlecture.dbwriter.service;

import io.springbatch.springbatchlecture.dbitemreader.Customer;

import java.time.LocalDate;

public class CustomServiceList<T> {

    public Customer joinMember() {
        Customer customer = new Customer();
        customer.setFirstName("abc");
        customer.setLastName("qwe");
        customer.setBirthDate(LocalDate.now());
        return customer;
    }

    public void writeMember(T item) {
        System.out.println("item = " + item);
    }
}
