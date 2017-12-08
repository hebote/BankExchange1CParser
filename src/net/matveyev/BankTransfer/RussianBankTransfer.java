package net.matveyev.BankTransfer;

import net.matveyev.BankAccount.RussianBankAccount;
import net.matveyev.Company.RussianCompany;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RussianBankTransfer {

    public String currency = "RUB", description, type, bankName;
    public int number, order;
    public LocalDate date, dateWrittenOff, dateArrived;
    public BigDecimal amount;
    public RussianBankAccount payerAccount, recipientAccount;
    public RussianCompany payer, recipient;

    public RussianBankTransfer() {

    }

    @Override
    public String toString() {
        return super.toString() + "\tDate: " + date + "\tAmount: " + amount + "\tPayer: " + payer + "\tPayer account: " + payerAccount + "\tRecipient: " + recipient + "\tRecipient account: " + recipientAccount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setNumber(String number) {
        this.number = Integer.parseInt( number );
    }



    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDate(String date) {
        this.date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }



    public LocalDate getDateWrittenOff() {
        return dateWrittenOff;
    }

    public void setDateWrittenOff(LocalDate date) {
        this.dateWrittenOff = date;
    }

    public void setDateWrittenOff(String date) {
        this.dateWrittenOff = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }



    public LocalDate getDateArrived() {
        return dateArrived;
    }

    public void setDateArrived(LocalDate date) {
        this.dateArrived = date;
    }

    public void setDateArrived(String date) {
        this.dateArrived = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }



    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAmount(String amount) {
        this.amount = new BigDecimal(amount);
    }


    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setOrder(String order) {
        this.order = Integer.parseInt(order);
    }



    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
