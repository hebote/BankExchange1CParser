import net.matveyev.BankExchange1CParser.BE1CPFileFormatIsInvalid;
import net.matveyev.BankExchange1CParser.BankExchange1CParser;
import net.matveyev.BankExchange1CParser.BankExchange1CParserException;
import net.matveyev.BankTransfer.RussianBankTransfer;

public class BE1CPDemo {

    public static void main(String args[]) {

        if(args.length != 1) {
            System.out.println("Please specify input file!");
        } else {

            try {

                BankExchange1CParser file = new BankExchange1CParser(args[0]);

                for(Object object : file.transferList) {

                    RussianBankTransfer transfer = (RussianBankTransfer) object;
                    System.out.println(transfer);

                }

                System.out.println("Total " + file.transferList.size() + " transfers.");

            } catch(java.io.IOException exc) {

                System.out.println("IOException: " + exc.getLocalizedMessage());

            } catch(BE1CPFileFormatIsInvalid exc) {

                System.out.println("File format is incorrect.");

            } catch(BankExchange1CParserException exc) {

                System.out.println("Something went wrong: " + exc);

            }

        }
    }
}
