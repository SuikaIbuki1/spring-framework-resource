package com.huang.spring.config;

import com.huang.spring.besn.Person;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MainConfig implements ApplicationContextAware, MessageSourceAware {

	private ApplicationContext applicationContext;

	private MessageSource messageSource;

	@Bean
	public Person person(){
		Person person = new Person();
		person.setName("lisi");
		return person;
	}

	public MainConfig() {
		System.out.println("MainConfig");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// 实现 ApplicationContextAware 接口，获取 ApplicationContext 对象
		this.applicationContext = applicationContext;
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		// 实现 MessageSourceAware 接口，获取 MessageSource 对象
		this.messageSource = messageSource;
	}
}
