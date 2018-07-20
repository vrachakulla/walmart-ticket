package com.walmart.coding.ticket.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.walmart.coding.ticket.model.Seat;
import com.walmart.coding.ticket.model.SeatHold;

public class TicketHelper {
	private static final String LIMITS_PROPERTIES = "limits.properties";
	private static final String ROWS_LIMIT = "rows.limit";
	private static final String SEATS_PER_ROW_LIMIT = "seats.per.row.limit";
	private static final String HOLD_TICKET_EXPIRY_TIME_IN_SECONDS = "hold.ticket.expiry.time.in.seconds";
	
	int rowsLimit = 5;
	int seatsPerRowLimit = 10;
	int loadSeatCounter = 1;
	int expiryDurationInSeconds = 0;

	private Map<Integer, List<Seat>> allSeats = new HashMap<Integer, List<Seat>>();
	private Map<Integer, SeatHold> heldSeatMap = new HashMap<Integer, SeatHold>();
	private List<Seat> reservedSeats = new ArrayList<Seat>();
	private List<Integer> rowNumbers = new ArrayList<Integer>();

	public void loadLimits() {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(LIMITS_PROPERTIES);
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
			rowsLimit = Integer.valueOf(properties.getProperty(ROWS_LIMIT));
			seatsPerRowLimit = Integer.valueOf(properties.getProperty(SEATS_PER_ROW_LIMIT));
			expiryDurationInSeconds = Integer.valueOf(properties.getProperty(HOLD_TICKET_EXPIRY_TIME_IN_SECONDS));
		} catch (IOException e) {
			// Do nothing, default rowNumber and seatsPerRowLimit would be retained.
		}
	}

	protected void loadSeats() {
		for (int rowNum = 1; rowNum <= rowsLimit; rowNum++) {
			addSeatPerEachRow(rowNum);
		}
	}
	
	protected void loadRowNumbers() {
		for (int rowNum = 1; rowNum <= rowsLimit; rowNum++) {
			rowNumbers.add(rowNum);
		}
	}

	
	private void addSeatPerEachRow(int rowNum) {
		for (int seatPerRow = 1; seatPerRow <= seatsPerRowLimit; seatPerRow++) {
			List<Seat> list = allSeats.get(rowNum);
			Seat seat = new Seat();
			seat.setRowNum(rowNum);
			seat.setSeatNumber(loadSeatCounter++);
			if (list == null) {
				list = new ArrayList<Seat>();
			}
			list.add(seat);
			allSeats.put(rowNum, list);
		}
	}

	
	public Map<Integer, List<Seat>> getAllSeats() {
		return allSeats;
	}

	
	public int getAllSeatsCount() {
		int allSeatsCount = 0;
		for (Entry<Integer, List<Seat>> entry : getAllSeats().entrySet()) {
			allSeatsCount += entry.getValue().size();
		}
		return allSeatsCount;
	}

	public List<Seat> getReservedSeats() {
		return reservedSeats;
	}

	public int getTotalHeldSeats() {
		int totalCount = 0;
		for (Entry<Integer, SeatHold> entry : getHeldSeatMap().entrySet()) {
			totalCount += entry.getValue().getSeats().size();
		}
		return totalCount;
	}

	public int getTotalReservedSeats() {
		return this.reservedSeats.size();
	}

	public Map<Integer, SeatHold> getHeldSeatMap() {
		return heldSeatMap;
	}

	public int getExpiryDurationInSeconds() {
		return expiryDurationInSeconds;
	}

	public List<Integer> getRowNumbers() {
		return rowNumbers;
	}

}
