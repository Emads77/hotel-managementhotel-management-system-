package nl.saxion.oop.model.clients;

public class Company extends Client {
    private final String companyName;
    private final boolean isTaxable;


    public Company(String companyName,String creditCard, boolean isTaxable) {
        super(creditCard);
        this.companyName = companyName;
        this.isTaxable = isTaxable;
    }

    public boolean isTaxable() {
        return isTaxable;
    }

    @Override
    public String getName() {
        return companyName;
    }

    @Override
    public String toString() {
        return companyName;
    }


}
