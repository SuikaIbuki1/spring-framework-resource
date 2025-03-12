package com.huang.spring.besn;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;


// Person 就是普通 bean，注册的组件对象就是 Person 对象。类型就是 Person
// 工厂 bean 实现 FactoryBean 接口组件，不是注册 HelloFactory 对象，而是注册 getObject 返回的对象
// mybatis 与 spring 整合，构建 SqlSessionFactoryBean
@Component
public class HelloFactory implements FactoryBean<Hello> {
	@Override
	public @Nullable Hello getObject() throws Exception {
		return new Hello();
	}

	@Override
	public @Nullable Class<?> getObjectType() {
		return Hello.class;
	}
}
