/*
 * Copyright(C) 2007-2013 National Institute of Informatics, All rights reserved.
 */
package domain.reservation;

import java.util.List;

/**
 * Interface for accessing to Reservation Data Object<br>
 * 
 */
public interface ReservationDao {

	public abstract Reservation getReservation(String reservationNumber)
			throws ReservationException;

	public abstract void updateReservation(Reservation reservation) throws ReservationException;

	public abstract void createReservation(Reservation reservation) throws ReservationException;

	public abstract void cancelReservation(Reservation reservation) throws ReservationException; // 追加

	public abstract List<Reservation> getAllReservations() throws ReservationException;	//追加
}
