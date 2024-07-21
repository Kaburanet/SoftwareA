/*
 * Copyright(C) 2007-2013 National Institute of Informatics, All rights reserved.
 */
package app.cui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Map;

import domain.room.RoomException;
import util.DateUtil;
import app.AppException;
import app.checkin.CheckInRoomForm;
import app.checkout.CheckOutRoomForm;
import app.reservation.ReserveRoomControl;
import app.reservation.ReserveRoomForm;
import domain.reservation.Reservation;
import domain.reservation.ReservationException;
import domain.reservation.ReservationManager;
import domain.room.Room;
import domain.room.RoomSqlDao;

/**
 * CUI class for Hotel Reservation Systems
 * 
 */
public class CUI {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private BufferedReader reader;

	CUI() {
		reader = new BufferedReader(new InputStreamReader(System.in));
	}

	private void execute() throws IOException {
		try {
			while (true) {
				int selectMenu;
				System.out.println("");
				System.out.println("Menu");
				System.out.println("1: Reservation");
				System.out.println("2: Check-in");
				System.out.println("3: Check-out");
				System.out.println("4: Empty room List");
				System.out.println("5: Reservation List");
				System.out.println("6: Cancel Reservation");
				System.out.println("9: End");
				System.out.print("> ");

				try {
					String menu = reader.readLine();
					selectMenu = Integer.parseInt(menu);
				} catch (NumberFormatException e) {
					selectMenu = 8;
				}

				if (selectMenu == 9) {
					break;
				}

				switch (selectMenu) {
					case 1:
						reserveRoom();
						break;
					case 2:
						checkInRoom();
						break;
					case 3:
						checkOutRoom();
						break;
					case 4:
						showEmptyRoomList();
						break;
					case 5:
						showReservationList();
						break;
					case 6:
						cancelReservation();
						break;
				}
			}
			System.out.println("Ended");
		} catch (AppException e) {
			System.err.println("AppException Error");
			System.err.println(e.getFormattedDetailMessages(LINE_SEPARATOR));
		} catch (RoomException e) {
			System.err.println("RoomException Error");
			System.err.println(e.getMessage());
		} finally {
			reader.close();
		}
	}

	private void reserveRoom() throws IOException, AppException {
		System.out.println("Input arrival date in the form of yyyy/mm/dd");
		System.out.print("> ");

		String dateStr = reader.readLine();

		// Validate input
		Date stayingDate = DateUtil.convertToDate(dateStr);
		if (stayingDate == null) {
			System.out.println("Invalid input");
			return;
		}

		ReserveRoomForm reserveRoomForm = new ReserveRoomForm();
		reserveRoomForm.setStayingDate(stayingDate);
		String reservationNumber = reserveRoomForm.submitReservation();

		System.out.println("Reservation has been completed.");
		System.out.println("Arrival (staying) date is " + DateUtil.convertToString(stayingDate) + ".");
		System.out.println("Reservation number is " + reservationNumber + ".");
	}

	private void checkInRoom() throws IOException, AppException {
		System.out.println("Input reservation number");
		System.out.print("> ");

		String reservationNumber = reader.readLine();

		if (reservationNumber == null || reservationNumber.length() == 0) {
			System.out.println("Invalid reservation number");
			return;
		}

		CheckInRoomForm checkInRoomForm = new CheckInRoomForm();
		checkInRoomForm.setReservationNumber(reservationNumber);

		String roomNumber = checkInRoomForm.checkIn();
		System.out.println("Check-in has been completed.");
		System.out.println("Room number is " + roomNumber + ".");

	}

	private void checkOutRoom() throws IOException, AppException {
		System.out.println("Input room number");
		System.out.print("> ");

		String roomNumber = reader.readLine();

		if (roomNumber == null || roomNumber.length() == 0) {
			System.out.println("Invalid room number");
			return;
		}

		CheckOutRoomForm checkoutRoomForm = new CheckOutRoomForm();
		checkoutRoomForm.setRoomNumber(roomNumber);
		checkoutRoomForm.checkOut();
		System.out.println("Check-out has been completed.");
	}

	public void showEmptyRoomList() throws IOException, AppException, RoomException {
		System.out.println("Avalable Room List");
		RoomSqlDao roomSqlDao = new RoomSqlDao();
		List<Room> emptyRoomList = roomSqlDao.getEmptyRooms();
		for (int i = 0; i < emptyRoomList.size(); i++) {
			System.out.println(emptyRoomList.get(i).getRoomNumber());
		}
	}

	public void showReservationList() throws IOException, AppException, RoomException {
		try {
			ReservationManager reservationManager = new ReservationManager();
			List<Reservation> reservations = reservationManager.getActiveReservations();
	
			if (reservations.isEmpty()) {
				System.out.println("No active reservations found.");
			} else {
				System.out.println("Active Reservation List:");
				for (Reservation reservation : reservations) {
					System.out.println("Reservation Number: " + reservation.getReservationNumber()
							+ ", Arrival Date: " + DateUtil.convertToString(reservation.getStayingDate())
							+ ", Status: " + reservation.getStatus());
				}
			}
		} catch (ReservationException e) {
			throw new AppException("Failed to retrieve reservations", e);
		}
	}

	public void cancelReservation() throws IOException, AppException {
		System.out.println("Input reservation number to cancel:");
		System.out.print("> ");

		String reservationNumber = reader.readLine();

		// Validate input
		if (reservationNumber == null || reservationNumber.isEmpty()) {
			System.out.println("Invalid input");
			return;
		}

		ReserveRoomControl reserveRoomControl = new ReserveRoomControl();
		reserveRoomControl.cancelReservation(reservationNumber);

		System.out.println("Reservation has been canceled.");
		System.out.println("Reservation number was " + reservationNumber + ".");
	}

	public static void main(String[] args) throws Exception {
		CUI cui = new CUI();
		cui.execute();
	}
}
