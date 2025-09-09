package com.showapp.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.showapp.model.Theatre;

//this is the client for Theatre service
//pass the name of the service you want to call
@FeignClient(name="theatre-service",url = "http://localhost:8082/theatres-service/v1")
public interface ITheatreFeignClient {
	//annotate the method to map the url of theatre-service Controller
	
	@GetMapping("/theatres/theatreId")
	Theatre getByTheatreId(@RequestParam  int theatreId);
}
