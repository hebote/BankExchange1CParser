package net.matveyev.Company;

import net.matveyev.BankAccount.RussianBankAccount;
import java.util.HashMap;
import java.util.Map;

public class RussianCompany {

    private String INN, KPP, name;

    private Map accounts;

    public RussianCompany(){
        accounts = new HashMap();
    }


    @Override
    public String toString() {
        return super.toString() + "\tINN: " + INN + "\tName: " + name + "\tKPP: " + KPP;
    }

    public void setINNAndName(String innAndName) {
        String arr[] = innAndName.split(" ", 2);
        INN = arr[0];
        name = arr[1];
    }

    public String id() {
        if( INN.equals("000000000000") ) return INN + name;
            else return INN;
    }

    public String getINN() {
        return INN;
    }

    public void setINN(String INN) {
        this.INN = INN;
    }



    public String getKPP() {
        return KPP;
    }

    public void setKPP(String KPP) {
        this.KPP = KPP;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {

        if( this.name == null ) this.name = name;
            else if( !this.name.equals(name) ) this.name = this.name + " (" + name + ")";

    }



    public Map getAccounts() {
        return accounts;
    }

    public void addAccount( RussianBankAccount toAdd) {
        accounts.put(toAdd.id(), toAdd);
    }
}
