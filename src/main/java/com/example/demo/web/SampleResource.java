package com.example.demo.web;

import org.ff4j.FF4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleResource {
    @Autowired
	FF4j ff4j;

	@GetMapping("/hello")
	public String test() {
		return "test results :\nflag1:" + ff4j.check("flag1")
			+ "\nflag2:" + ff4j.check("flag2") ;
	}

}
