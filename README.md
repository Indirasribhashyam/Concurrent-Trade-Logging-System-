# Concurrent-Trade-Logging-System-
This project simulates a simplified financial trading system where trades pass through four sequential stages: Order Placed, Order Validated, Order Executed, and Trade Settled. The system is designed to handle multiple trades concurrently using raw threads, ensuring that events for each trade are logged in the proper order even when processed simultaneously. 
Challenges:

1) Race condition: If two threads are updating a trade balance, one thread's update might overwrite the other’s changes.
Solution: synchronized locks the method, allowing only one thread at a time to update the balance.

2) Multiple trades settling at a time: Multiple threads could access shared resources (like settling trades) simultaneously without limits.
Solution: Use a Semaphore to control access _ allowing only a set number of threads at a time

3)Trade logs writing simultaneosuly: If multiple threads log messages at the same time using a normal ArrayList or LinkedList, data can get corrupted.
Solution: Concurrent collections use lock-free algorithms, so logs are safely added by multiple threads without blocking each other.

4) Improper validation: Executing trades before validation finishes could cause invalid trades.
Trades happen before checks, leading to invalid or incomplete trades.
Solution: The latch makes threads wait until validation completes before moving forward
