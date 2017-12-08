package net.matveyev.BankExchange1CParser;

import net.matveyev.BankAccount.RussianBankAccount;
import net.matveyev.BankTransfer.RussianBankTransfer;
import net.matveyev.Company.RussianCompany;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BankExchange1CParser {

    /*
        Supports 1.02 format from here: http://v8.1c.ru/edi/edi_stnd/100/101.htm
     */

    private String filename;
    private String fileContents;
    public long fileLength; // file length in bytes
    public String versionOfFormat,
                    fileEncoding;
    int documentCount, accountCount;
    LocalDate fileFirstTransferDate,
                    fileLastTransferDate;
    public Map ourAccountsMap, ourCompaniesMap;
    public Map counterpartiesAccountsMap, counterpartiesCompaniesMap;
    public List transferList;
    private RussianBankAccount bankAccount, payerBankAccount, recipientBankAccount; // intermediate storage for accounts data.
    private RussianCompany payerCompany, recipientCompany;
    private RussianBankTransfer transferData; // intermediate storage for transfer data.


    public BankExchange1CParser(String fn) throws IOException,BankExchange1CParserException {

        filename = fn;
        documentCount = accountCount = 0;
        ourCompaniesMap = new HashMap();
        ourAccountsMap = new HashMap();
        counterpartiesCompaniesMap = new HashMap();
        counterpartiesAccountsMap = new HashMap();
        transferList = new ArrayList();

        readFile();

        try {
            parseContents();
        } catch (BankExchange1CParserException exc) {
            throw exc;
        }

        try {
            validateResults();
        } catch (BankExchange1CParserException exc) {
            throw exc;
        }

    }

    private void readFile() throws IOException {

        String encoding = "windows-1251";
        readFile(encoding);

    }

    private void readFile(String encoding) throws IOException {

        //System.out.println("Trying encoding " + encoding);
        File file = new File(filename);
        fileLength = file.length();

        Scanner scanner = new Scanner(file, encoding);

        try {

            fileContents = scanner.nextLine();

            while( scanner.hasNextLine() ) {
                fileContents = fileContents + "\n" + scanner.nextLine();
            }

        } catch (NoSuchElementException exc) {

            scanner.close();

            switch(encoding) { // If windows-1251 does not work, try Cp866, then UTF-8

                case "windows-1251":
                    //System.out.println("windows-1251 did not work, try Cp866...");
                    readFile("Cp866");
                    break;
                case "Cp866":
                    //System.out.println("Cp866 did not work, try UTF-8...");
                    readFile("UTF-8");
                    break;
                case "UTF-8":
                    //System.out.println("UTF-8 did not work either!");
                    break;

            }

        } finally {
            scanner.close();
        }
    }

    private void parseContents() throws BankExchange1CParserException{

        String str[]; // Strings are splitted by key=value into this array

        boolean bankAccountFlag = false; // set / unset when the account section begins and ends. Needed to disambiguate keys meaning.

        boolean documentFlag = false; // set / unset when the document section begins and ends. Needed to disambiguate keys meaning.

        String arr[] = fileContents.split("\n");

        if( !arr[0].equals("1CClientBankExchange") ) throw new BE1CPFileFormatIsInvalid();

        for(int i=1; i<arr.length; i++) {

            str = arr[i].split("=");

            switch(str.length) {

                case 1:
                    switch(str[0]) {

                        case "СекцияРасчСчет":
                            bankAccountFlag = true;
                            accountCount++;
                            bankAccount = new RussianBankAccount();
                            break;

                        case "КонецРасчСчет":
                            bankAccountFlag = false;

                            // Save account to the map
                            ourAccountsMap.put( bankAccount.id(), bankAccount );

                            break;

                        case "КонецДокумента":
                            documentFlag = false;

                            // Payer part
                            if( !ourAccountsMap.containsKey( payerBankAccount.id() ) ) {                                                            // If it's not our account

                                counterpartiesAccountsMap.put( payerBankAccount.id(), payerBankAccount);                                            // Put it to counterparties account map.
                                transferData.payerAccount = (RussianBankAccount) counterpartiesAccountsMap.get( payerBankAccount.id() );            // And link the account from the map with the transfer.
                                ((RussianBankAccount) counterpartiesAccountsMap.get(payerBankAccount.id())).merge(payerBankAccount);                // Merge the data collected to the object existing in map.

                                if( !counterpartiesCompaniesMap.containsKey(payerCompany.id()) )
                                    counterpartiesCompaniesMap.put( payerCompany.id(), payerCompany );                                              // Save counterparty company in the list.

                                transferData.payer = (RussianCompany) counterpartiesCompaniesMap.get( payerCompany.id());                           // Link to the transfer from the map.
                                transferData.payer.addAccount((RussianBankAccount) counterpartiesAccountsMap.get(payerBankAccount.id()));           // Link the acount to the company.

                            } else {

                                transferData.payerAccount = (RussianBankAccount) ourAccountsMap.get(payerBankAccount.id());                         // Or link to our accounts map.
                                ((RussianBankAccount) ourAccountsMap.get(payerBankAccount.id())).merge(payerBankAccount);                           // Merge the data collected to the object existing in map.

                                if( !ourCompaniesMap.containsKey(payerCompany.id()) )
                                    ourCompaniesMap.put( payerCompany.id(), payerCompany );                                                         // Save our company in the list.

                                transferData.payer = (RussianCompany) ourCompaniesMap.get( payerCompany.id());                                      // Link to the transfer from the map.
                                transferData.payer.addAccount((RussianBankAccount) ourAccountsMap.get(payerBankAccount.id()));                      // Link the acount to the company.
                            }

                            // Recipient part
                            if( !ourAccountsMap.containsKey(recipientBankAccount.id()) ) {                                                          // If it's not our account

                                counterpartiesAccountsMap.put( recipientBankAccount.id(), recipientBankAccount );                                   // Put it to counterparties account map.
                                transferData.recipientAccount = (RussianBankAccount) counterpartiesAccountsMap.get(recipientBankAccount.id());      // And link the account from the map with the transfer.
                                ((RussianBankAccount) counterpartiesAccountsMap.get(recipientBankAccount.id())).merge(recipientBankAccount);        // Merge the data collected to the object existing in map.

                                if( !counterpartiesCompaniesMap.containsKey(recipientCompany.id()) )
                                    counterpartiesCompaniesMap.put( recipientCompany.id(), recipientCompany );                                      // Save counterparty company in the list.

                                transferData.recipient = (RussianCompany) counterpartiesCompaniesMap.get( recipientCompany.id());                   // Link to the transfer from the map.
                                transferData.recipient.addAccount((RussianBankAccount) counterpartiesAccountsMap.get(recipientBankAccount.id()));   // Link the acount to the company.

                            } else {

                                transferData.recipientAccount = (RussianBankAccount) ourAccountsMap.get(recipientBankAccount.id());                 // Or link to our accounts map.
                                ((RussianBankAccount) ourAccountsMap.get(recipientBankAccount.id())).merge(recipientBankAccount);                   // Merge the data collected to the object existing in map.

                                if( !ourCompaniesMap.containsKey(recipientCompany.id()) )
                                    ourCompaniesMap.put( recipientCompany.id(), recipientCompany );                                                 // Save our company in the list.

                                transferData.recipient = (RussianCompany) ourCompaniesMap.get( recipientCompany.id());                              // Link to the transfer from the map.
                                transferData.recipient.addAccount((RussianBankAccount) ourAccountsMap.get(recipientBankAccount.id()));              // Link the acount to the company.

                            }

                            // Transfers part
                            transferList.add(transferData);
                            break;

                        case "КонецФайла":
                            break;

                        case "Код": // Keys without values. Leave them unset.
                        case "ВидПлатежа":
                        case "ПолучательКорсчет":
                        case "ПоказательТипа":
                        case "ПолучательКПП":
                        case "СтатусСоставителя":
                        case "ПоказательКБК":
                        case "ОКАТО":
                        case "ПоказательОснования":
                        case "ПоказательПериода":
                        case "ПоказательДаты":
                        case "ПлательщикКПП":
                        case "ПлательщикКорсчет":
                        case "ПоказательНомера":
                        case "ПлательщикИНН":
                        case "ПлательщикБИК":
                        case "ПлательщикБанк1":
                        case "Очередность":
                            break;

                        default: System.out.println("You forgot to implement this: " + arr[i]);

                    }
                    break;

                case 2:
                    switch(str[0]) {

                        case "ВерсияФормата":
                            if(str[1].equals("1.02")) versionOfFormat = str[1];
                            else throw new BE1CPFileFormatUnsupported();
                            break;

                        case "Кодировка": // "Windows" or "DOS", however if we read the contents, it does not matter. Discard the value.
                            break;

                        case "Отправитель": // Not implemented.
                            break;

                        case "ДатаСоздания": // Not implemented.
                        break;

                        case "ВремяСоздания": // Not implemented.
                        break;

                        case "ДатаНачала": // may have different meanings in different sections
                        if(bankAccountFlag) { //first transfer date for this account

                            // Discard the value.

                        } else { // first transfer date in the file
                            fileFirstTransferDate = dateToLocalDate(str[1]);
                        }
                        break;

                        case "ДатаКонца": // may have different meanings in different sections
                        if(bankAccountFlag) { //last transfer date for this account

                            // Discard the value.

                        } else { // last transfer date in the file
                            fileLastTransferDate = dateToLocalDate(str[1]);
                        }
                        break;

                        case "РасчСчет": // may have different meanings in different sections
                        if(bankAccountFlag) {

                            // Save this account number to the account object.
                            bankAccount.setAccountNumber( str[1] );

                        } else {
                            // Ignore. It is repeated twice.
                        }
                        break;

                        case "Документ":
                        documentFlag = true;
                        break;

                        case "НачальныйОстаток":
                        if(bankAccountFlag) {

                            // Save starting balance to the account object.
                            bankAccount.setStartingBalance( str[1] );

                        } else throw new BE1CPFileFormatAmbiguous(); // Starting balance does not make sense outside of bank account description.
                        break;

                        case "ВсегоПоступило":
                        if(bankAccountFlag) {

                            // Save incoming transfers amount to the account object.
                            bankAccount.setIncomingTransfersAmount( str[1] );

                        } else throw new BE1CPFileFormatAmbiguous(); // Transfers amount does not make sense outside of bank account description.
                        break;

                        case "ВсегоСписано":
                        if(bankAccountFlag) {

                            // Save outgoing transfers amount to the account object.
                            bankAccount.outgoingTransfersAmount = new BigDecimal( str[1] );

                        } else throw new BE1CPFileFormatAmbiguous(); // Transfers amount does not make sense outside of bank account description.
                        break;

                        case "КонечныйОстаток":
                        if(bankAccountFlag) {

                            // Save ending balance amount to the account object.
                            bankAccount.setEndingBalance( str[1] );

                        } else throw new BE1CPFileFormatAmbiguous(); // Ending balance does not make sense outside of bank account description.
                        break;

                        case "СекцияДокумент":
                        documentFlag = true;
                        documentCount++;
                        payerCompany = new RussianCompany();
                        payerBankAccount = new RussianBankAccount();
                        recipientCompany = new RussianCompany();
                        recipientBankAccount = new RussianBankAccount();
                        transferData = new RussianBankTransfer();

                        transferData.setType( str[1] );
                        break;

                        case "Номер":
                        transferData.setNumber( str[1] );
                        break;

                        case "Дата":
                        transferData.setDate( str[1] );
                        break;

                        case "Сумма":
                        transferData.setAmount( str[1] );
                        break;

                        case "КвитанцияДата": // Not implemented.
                        break;

                        case "КвитанцияВремя": // Not implemented.
                        break;

                        case "КвитанцияСодержание": // Not implemented.
                        break;

                        case "ПлательщикСчет":
                        payerBankAccount.setAccountNumber( str[1] );
                        break;

                        case "ДатаСписано":
                        transferData.setDateWrittenOff( str[1] );
                        break;

                        case "Плательщик":
                        payerCompany.setINNAndName( str[1] );
                        break;

                        case "ПлательщикИНН":
                        payerCompany.setINN( str[1] );
                        break;

                        case "Плательщик1":
                        payerCompany.setName( str[1] );
                        break;

                        case "Плательщик2": // Not implemented.
                        break;

                        case "Плательщик3": // Not implemented.
                        break;

                        case "Плательщик4": // Not implemented.
                        break;

                        case "ПлательщикРасчСчет": // Not implemented.
                        break;

                        case "ПлательщикБанк1":
                            payerBankAccount.setBankName( str[1] );
                        break;

                        case "ПлательщикБанк2": // Not implemented.
                        break;

                        case "ПлательщикБИК":
                        payerBankAccount.setBIC( str[1] );
                        break;

                        case "ПлательщикКорсчет":
                        payerBankAccount.setCorrespondentNumber( str[1] );
                        break;

                        case "Получатель": // may have different meanings in different sections
                            if(documentFlag) { // Inside document it's the  name of the recipient
                                recipientCompany.setName( str[1] );
                            } else { // The name of the recipient software for the batch.
                                // Ignore
                            }
                            break;

                        case "ПолучательСчет":
                        recipientBankAccount.setAccountNumber( str[1] );
                        break;

                        case "ДатаПоступило":
                        transferData.setDateArrived( str[1] );
                        break;

                        case "ПолучательИНН":
                        recipientCompany.setINN( str[1] );
                        break;

                        case "Получатель1":
                            if(documentFlag) { // Inside document it's the name of the recipient
                                recipientCompany.setName( str[1] );
                            } else { // The name of the recipient software for the batch.
                                // Ignore
                            }
                            break;

                        case "Получатель2": // Not implemented.
                            break;

                        case "Получатель3": // Not implemented.
                            break;

                        case "Получатель4": // Not implemented.
                            break;

                        case "ПолучательРасчСчет": // Not implemented.
                            break;

                        case "ПолучательБанк1":
                            recipientBankAccount.setBankName( str[1] );
                            break;

                        case "ПолучательБанк2": // Not implemented.
                            break;

                        case "ПолучательБИК":
                            recipientBankAccount.setBIC( str[1] );
                            break;

                        case "ПолучательКорсчет":
                            recipientBankAccount.setCorrespondentNumber( str[1] );
                            break;

                        case "ВидПлатежа": // Not implemented.
                            break;

                        case "ВидОплаты": // Not implemented.
                            break;

                        case "Код": // Not implemented.
                            break;

                        case "НазначениеПлатежа":
                            transferData.description = str[1];
                            break;

                        case "НазначениеПлатежа 1":
                            // Ignore
                            break;

                        case "НазначениеПлатежа 2":
                            // Ignore
                            break;

                        case "НазначениеПлатежа 3":
                            // Ignore
                            break;

                        case "НазначениеПлатежа 4":
                            // Ignore
                            break;

                        case "НазначениеПлатежа 5":
                            // Ignore
                            break;

                        case "НазначениеПлатежа 6":
                            // Ignore
                            break;

                        case "СтатусСоставителя": // Not implemented.
                            break;

                        case "ПлательщикКПП":
                            payerCompany.setKPP( str[1] );
                            break;

                        case "ПолучательКПП":
                            recipientCompany.setKPP( str[1] );
                            break;

                        case "ПоказательКБК": // Not implemented.
                            break;

                        case "ОКАТО": // Not implemented.
                            break;

                        case "ПоказательОснования": // Not implemented.
                            break;

                        case "ПоказательПериода": // Not implemented.
                            break;

                        case "ПоказательНомера": // Not implemented.
                            break;

                        case "ПоказательДаты": // Not implemented.
                            break;

                        case "ПоказательТипа": // Not implemented.
                            break;

                        case "Очередность":
                            transferData.setOrder( str[1] );
                            break;

                        case "СрокАкцепта": // Not implemented.
                            break;

                        case "ВидАккредитива": // Not implemented.
                            break;

                        case "СрокПлатежа": // Not implemented.
                            break;

                        case "УсловиеОплаты1": // Not implemented.
                            break;

                        case "УсловиеОплаты2": // Not implemented.
                            break;

                        case "УсловиеОплаты3": // Not implemented.
                            break;

                        case "ПлатежПоПредст": // Not implemented.
                            break;

                        case "ДополнУсловия": // Not implemented.
                            break;

                        case "НомерСчетаПоставщика": // Not implemented.
                            break;

                        case "ДатаОтсылкиДок": // Not implemented.
                            break;

                        default: System.out.println("You forgot to implement this: " + arr[i]);

                    }
            }

        }

    }

    private LocalDate dateToLocalDate(String str) {

        return LocalDate.parse(str, DateTimeFormatter.ofPattern("dd.MM.yyyy"));

    }

    private void validateResults() throws BE1CPDataInconsistent {

        // Incoming transfers total sum verification

        List incomingTransfers = incomingTransfers();

        BigDecimal totalAmount = new BigDecimal(0);
        BigDecimal totalAmountExpected = new BigDecimal(0);

        for( Object transferObject : incomingTransfers) {

            RussianBankTransfer transfer = (RussianBankTransfer) transferObject;
            totalAmount = totalAmount.add(transfer.amount);

        }

        for( Object key : ourAccountsMap.keySet() ) {

            Object value = ourAccountsMap.get(key);

            RussianBankAccount account = (RussianBankAccount) value;
            totalAmountExpected = totalAmountExpected.add(account.incomingTransfersAmount);

        }

        for( Object key : ourCompaniesMap.keySet() ) {

            Object value = ourCompaniesMap.get(key);

            RussianCompany company = (RussianCompany) value;

        }

        if( !totalAmount.equals(totalAmountExpected) ) {
            throw new BE1CPDataInconsistent();
        }

        // Outgoing transfers total sum verification

        List outgoingTransfers = outgoingTransfers();

        totalAmount = new BigDecimal(0);
        totalAmountExpected = new BigDecimal(0);

        for( Object transferObject : outgoingTransfers) {

            RussianBankTransfer transfer = (RussianBankTransfer) transferObject;
            totalAmount = totalAmount.add(transfer.amount);

        }

        for( Object key : ourAccountsMap.keySet() ) {

            Object value = ourAccountsMap.get(key);

            RussianBankAccount account = (RussianBankAccount) value;
            totalAmountExpected = totalAmountExpected.add(account.outgoingTransfersAmount);

        }

        if( !totalAmount.equals(totalAmountExpected) ) {
            throw new BE1CPDataInconsistent();
        }

        // Overall statistics

        if( accountCount != ourAccountsMap.size() ) {
            throw new BE1CPDataInconsistent();
        }

        if( documentCount != transferList.size() ) {
            throw new BE1CPDataInconsistent();
        }

    }

    public List incomingTransfers() {

        List incomingTransfers = new ArrayList();

        for( Object transferObject : transferList) {

            RussianBankTransfer transfer = (RussianBankTransfer) transferObject;

            if( ourAccountsMap.containsKey( transfer.recipientAccount.id() ) ) {
                incomingTransfers.add( transfer );
            }

        }

        return incomingTransfers;
    }

    public List outgoingTransfers() {

        List outgoingTransfers = new ArrayList();

        for( Object transferObject : transferList) {

            RussianBankTransfer transfer = (RussianBankTransfer) transferObject;

            if( ourAccountsMap.containsKey( transfer.payerAccount.id() ) ) outgoingTransfers.add( transfer );

        }

        return outgoingTransfers;
    }

}
