package com.movieapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.movieapp.model.Movie;

@Repository
public interface IMovieRepository extends JpaRepository<Movie, Integer>{
	

	
	//equivalent sql query-> select * from movie inner join director where director_name like "%?%"
	
	@Query("from Movie m inner join m.director d where d.directorName like ?1 ")
	List<Movie> findByDirector(String directorName);
	
	@Query("from Movie m inner join m.theaters t where t.theatreName like ?1 and m.movieTitle like ?2 ")
	List<Movie> findByTheatreandmovie(String theatreName,String movieTitle) ;
	
	//NativeQuery
		@Query(value = "update movie set movie_title=?2 where movie_id=?1",nativeQuery = true)
		@Modifying
		 void updateMovie(int movieId,String movieTitle);
	
	
    
}
