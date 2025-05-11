package nl.saxion.oop.model.clients;

public abstract class Client {


    private String creditCard;


    public abstract String getName();

    public Client(String creditCard) {
        this.creditCard = creditCard;
    }



}
