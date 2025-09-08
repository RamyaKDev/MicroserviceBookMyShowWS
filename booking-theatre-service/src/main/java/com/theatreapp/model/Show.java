package com.theatreapp.model;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Show {
	private Integer showId;

    private String showTime;   // Example: "2025-09-01 18:30"

    private double price;
    
    private Movie movie;
    
    private Theatre theatre;
}
