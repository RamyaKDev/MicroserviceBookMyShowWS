package com.movieapp.service;

import java.util.List;

import com.movieapp.exception.MovieNotFoundException;
import com.movieapp.model.MovieDto;

public interface IMovieService {
	//CRUD Operations
		void addMovie(MovieDto movieDto);
		void updateMovie(MovieDto movieDto);
		void deleteMovie(int movieId);
		List<MovieDto> getAll();
		MovieDto getByMovieId(int movieId) throws MovieNotFoundException;
		
	//Custom methods	
	
		List<MovieDto> getByDirector(String directorName) throws MovieNotFoundException;
		List<MovieDto> getByTheatreandmovie(String theatreName,String movieTitle) throws MovieNotFoundException;
		
	//NativeQuery
		void updateMovieTitle(int movieId,String movieTitle );
		
	
		
}
