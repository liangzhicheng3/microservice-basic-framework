package com.liangzhicheng.config.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @description 以静态变量保存Spring ApplicationContext,可在代码中任何地方任何时候取出ApplicaitonContext
 * @author liangzhicheng
 * @since 2021-08-05
 */
@Lazy(false)
@Service
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {

	private static ApplicationContext applicationContext = null;

	private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

	/**
	 * @description 取出存储在静态变量中的ApplicationContext
	 * @return ApplicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}

	/**
	 * @description 从静态变量applicationContext中取得Bean,自动转型为所赋值对象的类型
	 * @param name
	 * @param <T>
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * @description 从静态变量applicationContext中取得Bean,自动转型为所赋值对象的类型
	 * @param requiredType
	 * @param <T>
	 * @return T
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		return applicationContext.getBean(requiredType);
	}

	/**
	 * @description 清除SpringContextHolder中的ApplicationContext为Null
	 */
	public static void clearHolder() {
		if (logger.isDebugEnabled()){
			logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
		}
		applicationContext = null;
	}

	/**
	 * @description 实现ApplicationContextAware接口,注入Context到静态变量中
	 * @param applicationContext
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextHolder.applicationContext = applicationContext;
	}

	/**
	 * @description 实现DisposableBean接口,在Context关闭时清理静态变量
	 * @throws Exception
	 */
	@Override
	public void destroy() throws Exception {
		SpringContextHolder.clearHolder();
	}

	/**
	 * @description 检查ApplicationContext不为空
	 */
	private static void assertContextInjected() {
		if(applicationContext == null) {
			throw new IllegalStateException("applicaitonContext属性未注入");
		}
	}

}