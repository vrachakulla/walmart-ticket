package com.walmart.coding.ticket.model;

import java.util.ArrayList;
import java.util.List;

public enum RowNumber {

	FIRST_ROW(1, 1), SECOND_ROW(2, 2), THIRD_ROW(3, 3), FOURTH_ROW(4, 4), FIFTH_ROW(5, 5);
	private int rowNumber;
	private int orderOfPreference;

	public int getRowNumber() {
		return rowNumber;
	}

	private RowNumber(int rowNumber, int orderOfPreference) {
		this.rowNumber = rowNumber;
		this.orderOfPreference = orderOfPreference;
	};

	public static RowNumber fromNumber(int number) {
		if (number == 0) {
			return null;
		}

		for (RowNumber rowNumber : values()) {
			if (rowNumber.rowNumber == number) {
				return rowNumber;
			}
		}
		return null;
	}

	public static List<RowNumber> getRowNumOrder() {
		List<RowNumber> rowNumbers = new ArrayList<RowNumber>();
		rowNumbers.add(RowNumber.FIRST_ROW);
		rowNumbers.add(RowNumber.SECOND_ROW);
		rowNumbers.add(RowNumber.THIRD_ROW);
		rowNumbers.add(RowNumber.FOURTH_ROW);
		rowNumbers.add(RowNumber.FIFTH_ROW);
		return rowNumbers;

	}

	public int getOrderOfPreference() {
		return orderOfPreference;
	}

}
