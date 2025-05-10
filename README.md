# Concurrent-Trade-Logging-System-
This project simulates a simplified financial trading system where trades pass through four sequential stages: Order Placed, Order Validated, Order Executed, and Trade Settled. The system is designed to handle multiple trades concurrently using raw threads, ensuring that events for each trade are logged in the proper order even when processed simultaneously. 

Trade process overview:
1.	Order Placed:
The trade process begins when a trader decides to buy or sell an asset. An order is submitted to the trading system.
Key actions:
1.The system receives the order details (e.g., asset, quantity, price).
2.A unique order identifier is assigned.
3.An initial log entry is created to capture the order submission event.

2.	Order Validated:
The system verifies that the trader has sufficient resources to execute the trade.
Key actions:
1. For a buy order, the system checks if the trader’s account has enough funds.
2. For a sell order, the system confirms that the trader holds the requisite number of shares.
3. A validation log entry is generated, noting whether the order is valid.

3.	Order Executed:
At this stage, the system’s matching engine pairs the order with a counter order.
Key actions:
1. The system scans the order book to locate a matching order (e.g., a corresponding sell order for a     buy order).
2. Once a match is found, the trade is executed, meaning the buyer and seller are paired.
3. An execution log entry is created, detailing the matched orders and confirming the trade execution.

4.	Trade Settled:
This final step finalizes the trade by transferring funds and shares between the buyer and seller.
Key actions:
1. The buyer’s account is debited while the seller’s account is credited with the appropriate amounts.
2. Asset ownership is updated to reflect the new positions.
3. A settlement log entry is created, confirming that the trade has been fully completed.

Data Flow Overview
1.	Trade Initiation:
A trade process thread begins the lifecycle by generating an "Order Placed" event.
2.	Validation:
The same thread proceeds to validate the order, logging the "Order Validated" event.
3.	Execution:
Once validation is successful, the trade is matched and the "Order Executed" event is logged.
4.	Settlement:
Finally, the trade process thread logs the "Trade Settled" event, and the dedicated logger thread processes all log entries in the order they were generated.

Challenges:

1) Race condition: If two threads are updating a trade balance, one thread's update might overwrite the other’s changes.
Solution: synchronized locks the method, allowing only one thread at a time to update the balance.

2) Multiple trades settling at a time: Multiple threads could access shared resources (like settling trades) simultaneously without limits.
Solution: Use a Semaphore to control access _ allowing only a set number of threads at a time

3) Trade logs writing simultaneosuly: If multiple threads log messages at the same time using a normal ArrayList or LinkedList, data can get corrupted.
Solution: Concurrent collections use lock-free algorithms, so logs are safely added by multiple threads without blocking each other.

4) Improper validation: Executing trades before validation finishes could cause invalid trades.
Trades happen before checks, leading to invalid or incomplete trades.
Solution: The latch makes threads wait until validation completes before moving forward
