package com.walmart.coding.ticket.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SeatHold {
	private Integer seatHoldId;
	private String customerEmailId;
	private Instant bookedTime;
	private List<Seat> seats = new ArrayList<Seat>();

	public Integer getSeatHoldId() {
		return seatHoldId;
	}

	public void setSeatHoldId(Integer seatHoldId) {
		this.seatHoldId = seatHoldId;
	}

	public Instant getBookedTime() {
		return bookedTime;
	}

	public void setBookedTime(Instant bookedTime) {
		this.bookedTime = bookedTime;
	}

	public List<Seat> getSeats() {
		return seats;
	}

	public void setSeats(List<Seat> seats) {
		this.seats = seats;
	}

	public SeatHold addSeat(Seat seat) {
		this.seats.add(seat);
		return this;

	}

	public void setCustomerEmailId(String customerEmailId) {
		this.customerEmailId = customerEmailId;
	}

	public String getCustomerEmailId() {
		return customerEmailId;
	}

}
