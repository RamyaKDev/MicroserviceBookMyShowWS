package com.showapp.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Theatre {
	private Integer theatreId;
	private String theatreName;
	private String location;
	
	@ToString.Exclude
	@JsonIgnore
	private List<Show> shows;
}
