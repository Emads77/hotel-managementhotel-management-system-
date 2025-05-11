package nl.saxion.oop.model.clients;

import java.lang.ref.Cleaner;

public class Person extends Client {
    private String firstName;
    private String lastName;

    public Person(String firstName, String lastName, String creditCard) {
        super(creditCard);
        this.firstName = firstName;
        this.lastName = lastName;
    }



    @Override
    public String getName() {
        return firstName;
    }


    @Override
    public String toString() {
        return firstName + "[Person]";
    }


}