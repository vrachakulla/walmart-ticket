package com.walmart.coding.ticket.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.walmart.coding.ticket.model.Seat;
import com.walmart.coding.ticket.model.SeatHold;

public class TicketServiceImpl implements TicketService {

	private static final String CHAR_LIST = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

	TicketHelper ticketHelper = new TicketHelper();
	int rowsLimit = 0;
	int seatsPerRowLimit = 0;

	public TicketServiceImpl() {
		ticketHelper.loadLimits();
		ticketHelper.loadRowNumbers();
		ticketHelper.loadSeats();
	}

	public synchronized int numSeatsAvailable() {
		return getAvailableSeatsNew();
	}

	public int getAvailableSeatsNew() {
		return ticketHelper.getAllSeatsCount() - ticketHelper.getTotalHeldSeats() - ticketHelper.getTotalReservedSeats();
	}

	public synchronized SeatHold findAndHoldSeats(int numSeats, String customerEmailId) {
		validateFindAndHoldSeatsNew(numSeats);
		SeatHold hold = getContiguousSeat(numSeats, customerEmailId);
		if (hold != null) {
			ticketHelper.getHeldSeatMap().put(hold.getSeatHoldId(), hold);
			return hold;
		}

		SeatHold seatHold = createSeatHoldForNonContiguousSeats(numSeats, customerEmailId);
		ticketHelper.getHeldSeatMap().put(seatHold.getSeatHoldId(), seatHold);
		return seatHold;
	}

	private SeatHold createSeatHoldForNonContiguousSeats(int numSeats, String customerEmailId) {
		SeatHold seatHold = constructSeatHold(numSeats, customerEmailId);
		int counter = 0;
		for (Integer rowNumber : ticketHelper.getRowNumbers()) {
			List<Seat> seats = ticketHelper.getAllSeats().get(rowNumber);
			for (Seat seat : seats) {
				if (counter == numSeats) {
					break;
				}
				if (seat.getSeatHoldId() != null) {
					continue;
				}
				seat.setSeatHoldId(seatHold.getSeatHoldId());
				seatHold.addSeat(seat);
				counter++;
			}
		}
		return seatHold;
	}

	public SeatHold getContiguousSeat(int numSeats, String customerEmailId) {
		Integer seatRowNumber = getContiguousAvailableSeatsRowNumber(numSeats);
		if (seatRowNumber == null) {
			return null;
		}
		SeatHold seatHold = constructSeatHold(numSeats, customerEmailId);
		int counter = 0;
		List<Seat> seats = ticketHelper.getAllSeats().get(seatRowNumber);
		for (Seat seat : seats) {
			if (counter == numSeats) {
				break;
			}
			if (seat.getSeatHoldId() != null) {
				continue;
			}
			seat.setSeatHoldId(seatHold.getSeatHoldId());
			seatHold.addSeat(seat);
			counter++;
		}
		ticketHelper.getHeldSeatMap().put(seatHold.getSeatHoldId(), seatHold);
		return seatHold;
	}

	public void validateFindAndHoldSeatsNew(int numSeats) {
		if (numSeats == 0) {
			throw new IllegalArgumentException("Cannot book with 0 seats.");
		}

		if (getAvailableSeatsNew() == 0) {
			throw new IllegalArgumentException("All seats are reserved.");
		}

		if (getAvailableSeatsNew() < numSeats) {
			throw new IllegalArgumentException("available seats are less than requested seats.");
		}
	}

	public SeatHold constructSeatHold(int numSeats, String customerEmailId) {
		SeatHold seatHold = new SeatHold();
		seatHold.setBookedTime(Instant.now());
		seatHold.setCustomerEmailId(customerEmailId);
		seatHold.setSeatHoldId(generateSeatHoldId());
		return seatHold;
	}

	public synchronized String reserveSeats(int seatHoldId, String customerEmail) {
		validateSeatHold(seatHoldId, customerEmail);
		SeatHold seatHold = ticketHelper.getHeldSeatMap().get(seatHoldId);
		ticketHelper.getReservedSeats().addAll(seatHold.getSeats());
		ticketHelper.getHeldSeatMap().remove(seatHoldId);
		return generateConfirmationCode();
	}

	public void validateSeatHold(int seatHoldId, String customerEmailId) {
		checkIfNoEmailProvided(customerEmailId);
		checkIfAlreadySeatIdIsReserved(seatHoldId);
		SeatHold seatHold = ticketHelper.getHeldSeatMap().get(seatHoldId);
		if (isNotExpiredNew(seatHold)) {
			return;
		}
		clearSeatHoldIdForExpiredSeats(seatHold);
		clearFromHeldSeatMap(seatHoldId, seatHold);
		throw new IllegalArgumentException("hold time expired.");

	}

	private void checkIfNoEmailProvided(String customerEmailId) {
		if (isBlank(customerEmailId)) {
			throw new IllegalArgumentException("customerEmailId is required.");
		}
	}

	private void checkIfAlreadySeatIdIsReserved(int seatHoldId) {
		for (Seat existingSeat : ticketHelper.getReservedSeats()) {
			if (existingSeat.getSeatHoldId().equals(seatHoldId)) {
				throw new IllegalArgumentException("Already SeatId is reserved.");
			}
		}
	}

	private void clearFromHeldSeatMap(int seatHoldId, SeatHold seatHold) {
		seatHold.setSeatHoldId(null);
		seatHold.setBookedTime(null);
		seatHold.setCustomerEmailId(null);
		seatHold.setSeats(new ArrayList<Seat>());
		ticketHelper.getHeldSeatMap().remove(seatHoldId);
	}

	private void clearSeatHoldIdForExpiredSeats(SeatHold seatHold) {
		Map<Integer, List<Seat>> allSeats = ticketHelper.getAllSeats();
		for (Entry<Integer, List<Seat>> entry : allSeats.entrySet()) {
			for (Seat seat : entry.getValue()) {
				if (seat.getSeatHoldId() != null && seat.getSeatHoldId().equals(seatHold.getSeatHoldId())) {
					seat.setSeatHoldId(null);
				}
			}
		}
	}

	public boolean isNotExpiredNew(SeatHold seatHold) {
		return Instant.now().isBefore(seatHold.getBookedTime().plusSeconds(ticketHelper.getExpiryDurationInSeconds()));
	}

	private static boolean isBlank(String string) {
		return string == null || string.length() == 0 || string.trim().length() == 0;
	}

	private String generateConfirmationCode() {
		StringBuilder confirmationCode = new StringBuilder();
		for (int i = 0; i < 20; i++) {
			int randonmLength = new Random().nextInt(CHAR_LIST.length());
			int randomCharacter = (randonmLength == 0) ? randonmLength : randonmLength - 1;
			confirmationCode.append(CHAR_LIST.charAt(randomCharacter));
		}
		return confirmationCode.toString();
	}

	private int generateSeatHoldId() {
		return new Random().nextInt(1000 - 500) + 500;
	}

	public Integer getContiguousAvailableSeatsRowNumber(int numSeats) {
		int availableCount = 0;
		for (Integer rowNumber : ticketHelper.getRowNumbers()) {
			for (Seat seat : ticketHelper.getAllSeats().get(rowNumber)) {
				if (seat.getSeatHoldId() == null) {
					availableCount++;
				}
				if (availableCount == numSeats) {
					return rowNumber;
				}
			}
			// Reset the counter for each row.
			availableCount = 0;
		}
		return null;
	}

	public TicketHelper getTicketHelper() {
		return ticketHelper;
	}

}
