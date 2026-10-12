package Thread_Programming_project3.system;

import Thread_Programming_project3.Methods;

import java.io.Serializable;
import java.util.HashMap;

public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private String Name;
    private String email;
    private String phoneNumber;
    private HashMap<String, String> Portfolios;
    private Double balance;
    private final String id;
    private String USER_PATH;

    private static final String ID_SET_PATH =
            "src/Thread_Programming_project3/Files/accountIDs.bin";

    public Account(String Name, String email, String phoneNumber) {
        this.Name = Name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.balance = 0.0;
        this.Portfolios = new HashMap<>();
        this.id = Methods.idSet(ID_SET_PATH);
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public HashMap<String, String> getPortfolios() {
        return Portfolios;
    }

    public void setPortfolios(HashMap<String, String> portfolios) {
        this.Portfolios = portfolios;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getUSER_PATH() {
        return USER_PATH;
    }

    public void setUSER_PATH(String USER_PATH) {
        this.USER_PATH = USER_PATH;
    }
}
