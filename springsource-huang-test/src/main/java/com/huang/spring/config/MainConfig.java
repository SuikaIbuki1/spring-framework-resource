package com.huang.spring.config;

import com.huang.spring.besn.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfig {

	@Bean
	public Person person(){
		Person person = new Person();
		person.setName("lisi");
		return person;
	}
}
