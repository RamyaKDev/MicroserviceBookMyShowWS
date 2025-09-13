package com.ticketbookingapp.model;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShowDto {
	private Integer showId;
	private String movieTitle;
    private LocalDateTime showDate;
    private LocalDateTime showTime;
    private double price;
    private int movieId;    
    private int theatreId;// Reference to Theatre Service
    private int totalNoOfSeats;
}
