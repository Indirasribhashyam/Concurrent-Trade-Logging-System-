import java.util.UUID;
import java.util.concurrent.*;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentTradeLoggingSystem {
    private static final ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<>(); //to avoid race conditions so that the log entries will store safely
    private static final String[] actions = {"Buying", "Selling"};

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5); //to handle multiple trades concurrently
        CountDownLatch validationLatch = new CountDownLatch(5); // to ensure all trades are validated before proceeding to next stages
        Semaphore settlementSemaphore = new Semaphore(2); // ensures only 2 trades are settled at the same time 

        // Example balances and shares for validation
        double[] buyerBalances = {5000.0, 1500.0, 8000.0, 200.0, 10000.0};
        int[] sellerShares = {50, 10, 100, 5, 200};

        for (int i = 0; i < 5; i++) {
            int orderId = i + 1;
            double price = randomPrice();
            int quantity = randomQuantity();
            double balance = buyerBalances[i];
            int shares = sellerShares[i];

            executor.submit(() -> {
                try {
                    //Order Placed
                    String orderIdStr = UUID.randomUUID().toString();
                    String action = actions[new Random().nextInt(actions.length)];
                    logEvent("Order Placed: " + orderIdStr + " - " + action);

                    //Order Validated
                    validateOrder(orderId, price, quantity, balance, shares, validationLatch);

                    //Order Executed
                    OrderBook.matchOrder();

                    //Trade Settled
                    settlementSemaphore.acquire();
                    logEvent("Trade Settled: Funds and shares transferred for Order " + orderId);
                    Thread.sleep(1000); // Simulate settlement time
                    settlementSemaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        validationLatch.await(); // Wait for all validations to complete
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS); // Wait for all tasks to finish

        // Print all logs
        System.out.println("\n=== Trade Logs ===");
        printLogs();
    }

    //Order Validated
    private static void validateOrder(int orderId, double price, int quantity, double balance, int shares, CountDownLatch latch) {
        boolean buyerValid = balance >= price * quantity;
        boolean sellerValid = shares >= quantity;

        String buyerResult = buyerValid ? "Buyer has sufficient funds." : "Buyer validation failed: Insufficient funds.";
        String sellerResult = sellerValid ? "Seller has sufficient shares." : "Seller validation failed: Not enough shares.";

        logEvent("Order " + orderId + " Validated -> " + buyerResult + " ; " + sellerResult);
        latch.countDown();
    }

    //Order Executed
    static class OrderBook {
        public static synchronized void matchOrder() {
            logEvent("Order Executed: Matched buy and sell orders");
        }
    }

    // Helper methods
    private static void logEvent(String event) {
        logQueue.add(event + " by " + Thread.currentThread().getName());
    }

    private static void printLogs() {
        logQueue.forEach(System.out::println);
    }

    private static double randomPrice() {
        return 100 + new Random().nextDouble() * 900; // Random price between 100 and 1000
    }

    private static int randomQuantity() {
        return 1 + new Random().nextInt(100); // Random quantity between 1 and 100
    }
}

