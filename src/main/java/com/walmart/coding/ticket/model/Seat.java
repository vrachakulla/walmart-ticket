package com.walmart.coding.ticket.model;

public class Seat {

	private RowNumber rowNumber;
	private Integer seatNumber;
	// private SeatHold seatHold;
	private Integer seatHoldId;

	public RowNumber getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(RowNumber rowNumber) {
		this.rowNumber = rowNumber;
	}

	public Integer getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(Integer seatNumber) {
		this.seatNumber = seatNumber;
	}

	public Integer getSeatHoldId() {
		return seatHoldId;
	}

	public void setSeatHoldId(Integer seatHoldId) {
		this.seatHoldId = seatHoldId;
	}

}
