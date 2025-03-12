package com.huang.spring.processor.bean;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

// huang：生命周期流程
@Component
public class MyInitializingBean implements InitializingBean {

	public MyInitializingBean() {
		System.out.println("MyInitializingBean.MyInitializingBean");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("MyInitializingBean.afterPropertiesSet");
	}
}
