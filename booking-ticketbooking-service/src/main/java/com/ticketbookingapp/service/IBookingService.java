package com.ticketbookingapp.service;

import java.util.List;

import com.ticketbookingapp.model.BookingDto;
import com.ticketbookingapp.model.BookingStatus;


public interface IBookingService {
	void createBooking(BookingDto bookingDto,String jwtToken);
	BookingDto getByBookingId(int bookingId);
	List<BookingDto> getBookingsByUser(int userId);
	List<BookingDto> getBookingsByShow(int showId) ;
	void updateBooking(BookingDto bookingDto);
	void deleteBooking(int bookingId);
	List<BookingDto> getByBookingStatus(BookingStatus bookingStatus);
	
}
