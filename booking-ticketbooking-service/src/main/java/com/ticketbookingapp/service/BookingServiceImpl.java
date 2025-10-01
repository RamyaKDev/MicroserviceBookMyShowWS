package com.ticketbookingapp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ticketbookingapp.exception.BookingNotFoundException;
import com.ticketbookingapp.exception.ShowNotFoundException;
import com.ticketbookingapp.model.Booking;
import com.ticketbookingapp.model.BookingDto;
import com.ticketbookingapp.model.BookingStatus;
import com.ticketbookingapp.model.Show;
import com.ticketbookingapp.model.User;
import com.ticketbookingapp.repository.IBookingRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

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
	@CircuitBreaker(name = "booking-cbservice",fallbackMethod = "fallbackcreatebooking")
	public BookingDto createBooking(BookingDto bookingDto,String jwtToken) {

		
     // Step 1: Fetch show details from Show Service
        
        Show show = getShowDetails(bookingDto.getShowId(), jwtToken);
       
        if (show == null) {
            throw new ShowNotFoundException("Invalid showId: ");
        }
        
        // Step 2: Check user requested seats are available for booking for the show
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
        
        User user = getUserDetails(bookingDto.getUserId(), jwtToken);

        if (user == null) {
            throw new ShowNotFoundException ("Invalid userId: ");
        }
    
        //Step 5:   Calculate price
       double price=show.getPrice() * bookingDto.getNumberOfSeats();
        
       //Step 6: setting values to booking class
        Booking booking=new Booking();
        booking.setUserId(user.getUserId());
        booking.setShowId(show.getShowId());
//        booking.setUser(user);        
//        booking.setShow(show);
        booking.setNumberOfSeats(bookingDto.getNumberOfSeats());
        booking.setBookingTime(LocalDateTime.now());
        booking.setTotalPrice(price);
        booking.setBookingStatus(BookingStatus.PENDING);
        
		Booking booked= bookingRepository.save(booking);
		// convert to dto and send to client
		return mapper.map(booked, BookingDto.class);
		
	}
	
	//check already occupied seats A1,A2,A3 again user requested to book
	//If again user wants to book A1,A2 throw exception
	 public void validateDuplicateSeats(Show showDto, List<String> seatNumbers) {
	
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
	 private boolean isSeatAvailable(Show showDto, int numberOfSeats) {
		 // Step 1: Get bookings for this show from DB
	        List<Booking> bookings = bookingRepository.findByShowId(showDto.getShowId());
	     
	        // Step 2: Extract seat numbers already booked
	        int bookedSeats = bookings.stream()
	        		.filter(booking->booking.getBookingStatus() != BookingStatus.CANCELLED )
	                .mapToInt(Booking::getNumberOfSeats)
	                .sum(); 
	        
	        return (showDto.getTotalNoOfSeats() - bookedSeats) >= numberOfSeats;
	}

	//fallback method
		//Here return Type CartDto is changed to Type 
		public BookingDto fallbackAddToCart(Exception e) {
			System.out.println(e.getMessage());
			return new BookingDto();
			//return new Failure("Technical Error");
		}
		
	
	 private Show getShowDetails(int showId, String jwtToken) {
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", jwtToken);
	        HttpEntity<String> entity = new HttpEntity<>(headers);

	        ResponseEntity<Show> response = restTemplate.exchange(
	        		SHOWBASEURL + showId,
	                HttpMethod.GET,
	                entity,
	                Show.class
	        );
	       return response.getBody();
    
	    }
	private User getUserDetails(int userId, String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwtToken); // already contains "Bearer xxx"
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<User> response = restTemplate.exchange(
        		USERBASEURL + userId,
                HttpMethod.GET,
                entity,
                User.class
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
	public String cancelBooking(int bookingId, String jwtToken) {
//		A Booking Cancellation usually has some validation rules, like:
//			1. Don’t allow cancellation after the show has started.
//			2. Don’t allow cancellation for already canceled bookings.
//			Allow cancellation only within X hours before the show.
		Booking booking =  bookingRepository.findById(bookingId)
				.orElseThrow(() -> new BookingNotFoundException("invalid id"));
		
		//validating the cancellation
		 // Validation 1: Already cancelled?
		BookingStatus currentStatus=booking.getBookingStatus();
        if (currentStatus==BookingStatus.CANCELLED) {
            return "Booking already cancelled!";
        }

        // Validation 2:  Check Show timing
        		//  Fetch show details from Show Service and compare show date with current date
        //
        
        Show show = getShowDetails(booking.getShowId(), jwtToken);
       
        if (show == null) {
            throw new ShowNotFoundException("Show not found for booking ");
        }
        
        LocalTime now = LocalTime.now();
        if (show.getShowTime().isBefore(now)) {
            throw new ShowNotFoundException("Cannot cancel past or ongoing shows!");
        }
        
      //if show time is 9pm, we can cancel it before 7 pm(deadline).. once 7pm is passed we cant cancel it.
    
         if (ChronoUnit.HOURS.between(LocalDateTime.now(), show.getShowTime()) < 2) {
             throw new RuntimeException("Cancellation only allowed 2 hours before show");
         }

		booking.setBookingStatus(BookingStatus.CANCELLED);
		bookingRepository.save(booking);
		 return "Booking cancelled successfully";
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
