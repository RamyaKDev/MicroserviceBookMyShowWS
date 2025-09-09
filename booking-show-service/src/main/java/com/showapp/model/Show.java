package com.showapp.model;





import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "movie_show")  
public class Show {
	@Id
	@GeneratedValue(generator = "movieshow_gen", strategy = GenerationType.AUTO)
	@SequenceGenerator(name = "movieshow_gen", sequenceName = "movieshow_seq", initialValue = 101, allocationSize = 1)
	private Integer showId;

	private String showTime; // Example: "2025-09-01 18:30"

	private double price;

	// External service references (from Movie and Theatre)

	private int movieId; // comes from Movie Service

	private int theatreId; // comes from Theatre Service
}
