package com.movieapp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movieapp.exception.MovieNotFoundException;
import com.movieapp.model.MovieDto;
import com.movieapp.model.MovieTitleDto;
import com.movieapp.service.IMovieService;

@RestController
@RequestMapping("/movies-api/v1")
public class MovieController {
	@Autowired
	private IMovieService movieService;
	
	
	//http://localhost:8081/movies-api/v1/movies
	@PostMapping("/movies")
	ResponseEntity<Void> addMovie(@RequestBody MovieDto movieDto) {
		movieService.addMovie(movieDto);
		HttpHeaders headers=new HttpHeaders();
		headers.add("info", "Movie Inserted");
		return ResponseEntity.status(HttpStatus.CREATED).headers(headers).build();

	}
	
	//http://localhost:8081/movies-api/v1/movies
	@PutMapping("/movies")
	ResponseEntity<Void> updateMovie(@RequestBody MovieDto movieDto) {
		movieService.updateMovie(movieDto);
		HttpHeaders headers=new HttpHeaders();
		headers.add("info", "Movie updated by Id");
		return ResponseEntity.status(HttpStatus.ACCEPTED).headers(headers).build();
		
	}
	
	//http://localhost:8081/movies-api/v1/movies/newTitle
	@PatchMapping("/movies/newTitle")
	ResponseEntity<Void> updateMovie(@RequestBody MovieTitleDto movietitleDto) {
		int movieId=movietitleDto.getMovieId();
		String newmovie=movietitleDto.getMovieTitle();
		
		movieService.updateMovieTitle(movieId, newmovie);	
		HttpHeaders headers=new HttpHeaders();
		headers.add("info", "Movie updated by Id");
		return ResponseEntity.status(HttpStatus.ACCEPTED).headers(headers).build();
		
		}
	
	//http://localhost:8081/movies-api/v1/movies/movieId/2
	@DeleteMapping("/movies/movieId/{movieId}")
	ResponseEntity<Void> deleteMovie(@PathVariable int movieId){
		movieService.deleteMovie(movieId);
		HttpHeaders headers=new HttpHeaders();
		headers.add("info", "Movie deleted by id");
		return ResponseEntity.status(HttpStatus.OK).headers(headers).build();
		
	}
	//http://localhost:8081/movies-api/v1/movies
	@GetMapping("/movies")
	ResponseEntity<List<MovieDto>> getAll(){
		List<MovieDto> movieDto=movieService.getAll();
		HttpHeaders headers=new HttpHeaders();
		headers.add("info", "Getting all movies");
		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(movieDto);
		
		
	}
	
	//http://localhost:8081/movies-api/v1/movies/movieId?movieId=1
	@GetMapping("/movies/movieId")
	ResponseEntity<MovieDto> getByMovieId(@RequestParam int movieId) throws MovieNotFoundException{
		MovieDto movieDto=movieService.getByMovieId(movieId);
		HttpHeaders headers=new HttpHeaders();
		headers.add("info", "Getting movie by id");
		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(movieDto);
	}


	
	//http://localhost:8081/movies-api/v1/movies/director/James
	@GetMapping("/movies/director/{director}")
	ResponseEntity<List<MovieDto>> getByDirector(@PathVariable("director") String directorName) throws MovieNotFoundException{
		List<MovieDto> movieDto=movieService.getByDirector(directorName);
		HttpHeaders headers=new HttpHeaders();
		headers.add("info", "Getting movie by director");
		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(movieDto);
		
	}
	
	//http://localhost:8081/movies-api/v1/movies/theatreName/Sri Ganesh/movieTitle/3BHK
		@GetMapping("/movies/theatreName/{theatreName}/movieTitle/{movieTitle}")
	ResponseEntity<List<MovieDto>> getByTheatreandmovie(@PathVariable String theatreName,@PathVariable String movieTitle) throws MovieNotFoundException{
			List<MovieDto> movieDto=movieService.getByTheatreandmovie(theatreName, movieTitle);
			HttpHeaders headers=new HttpHeaders();
			headers.add("info", "Getting movie by theatreName and movieTitle");
			return ResponseEntity.status(HttpStatus.OK).headers(headers).body(movieDto);
		
	}
		
}
