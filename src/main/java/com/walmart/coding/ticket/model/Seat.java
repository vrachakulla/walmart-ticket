package com.walmart.coding.ticket.model;

public class Seat {

	private Integer seatNumber;
	private Integer seatHoldId;
	private Integer rowNum;
	
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

	public Integer getRowNum() {
		return rowNum;
	}

	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}

}
