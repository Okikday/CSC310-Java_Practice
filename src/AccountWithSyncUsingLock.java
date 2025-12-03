import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class AccountWithSyncUsingLock {
    static final Account account = new Account();

    public static void main(String [] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        for(int i = 0; i < 100; i++){
            executor.execute(new AddAPennyTask());
        }

        executor.shutdown();
        while (!executor.isTerminated()) {

        }

        System.out.println("\nDone with execution!");
    }
    public static class AddAPennyTask implements  Runnable{

        @Override
        public void run() {
            account.deposit(1);
        }
    }
    public static class Account{
        private  final Lock lock = new ReentrantLock();
        private int balance = 0;

        public int getBalance() {
            return balance;
        }

        public synchronized void deposit(int amount){
            lock.lock();


            try {
                int newBalance = balance + amount;
                Thread.sleep(5);
                balance = newBalance;
            } catch (InterruptedException e) {
                System.out.println("An exception occurred");

            } finally {
                lock.unlock();
            }

            System.out.println("Added deposit: " + balance);
        }

    }
}


