package com.walmart.coding.ticket.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.walmart.coding.ticket.model.RowNumber;
import com.walmart.coding.ticket.model.Seat;
import com.walmart.coding.ticket.model.SeatHold;

public class TicketHelper {
	Map<RowNumber, List<Seat>> allSeats = new HashMap<RowNumber, List<Seat>>();
	int rowsLimit = 5;
	int seatsPerRowLimit = 10;
	int loadSeatCounter = 1;
	int expiryDurationInSeconds = 0;
	List<Seat> heldSeats = new ArrayList<Seat>();

	private Map<Integer, SeatHold> heldSeatMap = new HashMap<Integer, SeatHold>();
	List<Seat> reservedSeats = new ArrayList<Seat>();

	public void loadLimits() {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("\\resources\\limits.properties");
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
			rowsLimit = Integer.valueOf(properties.getProperty("rows.limit"));
			seatsPerRowLimit = Integer.valueOf(properties.getProperty("seats.per.row.limit"));
			expiryDurationInSeconds = Integer.valueOf(properties.getProperty("hold.ticket.expiry.time.in.seconds"));
		} catch (IOException e) {

		}
	}

	protected void loadSeats() {
		for (int rowNum = 1; rowNum <= rowsLimit; rowNum++) {
			addSeatPerEachRow(rowNum);
		}
	}

	private void addSeatPerEachRow(int rowNum) {
		for (int seatPerRow = 1; seatPerRow <= seatsPerRowLimit; seatPerRow++) {
			RowNumber rowNumber = RowNumber.fromNumber(rowNum);
			List<Seat> list = allSeats.get(rowNumber);
			Seat seat = new Seat();
			seat.setRowNumber(rowNumber);
			seat.setSeatNumber(loadSeatCounter++);
			if (list == null) {
				list = new ArrayList<Seat>();
			}
			list.add(seat);
			allSeats.put(rowNumber, list);
		}
	}

	public Map<RowNumber, List<Seat>> getAllSeats() {
		return allSeats;
	}

	public int getAllSeatsCount() {
		int allSeatsCount = 0;
		for (Entry<RowNumber, List<Seat>> entry : getAllSeats().entrySet()) {
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

}
