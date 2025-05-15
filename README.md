# Concurrent-Trade-Logging-System-
This project simulates a simplified financial trading system where trades pass through four sequential stages: Order Placed, Order Validated, Order Executed, and Trade Settled. The system is designed to handle multiple trades concurrently using raw threads, ensuring that events for each trade are logged in the proper order even when processed simultaneously. 

# Process Overview:

This project simulates a multi-threaded trade life cycle in a simplified trading system where each trade passes through four stages:

1.Order Placed
2.Order Validated
3.Order Executed
4.Trade Settled

Each stage is implemented in a thread-safe and concurrent manner, ensuring that multiple trades can be processed in parallel while preserving log order for individual trades.

# Stages in Trade Lifecycle:

1️⃣ Order Placed
* A trader places a buy/sell order.
* A unique order ID is generated.
* Action (buy/sell) is randomly chosen.
* Event is logged.

2️⃣ Order Validated
* Buy orders: Check if sufficient funds.
* Sell orders: Check if enough shares.
* Log whether validation is successful.
* CountDownLatch ensures all validations complete before moving forward.

3️⃣ Order Executed
* Synchronized matchOrder() method ensures thread-safe pairing.
* Order matching is simulated and logged.

4️⃣ Trade Settled
* Semaphore limits maximum of 2 concurrent settlements.
* Settlement is simulated with a delay and logged.


# Challenges:

1) Race condition: If two threads are updating a trade balance, one thread's update might overwrite the other’s changes.
Solution: synchronized locks the method, allowing only one thread at a time to update the balance.

2) Multiple trades settling at a time: Multiple threads could access shared resources (like settling trades) simultaneously without limits.
Solution: Use a Semaphore to control access _ allowing only a set number of threads at a time

3) Trade logs writing simultaneosuly: If multiple threads log messages at the same time using a normal ArrayList or LinkedList, data can get corrupted.
Solution: Concurrent collections use lock-free algorithms, so logs are safely added by multiple threads without blocking each other.

4) Improper validation: Executing trades before validation finishes could cause invalid trades.
Trades happen before checks, leading to invalid or incomplete trades.
Solution: The latch makes threads wait until validation completes before moving forward
