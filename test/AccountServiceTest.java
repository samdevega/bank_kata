import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountServiceTest {
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
        account.printStatement();

        assertEquals(formatter.history,
            new ArrayList<>(Arrays.asList(
                new Record(date1, 1000,1000),
                new Record(date2, 2000,3000),
                new Record(date3, -500,2500)
            )));
    }

    public class Account implements AccountService {
        private ArrayList<Record> history;
        private DateOwn dateSystem;
        private Formatter formatter;

        public Account(DateOwn dateSystem, Formatter formatter) {
            this.history = new ArrayList<>();
            this.dateSystem = dateSystem;
            this.formatter = formatter;
        }

        public void deposit(int amount) {
            int balance = history.stream()
                .map((record) -> record.amount)
                .reduce(0, (accumulator, current) -> accumulator + current);
            balance += amount;
            history.add(new Record(this.dateSystem.currentDate(), amount, balance));
        }

        @Override
        public void withdraw(int amount) {
            int balance = history.stream()
                .map((record) -> record.amount)
                .reduce(0, (accumulator, current) -> accumulator + current);
            balance -= amount;
            history.add(new Record(this.dateSystem.currentDate(), -amount, balance));
        }

        @Override
        public void printStatement() {
            formatter.format(history);
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
        String format(ArrayList<Record> history);
    }

    public class FormatterMock implements Formatter {

        public ArrayList<Record> history;
        private String fakeString;

        public FormatterMock(String fakeString) {
            this.fakeString = fakeString;
        }

        @Override
        public String format(ArrayList<Record> history) {
            this.history = history;
            return fakeString;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Record record = (Record) o;
            return amount == record.amount && balance == record.balance && Objects.equals(date, record.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(date, amount, balance);
        }
    }
}
