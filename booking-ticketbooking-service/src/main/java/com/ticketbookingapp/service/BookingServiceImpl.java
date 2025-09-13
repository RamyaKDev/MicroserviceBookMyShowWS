package com.ticketbookingapp.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import com.ticketbookingapp.exception.BookingNotFoundException;
import com.ticketbookingapp.exception.SeatNotFoundException;
import com.ticketbookingapp.exception.ShowNotFoundException;
import com.ticketbookingapp.model.Booking;
import com.ticketbookingapp.model.BookingDto;
import com.ticketbookingapp.model.BookingStatus;
import com.ticketbookingapp.model.ShowDto;
import com.ticketbookingapp.model.UserDto;
import com.ticketbookingapp.repository.IBookingRepository;

@Service
public class BookingServiceImpl implements IBookingService{
	
	private final ModelMapper mapper;
	
	private  final IBookingRepository bookingRepository;
    private  final RestTemplate restTemplate;

    private final String USERBASEURL = "http://user-service/users-service/v1/users/userId/";
    
    private final String SHOWBASEURL = "http://show-service/shows-service/v1/shows/showId/";

	public BookingServiceImpl(ModelMapper mapper, IBookingRepository bookingRepository, RestTemplate restTemplate) {
		super();
		this.mapper = mapper;
		this.bookingRepository = bookingRepository;
		this.restTemplate = restTemplate;
	}

	@Override
	public void createBooking(BookingDto bookingDto,String jwtToken) {
//		 // Step 1: Get already booked seats for this show
//        List<String> bookedSeats = getBookedSeats(bookingDto.getShowId());
//
//        // Step 2: Validate requested seats
//        for (String seat : bookingDto.getSeatNumbers()) {
//            if (bookedSeats.contains(seat)) {
//                throw new ShowNotFoundException("Seat " + seat + " is already booked!");
//            }
//        } 
		
     // Step 3: Fetch show details from Show Service
        
        ShowDto show = getShowDetails(bookingDto.getShowId(), jwtToken);
       
        if (show == null) {
            throw new ShowNotFoundException("Invalid showId: ");
        }
        
        // Step: Check user requested seats are available for booking for the show
        if(!isSeatAvailable(show,bookingDto.getNumberOfSeats())) {
        	throw new ShowNotFoundException("Not enough seats are available ");
        }
        
        //check seatNumbers[A1,A2,A3] and noOfSeats[3] are equal
        //if seatNumbers[A1,A2] and noOfSeats[3] will not work
        if(bookingDto.getSeatNumbers().size() != bookingDto.getNumberOfSeats()) {
        	throw new ShowNotFoundException("Seat number and NoOfSeats must be equal");
        }
        
        //checking duplicate seats
        //user want to book A1 but it is already booked then throw exception
        validateDuplicateSeats(show,bookingDto.getSeatNumbers());
        
        //Step 4:  Call User Service to validate user
        
        UserDto user = getUserDetails(bookingDto.getUserId(), jwtToken);

        if (user == null) {
            throw new ShowNotFoundException ("Invalid userId: ");
        }
    
        //Step 5:   Calculate price
       double price=show.getPrice() * bookingDto.getNumberOfSeats();
        
       //Step 6: setting values to booking class
        Booking booking=new Booking();
        booking.setUser(user);        
        booking.setShow(show);
        booking.setNumberOfSeats(bookingDto.getNumberOfSeats());
        booking.setBookingTime(LocalDateTime.now());
        booking.setTotalPrice(price);
        booking.setBookingStatus(BookingStatus.PENDING);
        
		 bookingRepository.save(booking);
		
	}
	
	//check already occupied seats A1,A2,A3 again user requested to book
	//If again user wants to book A1,A2 throw exception
	 public void validateDuplicateSeats(ShowDto showDto, List<String> seatNumbers) {
	
     // Step 1: Get bookings for this show from DB
     List<Booking> bookings = bookingRepository.findByShowId(showDto.getShowId());
  
     // Step 2: Filter seat numbers already booked
     Set<String> occupiedSeats = bookings.stream()
    		 			.filter(b-> b.getBookingStatus() != BookingStatus.CANCELLED)
    		 			.flatMap(b->b.getSeatNumbers().stream())
    		 			.collect(Collectors.toSet());
     
    // Step 3: check duplicate seats
     List<String> duplicateSeats= seatNumbers.stream()
    		 .filter(occupiedSeats::contains)
    		 .collect(Collectors.toList());
     
     if(!duplicateSeats.isEmpty()) {
    	 throw new ShowNotFoundException("Seat  is already booked!");
     }

	}

	//checking whether seats are available
    //getting total no of seats from show. for example it is 100
    //user wants to book 4 tickets.. already 90 tickets are booked.. here checking available seats
    //100-90=10 left so 10>4 condition true so user can book
    //if user wants to book 15 10>15 condition false so he cant book.
	 private boolean isSeatAvailable(ShowDto showDto, int numberOfSeats) {
		 // Step 1: Get bookings for this show from DB
	        List<Booking> bookings = bookingRepository.findByShowId(showDto.getShowId());
	     
	        // Step 2: Extract seat numbers already booked
	        int bookedSeats = bookings.stream()
	        		.filter(booking->booking.getBookingStatus() != BookingStatus.CANCELLED )
	                .mapToInt(Booking::getNumberOfSeats)
	                .sum(); 
	        
	        return (showDto.getTotalNoOfSeats() - bookedSeats) >= numberOfSeats;
	}

	
	
	 private ShowDto getShowDetails(int showId, String jwtToken) {
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", jwtToken);
	        HttpEntity<String> entity = new HttpEntity<>(headers);

	        ResponseEntity<ShowDto> response = restTemplate.exchange(
	        		SHOWBASEURL + showId,
	                HttpMethod.GET,
	                entity,
	                ShowDto.class
	        );
	       return response.getBody();
    
	    }
	private UserDto getUserDetails(int userId, String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwtToken); // already contains "Bearer xxx"
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<UserDto> response = restTemplate.exchange(
        		USERBASEURL + userId,
                HttpMethod.GET,
                entity,
                UserDto.class
        );

        return response.getBody();
	}
	
	@Override
	public BookingDto getByBookingId(int bookingId) {
		Booking booking =  bookingRepository.findById(bookingId)
				.orElseThrow(() -> new BookingNotFoundException("invalid id"));
		BookingDto bookingDto = mapper.map(booking, BookingDto.class);
		return bookingDto;
		
	}

	@Override
	public List<BookingDto> getBookingsByUser(int userId) {
		List<Booking> bookings = bookingRepository.findByUserId(userId);
		if (bookings.isEmpty())
			throw new BookingNotFoundException("No booking found in the specified userId");
		return bookings.stream().map(( booking) -> mapper.map( booking, BookingDto.class))
				.toList();
	}

	@Override
	public List<BookingDto> getBookingsByShow(int showId) {
		List<Booking> bookings = bookingRepository.findByShowId(showId);
		if (bookings.isEmpty())
			throw new BookingNotFoundException("No booking found in the specified showId");
		return bookings.stream().map(( booking) -> mapper.map( booking, BookingDto.class))
				.toList();
	}

	@Override
	public void updateBooking(BookingDto bookingDto) {
		Booking booking =  bookingRepository.findById(bookingDto.getBookingId())
				.orElseThrow(() -> new BookingNotFoundException("invalid id"));
		//before doing payment booking status is in pending.
		//if its not pending throw exception
		if(booking.getBookingStatus() != BookingStatus.PENDING ) {
			throw new BookingNotFoundException("Booking is not in Pending state");
		}
		//once payment done pending is changed to booked
		//PAYMENT Process
		booking.setBookingStatus(BookingStatus.BOOKED);
		bookingRepository.save(booking);
	}

	@Override
	public void deleteBooking(int bookingId) {
		Booking booking =  bookingRepository.findById(bookingId)
				.orElseThrow(() -> new BookingNotFoundException("invalid id"));
		
		//validating the cancellation
		validateCancellation(booking);
		
		booking.setBookingStatus(BookingStatus.CANCELLED);
		bookingRepository.save(booking);
	}

	private void validateCancellation(Booking booking) {
	//if show time is 9pm, we can cancel it before 7 pm(deadline).. once 7pm is passed we cant cancel it.
		LocalDateTime showTime= booking.getShow().getShowTime();
	LocalDateTime deadlineTime=	showTime.minusHours(2);
	
	//current time is 8.30 pm means it passed deadline
	//so cannot cancel booking
	if(LocalDateTime.now().isAfter(deadlineTime)) {
		throw new BookingNotFoundException("cannot cance the booking");
	}
	//booking status is cancelled we cant cancel again
	if(booking.getBookingStatus()==BookingStatus.CANCELLED) {
		throw new BookingNotFoundException("Booking Already cancelled");
		
	}
	}

	@Override
	public List<BookingDto> getByBookingStatus(BookingStatus bookingStatus) {
		List<Booking> bookings = bookingRepository.findByBookingStatus(bookingStatus);
		if (bookings.isEmpty())
			throw new BookingNotFoundException("No booking found in the specified bookingStatus");
		return bookings.stream().map(( booking) -> mapper.map( booking, BookingDto.class))
				.toList();
	}

	
}
