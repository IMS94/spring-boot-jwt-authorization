package com.example.springboot.jwt.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
	
	@PostMapping(path = "login", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public void login(
			@RequestParam String email,
			@RequestParam String password) {
		
	}
}
