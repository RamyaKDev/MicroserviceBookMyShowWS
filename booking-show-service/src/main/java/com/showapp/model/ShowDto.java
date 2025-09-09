package com.showapp.model;





import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class ShowDto {
	
    private Integer showId;

    private String showTime;   // Example: "2025-09-01 18:30"

    private double price;
 // External service references (from Movie and Theatre)

 	private int movieId; // comes from Movie Service

 	private int theatreId; // comes from Theatre Service
    
    
}
