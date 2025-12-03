import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.*;

public class ConsumerProducer {

    private static final Buffer buffer = new Buffer();

    public static void main(String [] args){
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(new ProducerTask());
        executor.execute(new ConsumerTask());
        executor.shutdown();

        while (!executor.isTerminated()){
        }


    }
    public static class ProducerTask implements Runnable{
        @Override
        public void run(){
            try {
                int i = 1;
                while (true){
                    System.out.println("Producer writes: " + i);
                    buffer.write(i++);
                    Thread.sleep((int)(Math.random() * 1000));
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static class ConsumerTask implements Runnable{
        @Override
        public void run(){
            try {
                while (true){
                    final int read = buffer.read();
                    System.out.println("Producer reads: " + read);

                    Thread.sleep((int)(Math.random() * 1000));
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static class Buffer{
        private final java.util.LinkedList<Integer> queue =  new java.util.LinkedList<>();
        private static final int CAPACITY = 1;
        private  static final Lock lock = new ReentrantLock();

        private final Condition notEmpty = lock.newCondition();
        private final Condition notFull = lock.newCondition();

        public void write(int value){
            lock.lock();
            try {
                while (queue.size() == CAPACITY){
                    System.out.println("Wait for the notFull Condition!");
                    notFull.await();
                }
                queue.offer(value);
                notEmpty.signal();
            } catch (Exception e) {
               e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }

        public int read(){
            lock.lock();
            int value = 0;
            try {
                while (queue.isEmpty()){
                    System.out.println("Wait for the notEmpty Condition!");
                    notEmpty.await();
                }
              value = queue.remove();
                notFull.signal();
            }catch (InterruptedException e){
                e.printStackTrace();
            }finally {
                lock.unlock();

            }
            return value;
        }
    }
}
