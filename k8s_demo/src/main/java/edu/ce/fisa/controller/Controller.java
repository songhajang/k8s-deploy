package edu.ce.fisa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

	@GetMapping("/get")
	public String getFun() {
		return "GET 방식 요청입니다 :)";
	}
	
	@PostMapping("/post")
	public String postFun() {
		return "GET 방식 요청입니다 :)";
	}
}
