# spring-boot-learn
spring boot 学习



# Spring 全家桶
 
## 一、Spring Boot 启动流程分析

> spring boot 2.0 和 spring boot 1.0 有点小区别。


### 我们程序的入口

SpringBoot核心启动类的SpringApplication流程分析

```

@SpringBootApplication
public class ServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}
```

### @SpringBootApplication 注解

```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration

// 相当于sprng mvn 扫描包
@ComponentScan(excludeFilters = {
		@Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
  ...
}
```

虽然定义使用了多个Annotation进行了原信息标注，但实际上重要的只有三个Annotation

```
@Configuration（@SpringBootConfiguration点开查看发现里面还是应用了@Configuration）
@EnableAutoConfiguration
@ComponentScan
```

##### @SpringBootConfiguration
这里的@Configuration对我们来说不陌生，它就是JavaConfig形式的Spring Ioc容器的配置类使用的那个@Configuration
```
表达形式层面
基于XML配置的方式是这样：

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd"
       default-lazy-init="true">
    <!--bean定义-->
</beans>

```
而基于JavaConfig的配置方式是这样：
``` 
@Configuration
public class MockConfiguration{
    //bean定义
}
```
任何一个标注了@Configuration的Java类定义都是一个JavaConfig配置类。

注册bean定义层面

基于XML的配置形式是这样：

```
<bean id="mockService" class="..MockServiceImpl">
    ...
</bean>
```
而基于JavaConfig的配置形式是这样的：
```
@Configuration
public class MockConfiguration{
    @Bean
    public MockService mockService(){
        return new MockServiceImpl();
    }
}
```
任何一个标注了@Bean的方法，其返回值将作为一个bean定义注册到Spring的IoC容器，方法名将默认成该bean定义的id。

- 表达依赖注入关系层面\
为了表达bean与bean之间的依赖关系，在XML形式中一般是这样：
```
<bean id="mockService" class="..MockServiceImpl">
    <propery name ="dependencyService" ref="dependencyService" />
</bean>

<bean id="dependencyService" class="DependencyServiceImpl"></bean>

```
 
- 而基于JavaConfig的配置形式是这样的：
- 
```
@Configuration
public class MockConfiguration{
    @Bean
    public MockService mockService(){
        return new MockServiceImpl(dependencyService());
    }
    
    @Bean
    public DependencyService dependencyService(){
        return new DependencyServiceImpl();
    }
}
```
如果一个bean的定义依赖其他bean,则直接调用对应的JavaConfig类中依赖bean的创建方法就可以了。

##### @ComponentScan

@ComponentScan这个注解在Spring中很重要，它对应XML配置中的元素，@ComponentScan的功能其实就是自动扫描并加载符合条件的组件（比如@Component和@Repository等）或者bean定义，最终将这些bean定义加载到IoC容器中。

我们可以通过basePackages等属性来细粒度的定制@ComponentScan自动扫描的范围，如果不指定，则默认Spring框架实现会从声明@ComponentScan所在类的package进行扫描。

> 注：所以SpringBoot的启动类最好是放在root package下，因为默认不指定basePackages

##### @EnableAutoConfiguration

个人感觉@EnableAutoConfiguration这个Annotation最为重要，大家是否还记得Spring框架提供的各种名字为@Enable开头的Annotation定义？比如@EnableScheduling、@EnableCaching、@EnableMBeanExport等，@EnableAutoConfiguration的理念和做事方式其实一脉相承，简单概括一下就是，借助@Import的支持，收集和注册特定场景相关的bean定义。

> @EnableScheduling是通过@Import将Spring调度框架相关的bean定义都加载到IoC容器。

> @EnableMBeanExport是通过@Import将JMX相关的bean定义加载到IoC容器。

> @EnableAutoConfiguration也是借助@Import的帮助，将所有符合自动配置条件的bean定义加载到IoC容器，仅此而已！

@EnableAutoConfiguration作为一个复合Annotation,其自身定义关键信息如下：

```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {

   //启用覆盖
	String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";
 
	Class<?>[] exclude() default {};
 
	String[] excludeName() default {};

}

```
其中，最关键的要属@Import(AutoConfigurationImportSelector.class)，借助AutoConfigurationImportSelector，@EnableAutoConfiguration可以帮助SpringBoot应用将所有符合条件的@Configuration配置都加载到当前SpringBoot创建并使用的IoC容器。就像一只“八爪鱼”一样。

借助于Spring框架原有的一个工具类：SpringFactoriesLoader的支持，

@EnableAutoConfiguration可以智能的自动配置功效才得以大功告成！

![image](http://7xqch5.com1.z0.glb.clouddn.com/springboot3-1.png)

SpringFactoriesLoader属于Spring框架私有的一种扩展方案，其主要功能就是从指定的配置文件META-INF/spring.factories加载配置.
```
public abstract class SpringFactoriesLoader {
    //...
    public static <T> List<T> loadFactories(Class<T> factoryClass, ClassLoader classLoader) {
        ...
    }


    public static List<String> loadFactoryNames(Class<?> factoryClass, ClassLoader classLoader) {
        ....
    }
}
```
配合@EnableAutoConfiguration使用的话，它更多是提供一种配置查找的功能支持，即根据@EnableAutoConfiguration的完整类名org.springframework.boot.autoconfigure.EnableAutoConfiguration作为查找的Key,获取对应的一组@Configuration类.

![image](http://7xqch5.com1.z0.glb.clouddn.com/springboot3-2.jpg)

上图就是从SpringBoot的**autoconfigure**依赖包中的META-INF/spring.factories配置文件中摘录的一段内容，可以很好地说明问题。

所以，@EnableAutoConfiguration自动配置的魔法骑士就变成了：从classpath中搜寻所有的META-INF/spring.factories配置文件，并将其中org.springframework.boot.autoconfigure.EnableutoConfiguration对应的配置项通过反射（Java Refletion）实例化为对应的标注了@Configuration的JavaConfig形式的IoC容器配置类，然后汇总为一个并加载到IoC容器.

转载<http://tengj.top/2017/03/09/springboot3/>

## SpringApplication执行流程,SpringApplication.run();


SpringApplication的run方法的实现是我们本次旅程的主要线路，该方法的主要流程大体可以归纳如下：

1） 如果我们使用的是SpringApplication的静态run方法，那么，这个方法里面首先要创建一个SpringApplication对象实例，然后调用这个创建好的SpringApplication的实例方法。在SpringApplication实例初始化的时候，它会提前做几件事情：
- 根据classpath里面是否存在某个特征类（org.springframework.web.context.ConfigurableWebApplicationContext）来决定是否应该创建一个为Web应用使用的ApplicationContext类型。
- 使用SpringFactoriesLoader在应用的classpath中查找并加载所有可用的ApplicationContextInitializer。
- 使用SpringFactoriesLoader在应用的classpath中查找并加载所有可用的ApplicationListener。
推断并设置main方法的定义类。

源码

```
public static ConfigurableApplicationContext run(Class<?> primarySource,
		String... args) {
	
	// 调用重构方法
	return run(new Class<?>[] { primarySource }, args);
}

```
- 进入重构run()方法
```
public static ConfigurableApplicationContext run(Class<?>[] primarySources,
		String[] args) {
		
	// 先创建SpringAppliaton 实例，然后调用run() 方法	
	return new SpringApplication(primarySources).run(args);
}
```
- 从这里可以看到首先创建了一个SpringApplication实例，然后在调用的其run()方法。首先我们先去创建实例这一流程：

```
public SpringApplication(Class<?>... primarySources) {
	this(null, primarySources);
}


spring boot 1.x

public SpringApplication(Object... sources) {
	initialize(sources);
}


```
调用 SpringApplication 重构构造方法

```
public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
	this.resourceLoader = resourceLoader;
	Assert.notNull(primarySources, "PrimarySources must not be null");
	this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
	
	// 运行环境创建
	this.webApplicationType = deduceWebApplicationType();
	setInitializers((Collection) getSpringFactoriesInstances(
			ApplicationContextInitializer.class));
	setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
	this.mainApplicationClass = deduceMainApplicationClass();
}



如果是 spring boot 1.x 则是调用


private void initialize(Object[] sources) {
	if (sources != null && sources.length > 0) {
		this.sources.addAll(Arrays.asList(sources));
	}
	
	// 运行环境创建
	this.webEnvironment = deduceWebEnvironment();
	setInitializers((Collection) getSpringFactoriesInstances(
			ApplicationContextInitializer.class));
	setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
	this.mainApplicationClass = deduceMainApplicationClass();
}

	
```
从面的代码可以看到初始化过程做了以下几件事情 

spring boot 2.0
> this.webEnvironment = deduceWebApplicationType(); 

spring boot 1.x
> this.webEnvironment = deduceWebEnvironment();

这一个方法决定创建的是一个WEB应用还是一个SPRING的标准Standalone应用。如果入方法可以看到其是怎么判断的

```
private WebApplicationType deduceWebApplicationType() {
	if (ClassUtils.isPresent(REACTIVE_WEB_ENVIRONMENT_CLASS, null)
			&& !ClassUtils.isPresent(MVC_WEB_ENVIRONMENT_CLASS, null)) {
		return WebApplicationType.REACTIVE;
	}
	for (String className : WEB_ENVIRONMENT_CLASSES) {
		if (!ClassUtils.isPresent(className, null)) {
			return WebApplicationType.NONE;
		}
	}
	return WebApplicationType.SERVLET;
}



spring boot 1.x

private boolean deduceWebEnvironment() {
	for (String className : WEB_ENVIRONMENT_CLASSES) {
		if (!ClassUtils.isPresent(className, null)) {
			return false;
		}
	}
	return true;
}
```
我们看到spring boot 2.0 判断 webApplicationType 有REACTIVE 枚举。

我们先看看这张图。Spring Boot 2.0 这里有两条不同的线分别是：

```
Spring Web MVC -> Spring Data
Spring WebFlux -> Spring Data Reactive
```

![](https://spring.io/img/homepage/diagram-boot-reactor.svg)


对照下 Spring Web MVC ，Spring Web MVC 是基于 Servlet API 和 Servlet 容器设计的。

那么 Spring WebFlux 肯定不是基于前面两者，它基于 Reactive Streams API 和 Servlet 3.1+ 容器设计。

Spring WebFlux 是 Spring 5 的一个新模块，包含了响应式 HTTP 和 WebSocket 的支持，另外在上层服务端支持两种不同的编程模型：

```
基于 Spring MVC 注解 @Controller 等
基于 Functional 函数式路由
```
转载 
<https://www.bysocket.com/?p=1987>


- 可以看到是根据org.springframework.util.ClassUtils的静态方法去判断classpath里面是否有 REACTIVE_WEB_ENVIRONMENT_CLASS 并且不是MVC_WEB_ENVIRONMENT_CLASS  则是 REACTIVE，
然后在判断是否有 WEB_ENVIRONMENT_CLASSES。

枚举类
```
/**
 * The application should not run as a web application and should not start an
 * embedded web server.
 */
NONE,

/**
 * The application should run as a servlet-based web application and should start an
 * embedded servlet web server.
 */
SERVLET,

/**
 * The application should run as a reactive web application and should start an
 * embedded reactive web server.
 */
REACTIVE
	
```

但是如果是spring boot 1.x ,
可以看到是根据org.springframework.util.ClassUtils的静态方法去判断classpath里面是否有WEB_ENVIRONMENT_CLASSES包含的类，如果有都包含则返回true则表示启动一个WEB应用，否则返回false启动一个标准Spring的应用。然后通过代码：

```
// spring boot 2.0 新增代码
private static final String REACTIVE_WEB_ENVIRONMENT_CLASS = "org.springframework."
		+ "web.reactive.DispatcherHandler";

// spring boot 2.0 新增代码
private static final String MVC_WEB_ENVIRONMENT_CLASS = "org.springframework."
		+ "web.servlet.DispatcherServlet";

private static final String[] WEB_ENVIRONMENT_CLASSES = { "javax.servlet.Servlet",
		"org.springframework.web.context.ConfigurableWebApplicationContext" };
			
```

可以看到是否启动一个WEB应用就是取决于classpath下是否有javax.servlet.Servlet和 
org.springframework.web.context.ConfigurableWebApplicationContext。

然后进入下一个阶段：

```
setInitializers((Collection) getSpringFactoriesInstances(
				ApplicationContextInitializer.class));
```
- 上面这个方法则是初始化classpath下的所有的可用的ApplicationContextInitializer

```
setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
```
- 上面这个方法则是初使化classpath下的所有的可用的ApplicationListener


```
this.mainApplicationClass = deduceMainApplicationClass();


private Class<?> deduceMainApplicationClass() {
	try {
		StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
		for (StackTraceElement stackTraceElement : stackTrace) {
			if ("main".equals(stackTraceElement.getMethodName())) {
				return Class.forName(stackTraceElement.getClassName());
			}
		}
	}
	catch (ClassNotFoundException ex) {
		// Swallow and continue
	}
	return null;
}
	
```
- 最后找出main方法的全类名并返回其实例并设置到SpringApplication的this.mainApplicationClass完成初始化。

- 然后调用SpringApplication实例的run方法来启动应用，代码如下：
> spring boot 2.0, spring boot 1.x 自己去看源码
```
/**
	 * Run the Spring application, creating and refreshing a new
	 * {@link ApplicationContext}.
	 * @param args the application arguments (usually passed from a Java main method)
	 * @return a running {@link ApplicationContext}
	 */
	 
	public ConfigurableApplicationContext run(String... args) {
	   // 统计时间
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		ConfigurableApplicationContext context = null;
		Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
		configureHeadlessProperty();
		
		 //初始化监听器
		SpringApplicationRunListeners listeners = getRunListeners(args);
	    
	    //发布ApplicationStartedEvent
		listeners.starting();
		try {
		
		    //获取启动时传入参数args并初始化为ApplicationArguments对象 
			ApplicationArguments applicationArguments = new DefaultApplicationArguments(
					args);
					
			ConfigurableEnvironment environment = prepareEnvironment(listeners,
					applicationArguments);
			configureIgnoreBeanInfo(environment);
			
			 //打印Banner
			Banner printedBanner = printBanner(environment);
			
			//创建ApplicationContext()
			context = createApplicationContext();
			
			// 读取 spring-boot 包里 META-INF/spring.factories
			exceptionReporters = getSpringFactoriesInstances(
					SpringBootExceptionReporter.class,
					new Class[] { ConfigurableApplicationContext.class }, context);

           //装配Context					
			prepareContext(context, environment, listeners, applicationArguments,printedBanner);
           
           //refreshContext					
			refreshContext(context);
			
			//afterRefresh
			afterRefresh(context, applicationArguments);
			stopWatch.stop();
			if (this.logStartupInfo) {
				new StartupInfoLogger(this.mainApplicationClass)
						.logStarted(getApplicationLog(), stopWatch);
			}
			
			//发布ApplicationReadyEvent
			listeners.started(context);
			callRunners(context, applicationArguments);
		}
		catch (Throwable ex) {
			handleRunFailure(context, listeners, exceptionReporters, ex);
			throw new IllegalStateException(ex);
		}
		listeners.running(context);
		return context;
	}

```
2、SpringApplication实例初始化完成并且完成设置后，就开始执行run方法的逻辑了，方法执行伊始，首先遍历执行所有通过SpringFactoriesLoader可以查找到并加载的SpringApplicationRunListener。调用它们的started()方法，告诉这些SpringApplicationRunListener，“嘿，SpringBoot应用要开始执行咯！”
```
SpringApplicationRunListeners listeners = getRunListeners(args);
	
```
3、创建并配置当前Spring Boot应用将要使用的Environment（包括配置要使用的PropertySource以及Profile） 

```
ConfigurableEnvironment environment = prepareEnvironment(listeners,
					applicationArguments);
```

4、遍历调用所有SpringApplicationRunListener的environmentPrepared()的方法，告诉他们：“当前SpringBoot应用使用的Environment准备好了咯！”。
```
ConfigurableEnvironment environment = prepareEnvironment(listeners,
					applicationArguments);
					

private ConfigurableEnvironment prepareEnvironment(
		SpringApplicationRunListeners listeners,
		ApplicationArguments applicationArguments) {
	// Create and configure the environment
	ConfigurableEnvironment environment = getOrCreateEnvironment();
	configureEnvironment(environment, applicationArguments.getSourceArgs());
	listeners.environmentPrepared(environment);
	bindToSpringApplication(environment);
	if (this.webApplicationType == WebApplicationType.NONE) {
		environment = new EnvironmentConverter(getClassLoader())
				.convertToStandardEnvironmentIfNecessary(environment);
	}
	ConfigurationPropertySources.attach(environment);
	return environment;
}					
```


 5、如果SpringApplication的showBanner属性被设置为true，则打印banner。
```
Banner printedBanner = printBanner(environment);
```
读取默认的Banner 文件，如果想自定义则在 project-name/src/main/resources/banner.txt中加入以下内容
```
 DEFAULT_BANNER_LOCATION = "banner.txt";
 
 private static final Banner DEFAULT_BANNER = new SpringBootBanner();

```
Banner 选择颜色 AnsiColor


6、根据用户是否明确设置了applicationContextClass类型以及初始化阶段的推断结果，决定该为当前SpringBoot应用创建什么类型的ApplicationContext并创建完成，

可以看出根据这前初始化过程初始化的this.webEnvironment来决定初始化一个什么容器.

将之前准备好的Environment设置给创建好的ApplicationContext使用。
 
```
context = createApplicationContext();

 
 // 初始化一个什么容器。
protected ConfigurableApplicationContext createApplicationContext() {
	Class<?> contextClass = this.applicationContextClass;
	if (contextClass == null) {
		try {
			switch (this.webApplicationType) {
			case SERVLET:
				contextClass = Class.forName(DEFAULT_WEB_CONTEXT_CLASS);
				break;
			case REACTIVE:
				contextClass = Class.forName(DEFAULT_REACTIVE_WEB_CONTEXT_CLASS);
				break;
			default:
				contextClass = Class.forName(DEFAULT_CONTEXT_CLASS);
			}
		}
		catch (ClassNotFoundException ex) {
			throw new IllegalStateException(
					"Unable create a default ApplicationContext, "
							+ "please specify an ApplicationContextClass",
					ex);
		}
	}
	return (ConfigurableApplicationContext) BeanUtils.instantiateClass(contextClass);
}
	
```
 如果不存在则用DEFAULT_CONTEXT_CLASS初始化容器。

```
/**
 * The class name of application context that will be used by default for non-web
 * environments.
 */
public static final String DEFAULT_CONTEXT_CLASS = "org.springframework.context."
		+ "annotation.AnnotationConfigApplicationContext";

/**
 * The class name of application context that will be used by default for web
 * environments.
 */
public static final String DEFAULT_WEB_CONTEXT_CLASS = "org.springframework.boot."
		+ "web.servlet.context.AnnotationConfigServletWebServerApplicationContext";

/**
 * The class name of application context that will be used by default for reactive web
 * environments.
 */
public static final String DEFAULT_REACTIVE_WEB_CONTEXT_CLASS = "org.springframework."
		+ "boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext";
		
```
- 以上是代码指定了容器的类名，最后通过Spring的工具类初始化容器类bean 
BeanUtils.instantiate(contextClass); 
完成容器的创建工作。

 7、ApplicationContext创建好之后，SpringApplication会再次借助Spring-FactoriesLoader，查找并加载classpath中所有可用的ApplicationContext-Initializer，然后遍历调用这些ApplicationContextInitializer的initialize（applicationContext）方法来对已经创建好的ApplicationContext进行进一步的处理。\
读取 spring-boot 包里 META-INF/spring.factories
```
exceptionReporters = getSpringFactoriesInstances(
		SpringBootExceptionReporter.class,
		new Class[] { ConfigurableApplicationContext.class }, context);
 
 ...
 
private <T> Collection<T> getSpringFactoriesInstances(Class<T> type) {
	return getSpringFactoriesInstances(type, new Class<?>[] {});
}

private <T> Collection<T> getSpringFactoriesInstances(Class<T> type,
		Class<?>[] parameterTypes, Object... args) {
	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	// Use names and ensure unique to protect against duplicates
	// 调用SpringFactoriesLoader.loadFactoryNames 读取
	// META-INF/spring.factories
	Set<String> names = new LinkedHashSet<>(
			SpringFactoriesLoader.loadFactoryNames(type, classLoader));
	List<T> instances = createSpringFactoriesInstances(type, parameterTypes,
			classLoader, args, names);
	AnnotationAwareOrderComparator.sort(instances);
	return instances;
}

```

8、遍历调用所有SpringApplicationRunListener的contextPrepared()方法。

9、最核心的一步，将之前通过@EnableAutoConfiguration获取的所有配置以及其他形式的IoC容器配置加载到已经准备完毕的ApplicationContext。

10、遍历调用所有SpringApplicationRunListener的contextLoaded()方法。

```
prepareContext(context, environment, listeners, applicationArguments,
				printedBanner);
				

private void prepareContext(ConfigurableApplicationContext context,
		ConfigurableEnvironment environment, SpringApplicationRunListeners listeners,
		ApplicationArguments applicationArguments, Banner printedBanner) {
	context.setEnvironment(environment);
	postProcessApplicationContext(context);
	applyInitializers(context);
	// 调用 SpringApplicationRunListeners 的contextPrepared
	listeners.contextPrepared(context);
	if (this.logStartupInfo) {
		logStartupInfo(context.getParent() == null);
		logStartupProfileInfo(context);
	}

	// Add boot specific singleton beans
	context.getBeanFactory().registerSingleton("springApplicationArguments",
			applicationArguments);
	if (printedBanner != null) {
		context.getBeanFactory().registerSingleton("springBootBanner", printedBanner);
	}

	// Load the sources
	Set<Object> sources = getAllSources();
	Assert.notEmpty(sources, "Sources must not be empty");
	load(context, sources.toArray(new Object[0]));
	
	//遍历调用所有SpringApplicationRunListener的contextLoaded()方法。
	listeners.contextLoaded(context);
}


// SpringApplicationRunListeners 类
//遍历调用所有SpringApplicationRunListener的contextPrepared()方法。
public void contextPrepared(ConfigurableApplicationContext context) {
    // 遍历
	for (SpringApplicationRunListener listener : this.listeners) {
		listener.contextPrepared(context);
	}
}

// SpringApplicationRunListeners 类
// 遍历调用所有SpringApplicationRunListener的contextLoaded()方法。
public void contextLoaded(ConfigurableApplicationContext context) {
	for (SpringApplicationRunListener listener : this.listeners) {
		listener.contextLoaded(context);
	}
}

```

11、调用ApplicationContext的refresh()方法，完成IoC容器可用的最后一道工序。
```
refreshContext(context);
afterRefresh(context, applicationArguments);


//根据条件决定是否添加ShutdownHook，
 
private void refreshContext(ConfigurableApplicationContext context) {
	refresh(context);
	if (this.registerShutdownHook) {
		try {
			context.registerShutdownHook();
		}
		catch (AccessControlException ex) {
			// Not allowed in some environments.
		}
	

```

12、查找当前ApplicationContext中是否注册有CommandLineRunner，如果有，则遍历执行它们。
```

```
13、正常情况下，遍历执行SpringApplicationRunListener的finished()方法、（如果整个过程出现异常，则依然调用所有SpringApplicationRunListener的finished()方法，只不过这种情况下会将异常信息一并传入处理）
```
spring boot 1.x 才有，2.0 找不到这个finished方法

listeners.finished(context, null);

而是多了
listeners.started(context);
callRunners(context, applicationArguments);
...
listeners.running(context);

```
去除事件通知点后，整个流程如下：


![image](http://7xqch5.com1.z0.glb.clouddn.com/springboot3-3.jpg)


转载
<http://tengj.top/2017/03/09/springboot3/>\
<https://blog.csdn.net/doegoo/article/details/52471310>

完


---
 

## 二、Embead Tomcat的启动流程 ,判断是否在web环境?

spring boot在启动时，先通过一个简单的查找Servlet类的方式来判断是不是在web环境：

```
private static final String REACTIVE_WEB_ENVIRONMENT_CLASS = "org.springframework."
		+ "web.reactive.DispatcherHandler";

private static final String MVC_WEB_ENVIRONMENT_CLASS = "org.springframework."
		+ "web.servlet.DispatcherServlet";
		
private WebApplicationType deduceWebApplicationType() {
	if (ClassUtils.isPresent(REACTIVE_WEB_ENVIRONMENT_CLASS, null)
			&& !ClassUtils.isPresent(MVC_WEB_ENVIRONMENT_CLASS, null)) {
		return WebApplicationType.REACTIVE;
	}
	for (String className : WEB_ENVIRONMENT_CLASSES) {
		if (!ClassUtils.isPresent(className, null)) {
			return WebApplicationType.NONE;
		}
	}
	return WebApplicationType.SERVLET;
}

```

 根据 上面方法(deduceWebApplicationType) webApplicationType 来判断返回那个Class

```
/**
 * Strategy method used to create the {@link ApplicationContext}. By default this
 * method will respect any explicitly set application context or application context
 * class before falling back to a suitable default.
 * @return the application context (not yet refreshed)
 * @see #setApplicationContextClass(Class)
 */
protected ConfigurableApplicationContext createApplicationContext() {
	Class<?> contextClass = this.applicationContextClass;
	if (contextClass == null) {
		try {
			switch (this.webApplicationType) {
			case SERVLET:
				contextClass = Class.forName(DEFAULT_WEB_CONTEXT_CLASS);
				break;
			case REACTIVE:
				contextClass = Class.forName(DEFAULT_REACTIVE_WEB_CONTEXT_CLASS);
				break;
			default:
				contextClass = Class.forName(DEFAULT_CONTEXT_CLASS);
			}
		}
		catch (ClassNotFoundException ex) {
			throw new IllegalStateException(
					"Unable create a default ApplicationContext, "
							+ "please specify an ApplicationContextClass",
					ex);
		}
	}
	return (ConfigurableApplicationContext) BeanUtils.instantiateClass(contextClass);
}
```




## 三、spring boot 打 jar 方式启动

打包为单个jar时，spring boot的启动方式
maven /gradle 打包之后，会生成两个jar文件：

```
demo-0.0.1-SNAPSHOT.jar
demo-0.0.1-SNAPSHOT.jar.original
```
其中demo-0.0.1-SNAPSHOT.jar.original是默认的maven-jar-plugin生成的包。

demo-0.0.1-SNAPSHOT.jar是spring boot maven插件生成的jar包，里面包含了应用的依赖，以及spring boot相关的类。下面称之为fat jar。

先来查看spring boot打好的包的目录结构（不重要的省略掉）
```
├── META-INF
│   ├── MANIFEST.MF
├── BOOT-INF
│       ├── classes
│               ├── spring.yml
│               ├── com.space.xxx   
│       ├── lib
│               ├── aopalliance-1.0.jar
│               ├── spring-beans-4.2.3.RELEASE.jar
│               ├── ...
└── org
    └── springframework
        └── boot
            └── loader
                ├── ExecutableArchiveLauncher.class
                ├── JarLauncher.class
                ├── JavaAgentDetector.class
                ├── LaunchedURLClassLoader.class
                ├── Launcher.class
                ├── MainMethodRunner.class
                ├── 一些文件夹  
                ├── ...   
                

                
```
依次来看下这些内容。
MANIFEST.MF
```
Manifest-Version: 1.0
Start-Class: com.space.Application
Spring-Boot-Classes: BOOT-INF/classes/
Spring-Boot-Lib: BOOT-INF/lib/
Spring-Boot-Version: 1.5.10.RELEASE
Main-Class: org.springframework.boot.loader.JarLauncher

```

- 可以看到有Main-Class是org.springframework.boot.loader.JarLauncher ，这个是jar启动的Main函数。

- 还有一个Start-Class是com.space.Application，这个是我们应用自己的Main函数。

- org/springframework/boot/loader 目录
这下面存放的是Spring boot loader的.class文件。

- ROOT-INF/classes 存放我们的类
- BOOT-INF/lib/    存放需要jar

<https://blog.csdn.net/hengyunabc/article/details/50120001>


### Archive的概念 / jar 启动原理
Archive的概念
archive即归档文件，这个概念在linux下比较常见
通常就是一个tar/zip格式的压缩包
jar是zip格式
在spring boot里，抽象出了Archive的概念。

一个archive可以是一个jar（JarFileArchive），也可以是一个文件目录（ExplodedArchive）。可以理解为Spring boot抽象出来的统一访问资源的层。

上面的demo-0.0.1-SNAPSHOT.jar 是一个Archive，然后demo-0.0.1-SNAPSHOT.jar里的/lib目录下面的每一个Jar包，也是一个Archive。

```
public abstract class Archive {
    public abstract URL getUrl();
    public String getMainClass();
    public abstract Collection<Entry> getEntries();
    public abstract List<Archive> getNestedArchives(EntryFilter filter);
```
可以看到Archive有一个自己的URL，比如：

> jar:file:/tmp/target/demo-0.0.1-SNAPSHOT.jar!/

还有一个getNestedArchives函数，这个实际返回的是demo-0.0.1-SNAPSHOT.jar/lib下面的jar的Archive列表。它们的URL是：

```
jar:file:/tmp/target/demo-0.0.1-SNAPSHOT.jar!/lib/aopalliance-1.0.jar
jar:file:/tmp/target/demo-0.0.1-SNAPSHOT.jar!/lib/spring-beans-4.2.3.RELEASE.jar
```

JarLauncher
从MANIFEST.MF可以看到Main函数是JarLauncher，下面来分析它的工作流程。

JarLauncher类的继承结构是：
```
class JarLauncher extends ExecutableArchiveLauncher
class ExecutableArchiveLauncher extends Launcher
```

以demo-0.0.1-SNAPSHOT.jar创建一个Archive：
JarLauncher先找到自己所在的jar，即demo-0.0.1-SNAPSHOT.jar的路径，然后创建了一个Archive。

下面的代码展示了如何从一个类找到它的加载的位置的技巧：

```
protected final Archive createArchive() throws Exception {
    ProtectionDomain protectionDomain = getClass().getProtectionDomain();
    CodeSource codeSource = protectionDomain.getCodeSource();
    URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
    String path = (location == null ? null : location.getSchemeSpecificPart());
    if (path == null) {
        throw new IllegalStateException("Unable to determine code source archive");
    }
    File root = new File(path);
    if (!root.exists()) {
        throw new IllegalStateException(
                "Unable to determine code source archive from " + root);
    }
    return (root.isDirectory() ? new ExplodedArchive(root)
            : new JarFileArchive(root));
}

```

获取lib/下面的jar，并创建一个LaunchedURLClassLoader
JarLauncher创建好Archive之后，通过getNestedArchives函数来获取到demo-0.0.1-SNAPSHOT.jar/lib下面的所有jar文件，并创建为List。

注意上面提到，Archive都是有自己的URL的。

获取到这些Archive的URL之后，也就获得了一个URL[]数组，用这个来构造一个自定义的ClassLoader：LaunchedURLClassLoader。

创建好ClassLoader之后，再从MANIFEST.MF里读取到Start-Class，即com.example.SpringBootDemoApplication，然后创建一个新的线程来启动应用的Main函数。

```
/**
 * Launch the application given the archive file and a fully configured classloader.
 */
protected void launch(String[] args, String mainClass, ClassLoader classLoader)
        throws Exception {
    Runnable runner = createMainMethodRunner(mainClass, args, classLoader);
    Thread runnerThread = new Thread(runner);
    runnerThread.setContextClassLoader(classLoader);
    runnerThread.setName(Thread.currentThread().getName());
    runnerThread.start();
}

/**
 * Create the {@code MainMethodRunner} used to launch the application.
 */
protected Runnable createMainMethodRunner(String mainClass, String[] args,
        ClassLoader classLoader) throws Exception {
    Class<?> runnerClass = classLoader.loadClass(RUNNER_CLASS);
    Constructor<?> constructor = runnerClass.getConstructor(String.class,
            String[].class);
    return (Runnable) constructor.newInstance(mainClass, args);
}
```

LaunchedURLClassLoader
LaunchedURLClassLoader和普通的URLClassLoader的不同之处是，它提供了从Archive里加载.class的能力。

结合Archive提供的getEntries函数，就可以获取到Archive里的Resource。当然里面的细节还是很多的，下面再描述。



## 四、Spring Boot 如何创建上下文环境



SpringApplication类是Spring Boot应用的标配，它可以启动Spring应用并加载配置文件，并创建Spring上下文环境


## 五、这句代码是如何完成Spring上下文创建以及相关bean的声明呢？

## 六、那个在主类上标记的奇怪的@SpringBootApplication注解是什么？

 

---