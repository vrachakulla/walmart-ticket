package com.walmart.coding.ticket;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

import com.walmart.coding.ticket.model.Seat;
import com.walmart.coding.ticket.model.SeatHold;
import com.walmart.coding.ticket.service.TicketServiceImpl;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

	TicketServiceImpl ticketServiceImpl = new TicketServiceImpl();

	@Test
	public void testFindNumSeatsAvailable() {
		int numSeatsAvailable = ticketServiceImpl.numSeatsAvailable();
		Assert.assertEquals(50, numSeatsAvailable);
	}

	@Test
	public void testFindAndHoldSeats() {
		SeatHold seatHold = ticketServiceImpl.findAndHoldSeats(5, "test@test.com");
		Assert.assertNotNull(seatHold);
	}

	@Test
	public void testFindAndHoldSeatsNew() {
		String customerEmailId = "test@test.com";
		SeatHold seatHold1 = ticketServiceImpl.findAndHoldSeats(5, customerEmailId);
		SeatHold seatHold2 = ticketServiceImpl.findAndHoldSeats(5, customerEmailId);
		SeatHold seatHold3 = ticketServiceImpl.findAndHoldSeats(5, customerEmailId);
		Assert.assertNotNull(seatHold1);
		Assert.assertNotNull(seatHold2);
		Assert.assertNotNull(seatHold3);
		Assert.assertEquals(1, seatHold1.getSeats().get(0).getRowNum().intValue());
		Assert.assertEquals(1, seatHold2.getSeats().get(0).getRowNum().intValue());
		Assert.assertEquals(2, seatHold3.getSeats().get(0).getRowNum().intValue());
		Assert.assertNotNull(seatHold3);
		Assert.assertEquals(customerEmailId, seatHold1.getCustomerEmailId());
	}

	@Test
	public void testReserveSeats() {
		SeatHold seatHold = ticketServiceImpl.findAndHoldSeats(5, "test@test.com");
		Assert.assertNotNull(seatHold);

		String confirmationNumber = ticketServiceImpl.reserveSeats(seatHold.getSeatHoldId(), "test@test.com");
		Assert.assertNotNull(confirmationNumber);
		int numSeatsAvailable = ticketServiceImpl.numSeatsAvailable();
		Assert.assertEquals(45, numSeatsAvailable);
	}

	@Test
	public void testReserveSeatsWithFullCapacity() {
		String customerEmailId = "test@test.com";
		SeatHold seatHold1 = ticketServiceImpl.findAndHoldSeats(10, customerEmailId);
		SeatHold seatHold2 = ticketServiceImpl.findAndHoldSeats(10, customerEmailId);
		SeatHold seatHold3 = ticketServiceImpl.findAndHoldSeats(10, customerEmailId);
		SeatHold seatHold4 = ticketServiceImpl.findAndHoldSeats(2, customerEmailId);
		SeatHold seatHold5 = ticketServiceImpl.findAndHoldSeats(6, customerEmailId);
		SeatHold seatHold6 = ticketServiceImpl.findAndHoldSeats(6, customerEmailId);
		SeatHold seatHold7 = ticketServiceImpl.findAndHoldSeats(6, customerEmailId);
		Assert.assertNotNull(seatHold1);
		Assert.assertNotNull(seatHold2);
		Assert.assertNotNull(seatHold3);
		Assert.assertNotNull(seatHold4);
		Assert.assertNotNull(seatHold5);
		Assert.assertNotNull(seatHold6);
		Assert.assertNotNull(seatHold7);

		String confirmationNumber = ticketServiceImpl.reserveSeats(seatHold1.getSeatHoldId(), customerEmailId);
		Assert.assertNotNull(confirmationNumber);
		int numSeatsAvailable = ticketServiceImpl.numSeatsAvailable();
		Assert.assertEquals(0, numSeatsAvailable);
	}

	@Test
	public void testAllSeats() {
		Map<Integer, List<Seat>> allSeats = ticketServiceImpl.getTicketHelper().getAllSeats();
		Assert.assertEquals(5, allSeats.size());
		int totalSize = 0;
		for (Entry<Integer, List<Seat>> entry : allSeats.entrySet()) {
			totalSize += entry.getValue().size();
		}

		Assert.assertEquals(50, totalSize);
	}

	@Test
	public void testAvailableSeatsInFirstRow() {
		Integer rowNumber = ticketServiceImpl.getContiguousAvailableSeatsRowNumber(10);
		Assert.assertEquals(new Integer(1), rowNumber);
	}

	@Test
	public void testMoreThanAvailableSeatsInARow() {
		Integer rowNumber = ticketServiceImpl.getContiguousAvailableSeatsRowNumber(20);
		Assert.assertNull(rowNumber);
	}

	@Test
	public void testGetAvailableSeatsNew() {
		int seatsNew = ticketServiceImpl.getAvailableSeatsNew();
		Assert.assertEquals(50, seatsNew);
	}

	@Test
	public void testFindAndHoldSeats_ExpireThemAndCheckTheirAvailabilityInPool() {
		String customerEmailId = "test@test.com";
		SeatHold seatHold1 = ticketServiceImpl.findAndHoldSeats(5, customerEmailId);
		SeatHold seatHold2 = ticketServiceImpl.findAndHoldSeats(5, customerEmailId);
		SeatHold seatHold3 = ticketServiceImpl.findAndHoldSeats(5, customerEmailId);

		Assert.assertNotNull(seatHold1);
		Assert.assertNotNull(seatHold2);
		Assert.assertNotNull(seatHold3);
		Assert.assertEquals(1, seatHold1.getSeats().get(0).getRowNum().intValue());

		Assert.assertEquals(1, seatHold1.getSeats().get(0).getRowNum().intValue());
		Assert.assertEquals(1, seatHold2.getSeats().get(0).getRowNum().intValue());
		Assert.assertEquals(2, seatHold3.getSeats().get(0).getRowNum().intValue());

		seatHold2.setBookedTime(Instant.now().minusSeconds(700));
		String confirmation1 = ticketServiceImpl.reserveSeats(seatHold1.getSeatHoldId(), customerEmailId);
		Assert.assertNotNull(confirmation1);
		try {
			ticketServiceImpl.reserveSeats(seatHold2.getSeatHoldId(), customerEmailId);
		} catch (IllegalArgumentException argumentException) {
			Assert.assertEquals("hold time expired.", argumentException.getMessage());
		}
		String confirmation3 = ticketServiceImpl.reserveSeats(seatHold3.getSeatHoldId(), customerEmailId);
		Assert.assertNotNull(confirmation3);

		SeatHold seatHold4 = ticketServiceImpl.findAndHoldSeats(5, customerEmailId);
		Assert.assertNotNull(seatHold4);
		Assert.assertEquals(1, seatHold4.getSeats().get(0).getRowNum().intValue());

		String confirmation4 = ticketServiceImpl.reserveSeats(seatHold4.getSeatHoldId(), customerEmailId);
		Assert.assertNotNull(confirmation4);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testReserveSeats_WithExistingHoldId() {
		String customerEmailId = "test@test.com";
		SeatHold seatHold1 = ticketServiceImpl.findAndHoldSeats(5, customerEmailId);
		ticketServiceImpl.reserveSeats(seatHold1.getSeatHoldId(), customerEmailId);
		try {
			ticketServiceImpl.reserveSeats(seatHold1.getSeatHoldId(), customerEmailId);
		} catch (IllegalArgumentException argumentException) {
			Assert.assertEquals("Already SeatId is reserved.", argumentException.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateSeatHoldWithNoEmailId() {
		try {
			ticketServiceImpl.validateSeatHold(0, null);
		} catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals("customerEmailId is required.", illegalArgumentException.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateFindAndHoldSeatsWithZeroHoldId() {
		try {
			ticketServiceImpl.validateFindAndHoldSeatsNew(0);
		} catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals("Cannot book with 0 seats.", illegalArgumentException.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateFindAndHoldSeatsWith() {
		try {
			ticketServiceImpl.validateFindAndHoldSeatsNew(0);
		} catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals("Cannot book with 0 seats.", illegalArgumentException.getMessage());
		}
	}

	@Test
	public void testReserveSeatsWithNoSeatsAvailable() {
		String customerEmailId = "test@test.com";
		ticketServiceImpl.findAndHoldSeats(10, customerEmailId);
		ticketServiceImpl.findAndHoldSeats(10, customerEmailId);
		ticketServiceImpl.findAndHoldSeats(10, customerEmailId);
		ticketServiceImpl.findAndHoldSeats(10, customerEmailId);
		ticketServiceImpl.findAndHoldSeats(10, customerEmailId);
		try {
			ticketServiceImpl.findAndHoldSeats(10, customerEmailId);
		} catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals("All seats are reserved.", illegalArgumentException.getMessage());
		}

	}

	@Test
	public void testReserveSeatsMoreThanAvailableSeats() {
		String customerEmailId = "test@test.com";
		ticketServiceImpl.findAndHoldSeats(10, customerEmailId);
		ticketServiceImpl.findAndHoldSeats(10, customerEmailId);
		ticketServiceImpl.findAndHoldSeats(10, customerEmailId);
		ticketServiceImpl.findAndHoldSeats(5, customerEmailId);
		ticketServiceImpl.findAndHoldSeats(6, customerEmailId);
		try {
			ticketServiceImpl.findAndHoldSeats(10, customerEmailId);
		} catch (IllegalArgumentException illegalArgumentException) {
			Assert.assertEquals("available seats are less than requested seats.", illegalArgumentException.getMessage());
		}

	}

}
