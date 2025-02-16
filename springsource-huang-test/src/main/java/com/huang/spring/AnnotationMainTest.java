package com.huang.spring;

import com.huang.spring.besn.Person;
import com.huang.spring.config.MainConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AnnotationMainTest {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(MainConfig.class);
		Person bean = context.getBean(Person.class);
		System.out.println(bean.getName());
	}
}
