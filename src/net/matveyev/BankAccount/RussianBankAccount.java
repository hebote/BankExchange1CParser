package net.matveyev.BankAccount;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RussianBankAccount {

    public String accountNumber, BIC, bankName, correspondentNumber;
    public LocalDate firstTransferDate, lastTransferDate;
    public BigDecimal startingBalance, endingBalance, incomingTransfersAmount, outgoingTransfersAmount;

    public RussianBankAccount() {

    }

    public RussianBankAccount(String an) {

        accountNumber = an;

    }

    public RussianBankAccount(String an, String b, String bn, String cn){

        accountNumber = an;
        BIC = b;
        bankName = bn;
        correspondentNumber = cn;

    }

    public String id() {
        return accountNumber;
    }

    public void merge( RussianBankAccount account) {

        if( !accountNumber.equals(account.accountNumber) ) {
            // TODO: throw an exception
        }

        if( BIC == null && account.getBIC() != null )                                           BIC = account.getBIC();
        if( bankName == null && account.getBankName() != null )                                 bankName = account.getBankName();
        if( correspondentNumber == null && account.getCorrespondentNumber() != null )           correspondentNumber = account.getCorrespondentNumber();
        if( firstTransferDate == null && account.getFirstTransferDate() != null )               firstTransferDate = account.getFirstTransferDate();
        if( lastTransferDate == null && account.getLastTransferDate() != null )                 lastTransferDate = account.getLastTransferDate();
        if( startingBalance == null && account.getStartingBalance() != null )                   startingBalance = account.getStartingBalance();
        if( endingBalance == null && account.getEndingBalance() != null )                       endingBalance = account.getEndingBalance();
        if( incomingTransfersAmount == null && account.getIncomingTransfersAmount() != null )   incomingTransfersAmount = account.getIncomingTransfersAmount();
        if( outgoingTransfersAmount == null && account.getOutgoingTransfersAmount() != null )   outgoingTransfersAmount = account.getOutgoingTransfersAmount();

    }


    @Override
    public String toString() {
        return super.toString() + "\tAccount: " + accountNumber + "\tBank: " + bankName + "\tBIC: " + BIC;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }



    public String getBIC() {
        return BIC;
    }

    public void setBIC(String BIC) {
        this.BIC = BIC;
    }



    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }



    public String getCorrespondentNumber() {
        return correspondentNumber;
    }

    public void setCorrespondentNumber(String correspondentNumber) {
        this.correspondentNumber = correspondentNumber;
    }



    public LocalDate getFirstTransferDate() {
        return firstTransferDate;
    }

    public void setFirstTransferDate(LocalDate firstTransferDate) {
        this.firstTransferDate = firstTransferDate;
    }



    public LocalDate getLastTransferDate() {
        return lastTransferDate;
    }

    public void setLastTransferDate(LocalDate lastTransferDate) {
        this.lastTransferDate = lastTransferDate;
    }



    public BigDecimal getStartingBalance() {
        return startingBalance;
    }

    public void setStartingBalance(BigDecimal startingBalance) {
        this.startingBalance = startingBalance;
    }

    public void setStartingBalance(String startingBalance) {
        this.startingBalance = new BigDecimal( startingBalance );
    }



    public BigDecimal getEndingBalance() {
        return endingBalance;
    }

    public void setEndingBalance(BigDecimal endingBalance) {
        this.endingBalance = endingBalance;
    }

    public void setEndingBalance(String endingBalance) {
        this.endingBalance = new BigDecimal( endingBalance );
    }



    public BigDecimal getIncomingTransfersAmount() {
        return incomingTransfersAmount;
    }

    public void setIncomingTransfersAmount(BigDecimal incomingTransfersAmount) {
        this.incomingTransfersAmount = incomingTransfersAmount;
    }

    public void setIncomingTransfersAmount(String incomingTransfersAmount) {
        this.incomingTransfersAmount = new BigDecimal( incomingTransfersAmount );
    }



    public BigDecimal getOutgoingTransfersAmount() {
        return outgoingTransfersAmount;
    }

    public void setOutgoingTransfersAmount(BigDecimal outgoingTransfersAmount) {
        this.outgoingTransfersAmount = outgoingTransfersAmount;
    }

    public void setOutgoingTransfersAmount(String outgoingTransfersAmount) {
        this.outgoingTransfersAmount = new BigDecimal( outgoingTransfersAmount );
    }

}
