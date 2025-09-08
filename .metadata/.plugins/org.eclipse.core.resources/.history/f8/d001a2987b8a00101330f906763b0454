package com.movieapp.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.movieapp.exception.MovieNotFoundException;
import com.movieapp.model.Movie;
import com.movieapp.model.MovieDto;
import com.movieapp.repository.IMovieRepository;

@Service
public class MovieServiceImpl implements IMovieService{
	@Autowired
	private ModelMapper mapper;
	
	private IMovieRepository movieRepository;
	
	
	public MovieServiceImpl(IMovieRepository movieRepository) {
		super();
		this.movieRepository = movieRepository;
	}

	@Override
	public void addMovie(MovieDto movieDto) {
		Movie movie=mapper.map(movieDto,Movie.class);
		movieRepository.save(movie);
		
	}

	@Override
	public void updateMovie(MovieDto movieDto) {
		Movie movie=mapper.map(movieDto,Movie.class);
		movieRepository.save(movie);
		
	}

	@Override
	public void deleteMovie(int movieId) {
		movieRepository.deleteById(movieId);
	}

	@Override
	public List<MovieDto> getAll() {
		List<Movie> movies=movieRepository.findAll();
		return movies.stream()
				.map((movie)->mapper.map(movie, MovieDto.class))
				.toList();
	}

	@Override
	public MovieDto getByMovieId(int movieId) throws MovieNotFoundException {
		Movie movie=movieRepository.findById(movieId)
				.orElseThrow(()->new MovieNotFoundException("invalid id"));			
		MovieDto movieDto=mapper.map(movie,MovieDto.class);
		return movieDto;
	}

//	

	@Override
	public List<MovieDto> getByDirector(String directorName) throws MovieNotFoundException {
		List<Movie> movies=movieRepository.findByDirector("%"+directorName+"%");
		if(movies.isEmpty())
			throw new MovieNotFoundException("No movie found in the specified director");
		return movies.stream()
		.map((movie)->mapper.map(movie, MovieDto.class))
		.toList();
	}

	@Override
	public List<MovieDto> getByTheatreandmovie(String theatreName, String movieTitle) throws MovieNotFoundException {
		List<Movie> movies=movieRepository.findByTheatreandmovie("%"+theatreName+"%", "%"+movieTitle+"%");
		if(movies.isEmpty())
			throw new MovieNotFoundException("No movie found in the specified theatreName and movieTitle");
		return movies.stream()
		.map((movie)->mapper.map(movie, MovieDto.class))
		.toList();
	}

	@Override
	@Transactional
	public void updateMovieTitle(int movieId, String movieTitle) {
		movieRepository.updateMovie(movieId, movieTitle);
		
	}

	

	

}
