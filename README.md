## walmart-ticket
Project cannot be run standalone. Some other services would be consuming or a web application would create the instance. 

### Assumptions
1. Booking is done based on the best contiguous seats available in each row. The order of preference is from 1..5 as 1 is closer to the stage and people would like to be closer to the stage.
2. Seat is an object which holds seat number, row number and seatHoldId if any.
3. Initially a map is loaded with all the seats with its seat number and row number.
4. A TicketHelper class is created to populate all the seats in the map and Row numbers in a list.
5. RowNumbers are loaded in a list to preserve the order of preference.
6. Since there are no checked exceptions and due to Time constraint I am throwing a generic IllegalArguementException. This I could have tweaked bit more but couldn't do it.
7. The capacity of the theater/event can be configured. Number of Rows in theater and seats per Row are stored in limits.properties file. This means tomorrow if the capacity of the theater increased all we would require is to change them in the properties file. 
8. The test cases as of now would be running against the fixed number of seats and rows in the theater.
9. There are couple of Maps created for storing the seats, heldSeats.

### Instructions
#### Building
Assuming Java and Git are setup on device:

```
git clone https://github.com/vrachakulla/walmart-ticket.git
cd walmart-ticket
mvn clean install
```
