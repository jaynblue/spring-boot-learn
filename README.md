[toc]

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

### @SpringBootConfiguration
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

### @ComponentScan

@ComponentScan这个注解在Spring中很重要，它对应XML配置中的元素，@ComponentScan的功能其实就是自动扫描并加载符合条件的组件（比如@Component和@Repository等）或者bean定义，最终将这些bean定义加载到IoC容器中。

我们可以通过basePackages等属性来细粒度的定制@ComponentScan自动扫描的范围，如果不指定，则默认Spring框架实现会从声明@ComponentScan所在类的package进行扫描。

> 注：所以SpringBoot的启动类最好是放在root package下，因为默认不指定basePackages

### @EnableAutoConfiguration

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

### SpringApplication执行流程,SpringApplication.run();


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
		    // 判断是否是内嵌tomcat 运行.
			if ("main".equals(stackTraceElement.getMethodName())) {
				return Class.forName(stackTraceElement.getClassName());
			}
		}
	}
	catch (ClassNotFoundException ex) {
		// Swallow and continue
	}
	// 如果是使用外部tomcat 运行，则这里返回null
	return null;
}
	
```
- 最后找出main方法的全类名并返回其实例并设置到SpringApplication的this.mainApplicationClass完成初始化。

- 如果是使用独立tomcat 运行 war 上面这个方法返回null。
 
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
           
           //refreshContext,创建 Tomcat					
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
读取 spring-boot 包里 META-INF/spring.factories。
使用SpringFactoriesLoader.loadFactoryNames方法去取上面说的被配置的ApplicationContextInitializer的名字放进Set<String>中，并用反射创建这些名字的实例。
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

	 //这里会去调用这个方法，方法里面有调用创建  Tomcat或者其他容器.
	refresh(context);
	if (this.registerShutdownHook) {
		try {
			context.registerShutdownHook();
		}
		catch (AccessControlException ex) {
			// Not allowed in some environments.
		}
	
```

#### 创建内嵌 Tomcat 的调用过程
``` 
//上面的 refreshContext 方法里面调用 自己类里面refresh 方法

refresh(context); // 这里去 SpringApplication 类的 refresh 方法

/**
 * Refresh the underlying {@link ApplicationContext}.
 * @param applicationContext the application context to refresh
 */
protected void refresh(ApplicationContext applicationContext) {
  
    // 首先是判断context是否是AbstractApplicationContext派生类的实例，
    Assert.isInstanceOf(AbstractApplicationContext.class, applicationContext);
    
    // 之后调用了强转为AbstractApplicationContext类型并调用它的refresh方法
    ((AbstractApplicationContext) applicationContext).refresh();
}


//AbstractApplicationContext 类 refresh 方法
// 这个refresh也就是AbstractApplicationContext的refresh方法了，
// 它内部是一个synchronized锁全局的代码块，
// 同样的加锁方法还有这个类里的close和registerShutdownHook方法。
@Override
public void refresh() throws BeansException, IllegalStateException {
    synchronized (this.startupShutdownMonitor) {
    
        // Prepare this context for refreshing.
        // 同步代码块中第一个方法prepareRefresh，
        // 首先会执行AnnotationConfigEmbeddedWebApplicationContext的prepareRefresh方法：
        prepareRefresh();

        // Tell the subclass to refresh the internal bean factory.
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        // Prepare the bean factory for use in this context.
        prepareBeanFactory(beanFactory);

        try {
            // Allows post-processing of the bean factory in context subclasses.
            postProcessBeanFactory(beanFactory);

            // Invoke factory processors registered as beans in the context.
            invokeBeanFactoryPostProcessors(beanFactory);

            // Register bean processors that intercept bean creation.
            registerBeanPostProcessors(beanFactory);

            // Initialize message source for this context.
            initMessageSource();

            // Initialize event multicaster for this context.
            initApplicationEventMulticaster();

            // Initialize other special beans in specific context subclasses.
            onRefresh();

            // Check for listener beans and register them.
            registerListeners();

            // Instantiate all remaining (non-lazy-init) singletons.
            finishBeanFactoryInitialization(beanFactory);

            // Last step: publish corresponding event.
            finishRefresh();
        }

        catch (BeansException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Exception encountered during context initialization - " +
                        "cancelling refresh attempt: " + ex);
            }

            // Destroy already created singletons to avoid dangling resources.
            destroyBeans();

            // Reset 'active' flag.
            cancelRefresh(ex);

            // Propagate exception to caller.
            throw ex;
        }

        finally {
            // Reset common introspection caches in Spring's core, since we
            // might not ever need metadata for singleton beans anymore...
            resetCommonCaches();
        }
    }
}

```

> Spring boot 1.x
由于AnnotationConfigEmbeddedWebApplicationContext继承自EmbeddedWebApplicationContext，
所以会执行EmbeddedWebApplicationContext的refresh方法，继而执行其中的super.refresh。

Spring boot 2.0
如下图
由于 AnnotationConfigServletWebServerApplicationContext 继承ServletWebServerApplicationContext，
所以会执行 ServletWebServerApplicationContext的refresh 方法，而 ServletWebServerApplicationContext 

![image](http://note.youdao.com/yws/res/3481/WEBRESOURCEedba6f4c94a9fb3ec922e8c78e2c5089)

AnnotationConfigServletWebServerApplicationContext 继承 ServletWebServerApplicationContext 继承 GenericWebApplicationContext
继承 GenericApplicationContext 继承 抽象类 AbstractApplicationContext

同步代码块中第一个方法prepareRefresh，首先会执行AnnotationConfigServletWebServerApplicationContext的prepareRefresh方法：

``` 
protected void prepareRefresh() {
    this.scanner.clearCache();
    super.prepareRefresh();
}
```

这个super也就是AbstractApplicationContext，它的prepareRefresh方法逻辑是：生成启动时间；\
设置closed状态为false；active状态为true；\
initPropertySources方法主要是调用了AbstractEnvironment的getPropertySources方法\
获取了之前SpringApplication的prepareEnvironment方法中getOrCreateEnvironment方法准备的各种环境变量及配置并用于初始化ServletPropertySources。\
具体的servletContextInitParams这些是在环境对象初始化时由各集成级别Environment的customizePropertySources方法中初始化的。

![image](https://images2015.cnblogs.com/blog/445166/201701/445166-20170117171816396-498355230.png)

- 接着的getEnvironment().validateRequiredProperties()方法实际执行了AbstractEnvironment中的this.propertyResolver.validateRequiredProperties()，
主要是验证了被占位的key如果是required的值不能为null。\
prepareRefresh的最后是初始化this.earlyApplicationEvents = new LinkedHashSet<ApplicationEvent>()。*****
``` 
protected void prepareRefresh() {
    this.startupDate = System.currentTimeMillis();
    this.closed.set(false);
    this.active.set(true);

    if (logger.isInfoEnabled()) {
        logger.info("Refreshing " + this);
    }

    // Initialize any placeholder property sources in the context environment
    initPropertySources();

    // Validate that all properties marked as required are resolvable
    // see ConfigurablePropertyResolver#setRequiredProperties
    
    // 实际执行了AbstractEnvironment中的this.propertyResolver.validateRequiredProperties()，
    // 主要是验证了被占位的key如果是required的值不能为null
    getEnvironment().validateRequiredProperties();

    // Allow for the collection of early ApplicationEvents,
    // to be published once the multicaster is available...
    //最后是初始化t
    this.earlyApplicationEvents = new LinkedHashSet<>();
}
```
- 只够是获取BeanFactory实例的方法obtainFreshBeanFactory()，
首先在refreshBeanFactory方法中用原子布尔类型判断是否刷新过，\
BeanFactory实例是在createApplicationContext创建Context实例时被创建的，如果没有刷新则设置一个用于序列化的id，\
id是ContextIdApplicationContextInitializer初始化设置的（如未配置该初始化器，是有一个默认ObjectUtils.identityToString(this)生成的），\
这个id的生成规则是spring.config.name截取的+":"+server.port的占位截取。设置序列化id时，同时保存了一个id和弱引用DefaultListableBeanFactory实例映射。

``` 
/**
 * Tell the subclass to refresh the internal bean factory.
 * @return the fresh BeanFactory instance
 * @see #refreshBeanFactory()
 * @see #getBeanFactory()
 */
protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {

    // 首先在refreshBeanFactory方法中用原子布尔类型判断是否刷新过
    refreshBeanFactory();
   
    // BeanFactory实例是在createApplicationContext创建Context实例时被创建的，
    // 如果没有刷新则设置一个用于序列化的id
    ConfigurableListableBeanFactory beanFactory = getBeanFactory();
    if (logger.isDebugEnabled()) {
        logger.debug("Bean factory for " + getDisplayName() + ": " + beanFactory);
    }
    return beanFactory;
}
```


- 得到了beanFactory后就是prepareBeanFactory(beanFactory)了，
逻辑是注册了BeanClassLoader用于注入的bean实例的创建；\
StandardBeanExpressionResolver用于EL表达式，比如配置文件或者@Value("#{...}")等使用；\
用ResourceEditorRegistrar注册属性转换器，比如xml配置的bean属性都是用的字符串配置的要转成真正的属性类型；\

addBeanPostProcessor(new ApplicationContextAwareProcessor(this))注册ApplicationContextAwareProcessor，\
它的invokeAwareInterfaces方法会对实现指定接口的bean调用指定的set方法；

ignoreDependencyInterface忽略对这些接口的自动装配，\
比如Aware这些是要做独立处理的，不适合通用的方法；然后是有几个类型直接手动注册，比如BeanFactory，这个很好理解；\

接着注册一个后置处理器ApplicationListenerDetector的实例，addBeanPostProcessor注册的会按照注册先后顺序执行；\
这个方法的最后判断了特定的4个bean名字，如果存在会做相应注册，包括loadTimeWeaver、environment、systemProperties和systemEnvironment。\

补充一点，在最开始创建实例的时候还执行过ignoreDependencyInterface(BeanNameAware.class);ignoreDependencyInterface(BeanFactoryAware.class);
ignoreDependencyInterface(BeanClassLoaderAware.class)。
``` 
/**
 * Configure the factory's standard context characteristics,
 * such as the context's ClassLoader and post-processors.
 * @param beanFactory the BeanFactory to configure
 *
 *  逻辑是注册了BeanClassLoader用于注入的bean实例的创建
 */
protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    // Tell the internal bean factory to use the context's class loader etc.
    beanFactory.setBeanClassLoader(getClassLoader());
    
    //注册BeanClassLoader用于EL表达式
    beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
    
    //注册属性转换器，
    beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));

    // Configure the bean factory with context callbacks.
    //注册ApplicationContextAwareProcessor
    beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
    
    //忽略对这些接口的自动装配
    beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
    beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
    beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
    beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
    beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
    beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);

    // BeanFactory interface not registered as resolvable type in a plain factory.
    // MessageSource registered (and found for autowiring) as a bean.
    beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
    beanFactory.registerResolvableDependency(ResourceLoader.class, this);
    beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
    beanFactory.registerResolvableDependency(ApplicationContext.class, this);

    // Register early post-processor for detecting inner beans as ApplicationListeners.
    // 注册一个后置处理器ApplicationListenerDetector的实例
    beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));

    // Detect a LoadTimeWeaver and prepare for weaving, if found.
    if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
        beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
        // Set a temporary ClassLoader for type matching.
        beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
    }

    // Register default environment beans.
    if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
        beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
    }
    if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
        beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
    }
    if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
        beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
    }
}

```

之后到了refresh的postProcessBeanFactory方法，首先是会走到 AnnotationConfigServletWebServerApplicationContext 
(AnnotationConfigEmbeddedWebApplicationContext)的Override，
需要注意的一点是，这是web环境，如果不是是不会加载这个上下文的，也就不会这么走。
它重写的第一步是先走super也就是ServletWebServerApplicationContext(EmbeddedWebApplicationContext)的postProcessBeanFactory，
>括号里类是spring boot 1.x 

这里又注册了个后置处理器WebApplicationContextServletContextAwareProcessor的实例，构造参数是this，也就是当前上下文，同时忽略ServletContextAware接口，
这个接口是用于获取ServletContext的，为什么要忽略呢，我猜应该是因为我们既然有了web应用并且内嵌servlet的上下文实例，还要ServletContext的实现就没什么用了，
还有可能出现冲突的问题，有空我再确认下。

然后是配置的basePackages和annotatedClasses：
``` 
@Override
protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    super.postProcessBeanFactory(beanFactory);
    if (this.basePackages != null && this.basePackages.length > 0) {
        this.scanner.scan(this.basePackages);
    }
    if (!this.annotatedClasses.isEmpty()) {
        this.reader.register(ClassUtils.toClassArray(this.annotatedClasses));
    }
}
```


- 到了invokeBeanFactoryPostProcessors方法，这个方法就是执行之前注册的BeanFactory后置处理器的地方。
代码一目了然，PostProcessorRegistrationDelegate的invokeBeanFactoryPostProcessors中只是有些排序的逻辑，我就不说了：

``` 
/**
 * Instantiate and invoke all registered BeanFactoryPostProcessor beans,
 * respecting explicit order if given.
 * <p>Must be called before singleton instantiation.
 */
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());

    // Detect a LoadTimeWeaver and prepare for weaving, if found in the meantime
    // (e.g. through an @Bean method registered by ConfigurationClassPostProcessor)
    if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
        beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
        beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
    }
}
```

BeanFactory后置处理器执行之后是注册Bean的后置处理器方法registerBeanPostProcessors。
例如new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount)会在Bean没有合适的后置处理器时记条info级日志。
ApplicationListenerDetector也注册了一个。


initMessageSource这个方法在我这没什么用，都说是国际化的，随便搜索一下一堆一堆的，而且其实严格来说这篇多数不属于spring boot的部分，这方法我就不细写了。

initApplicationEventMulticaster方法主要也就是初始化并注册applicationEventMulticaster的这两句代码：
``` 
 this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
 beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
 
```

onRefresh也是根据环境不同加载的上下文不同而不同的，用于支持子类扩展出来的上下文特定的逻辑的。\
EmbeddedWebApplicationContext的onRefresh首先依然是super.onRefresh，逻辑就是初始化了主题；\
createEmbeddedServletContainer方法名我就不翻译了， \
一般情况下是使用getBeanFactory .getBeanNamesForType方法找到EmbeddedServletContainerFactory类型的实例， \
这也就是我之前那个问题解决过程中，为什么只要排除掉tomcat引用，引入jetty引用就可以自动换成jetty的原因。 \
创建容器的过程中初始化方法selfInitialize注册了filter和MappingForUrlPatterns等，代码在AbstractFilterRegistrationBean等onStartup，\
这里就不细说了，如果能抽出时间说说之前查问题的时候查的容器代码再说。

然后初始化PropertySources，servletContextInitParams和servletConfigInitParams：

``` 
@Override
protected void onRefresh() {
    super.onRefresh();
    try {
        createWebServer();
    }
    catch (Throwable ex) {
        throw new ApplicationContextException("Unable to start web server", ex);
    }
}
```


registerListeners首先注册静态监听：
``` 
/**
 * Add beans that implement ApplicationListener as listeners.
 * Doesn't affect other listeners, which can be added without being beans.
 */
protected void registerListeners() {
    // Register statically specified listeners first.
    for (ApplicationListener<?> listener : getApplicationListeners()) {
        getApplicationEventMulticaster().addApplicationListener(listener);
    }

    // Do not initialize FactoryBeans here: We need to leave all regular beans
    // uninitialized to let post-processors apply to them!
    String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
    for (String listenerBeanName : listenerBeanNames) {
        getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
    }

    // Publish early application events now that we finally have a multicaster...
    Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
    this.earlyApplicationEvents = null;
    if (earlyEventsToProcess != null) {
        for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
            getApplicationEventMulticaster().multicastEvent(earlyEvent);
        }
    }
}
```
registerListeners的最后，初始化过的earlyApplicationEvents如果有事件，这时候会被发布。




finishBeanFactoryInitialization结束BeanFactory的初始化并初始化所有非延迟加载的单例。\
事实上我们自定义的单例Bean都是在这里getBean方法初始化的，所以如果注册的Bean特别多的话，这个过程就是启动过程中最慢的。\
初始化开始前先设置configurationFrozen为true，并this.frozenBeanDefinitionNames = StringUtils.toStringArray ( this. beanDefinitionNames )。\
如果有bean实例实现了SmartInitializingSingleton会有后置处理触发，不包括延迟加载的。\
例如：org.springframework.context.event. internalEventListenerProcessor会触发
EventListenerMethodProcessor的afterSingletonsInstantiated方法对所有对象（Object的子类）处理。

``` 
/**
 * Finish the initialization of this context's bean factory,
 * initializing all remaining singleton beans.
 * finishBeanFactoryInitialization结束BeanFactory的初始化并初始化所有非延迟加载的单例.
 *
 * 事实上我们自定义的单例Bean都是在这里getBean方法初始化的，所以如果注册的Bean特别多的话，这个过程就是启动过程中最慢的
 */
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
    // Initialize conversion service for this context.
    if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
            beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
        beanFactory.setConversionService(
                beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
    }

    // Register a default embedded value resolver if no bean post-processor
    // (such as a PropertyPlaceholderConfigurer bean) registered any before:
    // at this point, primarily for resolution in annotation attribute values.
    if (!beanFactory.hasEmbeddedValueResolver()) {
        beanFactory.addEmbeddedValueResolver(strVal -> getEnvironment().resolvePlaceholders(strVal));
    }

    // Initialize LoadTimeWeaverAware beans early to allow for registering their transformers early.
    String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
    for (String weaverAwareName : weaverAwareNames) {
        getBean(weaverAwareName);
    }

    // Stop using the temporary ClassLoader for type matching.
    beanFactory.setTempClassLoader(null);

    // Allow for caching all bean definition metadata, not expecting further changes.
    beanFactory.freezeConfiguration();

    // Instantiate all remaining (non-lazy-init) singletons.
    beanFactory.preInstantiateSingletons();
}

```
finishRefresh:Refresh的最后一步，发布相应事件。\
同样先执行EmbeddedWebApplicationContext中对应方法的super ServletWebServerApplicationContext (EmbeddedWebApplicationContext)的对应方法：
```
@Override
protected void finishRefresh() {

    // 调用父类方法
    super.finishRefresh();
    
    // 启动前面创建的内嵌容器,
    WebServer webServer = startWebServer();
    if (webServer != null) {
        publishEvent(new ServletWebServerInitializedEvent(webServer, this));
    }
}

// ServletWebServerApplicationContext 类
private WebServer startWebServer() {
    WebServer webServer = this.webServer;
    
    // 如果是独立tomcat，则这里是null,不需要启动,TomcatWebServer 等实现了这个WebServer 接口
    if (webServer != null) {
        webServer.start();
    }
    return webServer;
}
	
```

``` 
/**
 * Finish the refresh of this context, invoking the LifecycleProcessor's
 * onRefresh() method and publishing the
 * {@link org.springframework.context.event.ContextRefreshedEvent}.
 */
protected void finishRefresh() {
    // Clear context-level resource caches (such as ASM metadata from scanning).
    clearResourceCaches();

    // Initialize lifecycle processor for this context.
    initLifecycleProcessor();

    // Propagate refresh to lifecycle processor first.
    getLifecycleProcessor().onRefresh();

    // Publish the final event.
    publishEvent(new ContextRefreshedEvent(this));

    // Participate in LiveBeansView MBean, if active.
    // 初始化生命周期处理器
    LiveBeansView.registerApplicationContext(this);
}

```
初始化生命周期处理器，逻辑是判断beanFactory中是否已经注册了lifecycleProcessor，\
没有就new一个DefaultLifecycleProcessor并setBeanFactory(beanFactory)， \
然后将它赋值给私有LifecycleProcessor类型的this变量。然后执行生命周期处理器的onRefresh， \
其中先startBeans，被start的beans是通过getBeanNamesForType(Lifecycle.class, false, false)从beanFactory中取出来的，\
例如endpointMBeanExporter和lifecycleProcessor，会去调用bean的start方法， \
endpointMBeanExporter的start中执行 locateAndRegisterEndpoints方法并设置running属性为true，\
这个过程加了ReentrantLock锁。bean都启动完会设置处理器的running为true。 \
刷新完会发布ContextRefreshedEvent事件，这个事件除了都有的记录时间还执行了ConfigurationPropertiesBindingPostProcessor的freeLocalValidator方法，\
我这的逻辑是实际上执行了ValidatorFactoryImpl的close方法。 \
这个逻辑的最后会检查一个配置spring.liveBeansView.mbeanDomain是否存在，有就会创建一个MBeanServe
``` 

static void registerApplicationContext(ConfigurableApplicationContext applicationContext) {
    String mbeanDomain = applicationContext.getEnvironment().getProperty(MBEAN_DOMAIN_PROPERTY_NAME);
    if (mbeanDomain != null) {
        synchronized (applicationContexts) {
            if (applicationContexts.isEmpty()) {
                try {
                    MBeanServer server = ManagementFactory.getPlatformMBeanServer();
                    applicationName = applicationContext.getApplicationName();
                    server.registerMBean(new LiveBeansView(),
                            new ObjectName(mbeanDomain, MBEAN_APPLICATION_KEY, applicationName));
                }
                catch (Throwable ex) {
                    throw new ApplicationContextException("Failed to register LiveBeansView MBean", ex);
                }
            }
            applicationContexts.add(applicationContext);
        }
    }
}
```

###### finishRefresh最后会启动前面创建的内嵌容器，并发布EmbeddedServletContainerInitializedEvent事件，
启动这一部分算是容器的逻辑了，有机会整理容器逻辑再细写，我这里是Tomcat的：



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
 

## 二、 Spring boot 如何判断内嵌Tomcat启动 ? 还是独立tomcat 启动 war? 或者是 jar 方式启动?

spring-boot默认提供内嵌的tomcat，所以打包直接生成jar包，
用java -jar命令就可以启动。
但是，有时候我们更希望一个tomcat来管理多个项目，
这种情况下就需要项目是war格式的包而不是jar格式的包。
spring-boot同样提供了解决方案，只需要简单的几步更改就可以了，
这里提供maven项目的解决方法：
 
```
@SpringBootApplication
public class ServiceApplication extends SpringBootServletInitializer{

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ServiceApplication.class);
    }
}
```
打包的时候记得设置成war 方式.


这是 Boot 使用的三个embedded 容器，默认启动的是 Tomcat。

![image](https://ss.csdn.net/p?http://mmbiz.qpic.cn/mmbiz_png/8yRv8Dibia2spOb5dfToEPNB8VpvyA1ianA2HKK5K2vfFtxYc0NKMd4VMbH5Du6u73DL41wFNtcAiaPZgAkYa9jYTA/640?wx_fmt=png&wxfrom=5&wx_lazy=1)

要分析这个问题，该从哪看起呢？

Boot 在启动的时候，很清楚的告诉我们这样一条信息

s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat initialized with port 

我们看到的这一条是logback输出的信息。前面是缩略形式写的包名，最主要的是这个Container，跳转到类里看一眼。

可以匹配到这一行 log 的, 是 container 的init 方法

![](https://ss.csdn.net/p?https://mmbiz.qpic.cn/mmbiz_png/8yRv8Dibia2spOb5dfToEPNB8VpvyA1ianAlYXVgotdiarCBjozD9b1TGL4MCLS5qb0KexZibkuLMHUerhdHcxmAXeA/640?wx_fmt=png)

前面一些细节类的内容先不过多关注，进入眼里的， 一定是这个

this.tomcat.start();

这里这个 tomcat ，就是 Embedded Tomcat类的实例。

这里 start 的操作，是将 容器启动起来

![](https://ss.csdn.net/p?https://mmbiz.qpic.cn/mmbiz_png/8yRv8Dibia2spOb5dfToEPNB8VpvyA1ianADEEeCVPB8622aGjduBzpmUaHbn12Vg7xeXCzCj2QX4uMgchOerhDmw/640?wx_fmt=png)


我们知道，Spring MVC 是通过 DispatcherServlet 来分发处理请求，
在 Spring Boot 出现之前，都是需要在web.xml里配置，来实现请求的拦截。

而在Servlet 3.0 之后，规则中新增了Dynamic Servlet、Dynamic Filter这些概念,
 可以在运行时动态注册组件到 Context 中。

```
```

所以我们观察到的 Context 仅仅是一个空的应用，
然后再通过动态添加Servlet、 Filter 等内容进去。

除了以 Jar 的形式直接执行 Main 方法外， 
Spring Boot 还支持将 Boot 应用打包成 War 文件，
部署到标准和容器中，不使用 Embedded 容器。


<https://blog.csdn.net/bntX2jSQfEHy7/article/details/79385689>

 




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



## Tomcat 加载

我们知道，一个Java应用是需要一个main函数的作为入口来执行。
但是我们平时写Web项目好像没有写过main函数。
你可能有疑问，那它是怎么跑起来的？
是不是用了什么特别的黑科技，没有用main函数？
其实不是这样的，我们写web项目，少不了一个Web的应用容器，
例如Tomcat， JBoss等等。main函数其实是在这些容器里面。

例如Tomcat,它的主类叫做Bootstrap，main函数就写在这个主类里面.

 
 在Tomcat应用服务器启动以后。
 它会通过反射，利用ClassLoader来加载Web-App文件夹下面的Web应用的jar包。
 
 
 
 ### Tomcat加载Web应用？
 
 我们知道，每个Web应用都有一个自己唯一的一个ServletContext，
 在Tomcat里面，SerlvetContext的实现类是ApplicationContext。
 看名字也可以知道这代表着一个Application应用的上下文环境。
 我们现在只关心这个类就可以了。
 这个类里面有个类型为StandardContext,这是Context标准实现。
 
 说到这个类，然后我们就需要谈起一个接口ServletContextListener，
 这个接口用来提供一个观察者模式，简单的讲就是用来监控ServletContext的启动，
 销毁生命周期的。
 
 看一下Tomcat所有容器的抽象类里面的ContainerMBean类里面的addChild方法，
 这个StandardContext其实就是一个ServletContext，即为一个应用容器。
 
 
```
//为容器添加子容器
public void addChild(String type, String name) throws MBeanException{

    Container contained = (Container) newInstance(type);
    contained.setName(name);

    if(contained instanceof StandardHost){
        HostConfig config = new HostConfig();
        contained.addLifecycleListener(config);
    } else if(contained instanceof StandardContext){
        //添加的容器是一个Tomcat子容器的话，就分配其一个ContextCofig
        ContextConfig config = new ContextConfig();
        //将ContextConfig添加到Container的监听者行列中
        contained.addLifecycleListener(config);
    }

    boolean oldValue= true;

    ContainerBase container = doGetManagedResource();
    try {
        oldValue = container.getStartChildren();
        container.setStartChildren(false);
        container.addChild(contained);
        /*
            开始一个初始化，会通知所有正在监听Container的观察者
            对于ContextConfig来说，现在应该做的是加载配置等
        */
        contained.init();
    } catch (LifecycleException e){
        throw new MBeanException(e);
    } finally {
        if(container != null) {
            container.setStartChildren(oldValue);
        }
    }
}

```
 Tomcat 在为Host容器添加Context子容器时，会为其分配一个ContextConfig类。
 当你看到这个类名应该就会想到，这应该是和Web配置加载有关的一个类.
 ```
@Override
 public void lifecycleEvent(LifecycleEvent event) {

     ......省略无用代码......
     // Process the event that has occurred
     if (event.getType().equals(Lifecycle.CONFIGURE_START_EVENT)) {
         //当监听的容器发送，CONFIGURE_START_EVENT时，配置开始
         configureStart();
     } 
     .....省略无用代码.......
 }

 ```
 再来看看configureStart()方法
 
```
protected synchronized void configureStart() {
        
        ....省略无用代码·····     
    
        webConfig();

        if (!context.getIgnoreAnnotations()) {
            applicationAnnotationsConfig();
        }
        ·····省略无用代码·····
}

```

从代码中可以看到，Tomcat先是从调用WebConfig()函数.
这个函数的主要的动作就是读取web.xml配置文件，

函数webConfig()的执行动作的流程
读取web-fragment.xml和各个jar模块
排序所有读取到的fragments
- 查找所有的ServletContainerInitializer(SCIs)
- 处理WEB-INF/Classes文件夹下面的
- 处理所有的注解配置类和，并缓存
- 将所有的web-fragment.xml合并
- 转换所有的JSP代码成Java代码
- 将Web.xml配置转变成代码式的配置
- 查找静态资源默认文件夹 WEB-INF/classes/META-INF/resources
- 将所有的实现ServletContainerInitializers的类添加到StandardContext的initializers集合中

我们在这里重点关注 第三步和第十三步，
webConfig()中会调用processServletContainerInitializers()
这个方法即为加载所有的经过 @HandlesTypes注解的类。

```
protected void webConfig() {

    ....

    // Step 3. 查找ServletContainerInitializers
    if (ok) {
        processServletContainerInitializers();
    }


   ......

    // Step 11. ServletContainerInitializer 交给StandardContext去处理！在这里即为我们的应用所在的容器
    if (ok) {
        for (Map.Entry<ServletContainerInitializer,
                Set<Class<?>>> entry :
                    initializerClassMap.entrySet()) {
            if (entry.getValue().isEmpty()) {
                context.addServletContainerInitializer(
                        entry.getKey(), null);
            } else {
                context.addServletContainerInitializer(
                        entry.getKey(), entry.getValue());
            }
        }
    }
}
  ```

我们去StandardContext里面去看一下它是怎么处理这个集合属性的。在StandardContext类里面的startInternel()方法，就是启动这个

```
@Override
protected synchronized void startInternal() throws LifecycleException {
        ....
        // Call ServletContainerInitializers,启动这些
        for (Map.Entry<ServletContainerInitializer, Set<Class<?>>> entry :
            initializers.entrySet()) {
            try {
      //启动所有的ServletContainerInitializer
                entry.getKey().onStartup(entry.getValue(),
                        getServletContext());
            } catch (ServletException e) {
                log.error(sm.getString("standardContext.sciFail"), e);
                ok = false;
                break;
            }
        }
        ...
}
```
即: 通过内部启动时，它会通知所有正在监听的 ServletContainerInitializers,这样，
即完成了Web应用的加载和初始化的配置！

总结
Tomcat的Host容器在添加子容器时，
会通过解析.xml并通过classloader加载 @HandlesTypes注解的类
读取@HandlesTypes注解value值。
并放入ServletContainerInitializers 对应的Set集合中
在ApplicationContext 内部启动时会通知 
ServletContainerInitializers 的onStart方法()。
这个onStart方法的第一个参数就是@HandlesTypes注解的value 值指定的Class集合
在Spring 应用中，对ServletContainerInitializers的实现就是SpringServletContainerInitializer,
注解指定的类就是WebApplicationInitializer.
 
---
