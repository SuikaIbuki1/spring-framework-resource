package com.huang.spring.besn;


import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class Cat {

	private String name;

	@Autowired
	public Cat(String name) {
		this.name = name;
	}
}
