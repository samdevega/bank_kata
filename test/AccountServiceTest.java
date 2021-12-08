import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountServiceTest {
    @Test
    public void test() {

        assertEquals(true, true);
    }

    @Test
    public void Given_a_client_makes_a_deposit_of_1000_on_10_01_2012(){
        var date1 = new GregorianCalendar(2012, Calendar.JANUARY, 10);
        var date2 = new GregorianCalendar(2012, Calendar.JANUARY, 10);
        var date3 = new GregorianCalendar(2012, Calendar.JANUARY, 10);
        var dateSystem = new DateOwnMock(new ArrayList<GregorianCalendar>(Arrays.asList(
           date1,
           date2,
           date3
        )));
        var formatter = new FormatterMock(
                "Date       || Amount || Balance\n" +
                        "14/01/2012 || -500   || 2500\n" +
                        "13/01/2012 || 2000   || 3000\n" +
                        "10/01/2012 || 1000   || 1000");
        var account = new Account(dateSystem,formatter);

        account.deposit(1000);
        account.deposit(2000);
        account.withdraw(500);

        assertTrue(dateSystem.getCalls() == 3);
        assertEquals(formatter.history, new AccountHistoryMock(
            new ArrayList<>(Arrays.asList(
                new Record(date1, 1000,1000),
                new Record(date2, 2000,3000),
                new Record(date3, -500,2500)
            ))));
    }

    public class Account implements AccountService {

        public Account(DateOwn dateSystem, Formatter formatter) {
            new ExecutionControl.NotImplementedException("");
        }

        public void deposit(int amount){
            new ExecutionControl.NotImplementedException("");
        }

        @Override
        public void withdraw(int amount) {
            new ExecutionControl.NotImplementedException("");
        }

        @Override
        public void printStatement() {
            new ExecutionControl.NotImplementedException("");
        }
    }

    public interface DateOwn {
        GregorianCalendar currentDate();
    }

    public class DateOwnMock implements DateOwn {

        public ArrayList<GregorianCalendar> fakeDates;
        private int calls = 0;

        public DateOwnMock(ArrayList<GregorianCalendar> fakeDates) {
            this.fakeDates = fakeDates;
        }

        public int getCalls() {
            return calls;
        }

        @Override
        public GregorianCalendar currentDate() {
            var tempCalls = calls;
            calls = calls +1;
            return fakeDates.get(tempCalls);
        }
    }

    public interface Formatter {
        String format(IAccountHistory history);
    }

    public class FormatterMock implements Formatter {

        public IAccountHistory history;
        private String fakeString;

        public FormatterMock(String fakeString) {
            this.fakeString = fakeString;
        }

        @Override
        public String format(IAccountHistory history) {
            this.history = history;
            return fakeString;
        }
    }

    public interface IAccountHistory{
        void addRecord(GregorianCalendar date, int amount, int balance);
        ArrayList getHistory();
    }

    public class AccountHistoryMock implements IAccountHistory{
        public int calls = 0;
        public ArrayList<Record> fakeHistory;


        public AccountHistoryMock(ArrayList<Record> fakeHistory) {
            this.fakeHistory = fakeHistory;
        }

        public void addRecord(GregorianCalendar date, int amount, int balance){
            calls = calls +1;
        }

        public ArrayList<Record> getHistory(){
            return fakeHistory;
        }
    }

    public class Record {
        private GregorianCalendar date;
        private int amount;
        private int balance;

        public Record(GregorianCalendar date, int amount, int balance) {
            this.date = date;
            this.amount = amount;
            this.balance = balance;
        }

        public GregorianCalendar getDate() {
            return date;
        }

        public int getAmount() {
            return amount;
        }

        public int getBalance() {
            return balance;
        }
    }
}
