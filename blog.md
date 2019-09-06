# Springboot—Dubbo简单示例


## 创建接口

在 -interface 模块
```
public interface UserService {
    User getUserById(int id);
}
```

## 创建生产者

在 -impl 模块

引入 interface
```
<dependency>
	<groupId>com.ran.demo</groupId>
	<artifactId>demoDubboProducer-interface</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```
引入 dubbo
```
<!-- dubbo -->
<dependency>
	<groupId>com.alibaba.boot</groupId>
	<artifactId>dubbo-spring-boot-starter</artifactId>
	<version>0.2.0</version>
</dependency>
```

编写实现类
```
import com.alibaba.dubbo.config.annotation.Service;
import com.example.dubboproducer.dao.UserDao;
import com.example.pojo.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Service //这里是dubbo的注解, 将接口暴露在外, 可以供调用, 这里就是放在了注册中心
//@org.springframework.stereotype.Service   spring自己的注解, 为了区分, 使用了@Component
@Component //让spring扫描该类, 用spring的 @Service也行
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User getUserById(int id) {
        return userDao.selectUserById(id);
    }
}
```

properties
```
dubbo.application.name=demoDubboProducer
dubbo.registry.address=zookeeper://127.0.0.1:2181
```

启动Dubbo配置
```
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo  //启动dubbo的配置
public class DubboProducerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubboProducerApplication.class, args);
    }
}
```

生产者项目目录
```
├── demoDubboProducer-interface
│   ├── demoDubboProducer-interface.iml
│   ├── pom.xml
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com
│   │   │   │       └── ran
│   │   │   │           └── demo
│   │   │   │               └── dubboproducer
│   │   │   │                   ├── User.java
│   │   │   │                   └── UserService.java
│   │   │   └── resources
├── demoDubboProducer-impl
│   ├── pom.xml
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com
│   │   │   │       └── ran
│   │   │   │           └── demo
│   │   │   │               └── dubboproducer
│   │   │   │                   ├── DemoDubboProducerApplication.java
│   │   │   │                   └── UserServiceImpl.java
│   │   │   └── resources
│   │   │       ├── application.properties
│   │   │       └── logback-spring.xml
└── pom.xml
```


## 创建消费者

引入 interface
```
<dependency>
	<groupId>com.ran.demo</groupId>
	<artifactId>demoDubboProducer-interface</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```
引入 dubbo
```
<!-- dubbo -->
<dependency>
	<groupId>com.alibaba.boot</groupId>
	<artifactId>dubbo-spring-boot-starter</artifactId>
	<version>0.2.0</version>
</dependency>
```

properties
```
dubbo.application.name=demoDubboConsumer
dubbo.registry.address=zookeeper://127.0.0.1:2181
```

创建controller, 完成调用消费者
```
import com.alibaba.dubbo.config.annotation.Reference;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference  //将生产者注册到Dubbo映射到该对象上, 这里完成了服务之间的调用
    private UserService userService;

    @RequestMapping("/getUserById")
    public Object getUserById (int id){
       return userService.getUserById(id);
    }
}
```

消费者项目目录
```
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── ran
│   │   │           └── demo
│   │   │               └── dubboconsumer
│   │   │                   ├── DemoDubboConsumerApplication.java
│   │   │                   └── UserController.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── logback-spring.xml
```

## 测试

### 启动生产者

查看 zookeeper 中注册的服务
```
[zk: localhost:2181(CONNECTED) 0] ls /
[dubbo, zookeeper]
[zk: localhost:2181(CONNECTED) 1] ls /dubbo
[com.ran.demo.dubboproducer.UserService]
```

### 启动消费者

```
ran@RandeMacBook-Pro:~$ curl http://127.0.0.1:8081/user/getUserById?id=1
{"id":1,"name":"u1","age":10}
ran@RandeMacBook-Pro:~$ curl http://127.0.0.1:8081/user/getUserById?id=2
{"id":2,"name":"u2","age":20}
ran@RandeMacBook-Pro:~$ curl http://127.0.0.1:8081/user/getUserById?id=3
{"id":3,"name":"u3","age":30}
```

## 附

### 生成者启动日志

```
Connected to the target VM, address: '127.0.0.1:65166', transport: 'socket'
15:58:38.730 demoLogback [main] INFO  c.a.d.common.logger.LoggerFactory - using logger: com.alibaba.dubbo.common.logger.slf4j.Slf4jLoggerAdapter
15:58:38.735 demoLogback [main] INFO  c.a.b.d.c.e.WelcomeLogoApplicationListener - 

 :: Dubbo Spring Boot (v0.2.0) : https://github.com/apache/incubator-dubbo-spring-boot-project
 :: Dubbo (v2.6.2) : https://github.com/apache/incubator-dubbo
 :: Google group : dev@dubbo.incubator.apache.org

15:58:38.738 demoLogback [main] INFO  c.a.b.d.c.e.OverrideDubboConfigApplicationListener - Dubbo Config was overridden by externalized configuration {dubbo.application.name=demoDubboProducer, dubbo.registry.address=zookeeper://127.0.0.1:2181}

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.1.4.RELEASE)

15:58:38.812 demoLogback [main] INFO  c.r.d.d.DemoDubboProducerApplication - Starting DemoDubboProducerApplication on RandeMacBook-Pro.local with PID 35933 (/Users/ran/Documents/github_src/demoDubboProducer/demoDubboProducer-impl/target/classes started by ran in /Users/ran/Documents/github_src/demoDubboProducer)
15:58:38.813 demoLogback [main] INFO  c.r.d.d.DemoDubboProducerApplication - No active profile set, falling back to default profiles: default
15:58:39.183 demoLogback [main] INFO  c.a.d.c.s.c.a.DubboConfigBindingRegistrar - The dubbo config bean definition [name : com.alibaba.dubbo.config.ApplicationConfig#0, class : com.alibaba.dubbo.config.ApplicationConfig] has been registered.
15:58:39.184 demoLogback [main] INFO  c.a.d.c.s.c.a.DubboConfigBindingRegistrar - The BeanPostProcessor bean definition [com.alibaba.dubbo.config.spring.beans.factory.annotation.DubboConfigBindingBeanPostProcessor] for dubbo config bean [name : com.alibaba.dubbo.config.ApplicationConfig#0] has been registered.
15:58:39.185 demoLogback [main] INFO  c.a.d.c.s.c.a.DubboConfigBindingRegistrar - The dubbo config bean definition [name : com.alibaba.dubbo.config.RegistryConfig#0, class : com.alibaba.dubbo.config.RegistryConfig] has been registered.
15:58:39.185 demoLogback [main] INFO  c.a.d.c.s.c.a.DubboConfigBindingRegistrar - The BeanPostProcessor bean definition [com.alibaba.dubbo.config.spring.beans.factory.annotation.DubboConfigBindingBeanPostProcessor] for dubbo config bean [name : com.alibaba.dubbo.config.RegistryConfig#0] has been registered.
15:58:39.517 demoLogback [main] INFO  c.a.d.c.s.b.f.a.ServiceAnnotationBeanPostProcessor -  [DUBBO] BeanNameGenerator bean can't be found in BeanFactory with name [org.springframework.context.annotation.internalConfigurationBeanNameGenerator], dubbo version: 2.6.2, current host: 172.16.42.209
15:58:39.517 demoLogback [main] INFO  c.a.d.c.s.b.f.a.ServiceAnnotationBeanPostProcessor -  [DUBBO] BeanNameGenerator will be a instance of org.springframework.context.annotation.AnnotationBeanNameGenerator , it maybe a potential problem on bean name generation., dubbo version: 2.6.2, current host: 172.16.42.209
15:58:39.525 demoLogback [main] WARN  c.a.d.c.s.b.f.a.ServiceAnnotationBeanPostProcessor -  [DUBBO] The BeanDefinition[Root bean: class [com.alibaba.dubbo.config.spring.ServiceBean]; scope=; abstract=false; lazyInit=false; autowireMode=0; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=null; factoryMethodName=null; initMethodName=null; destroyMethodName=null] of ServiceBean has been registered with name : ServiceBean:userServiceImpl:com.ran.demo.dubboproducer.UserService, dubbo version: 2.6.2, current host: 172.16.42.209
15:58:39.525 demoLogback [main] INFO  c.a.d.c.s.b.f.a.ServiceAnnotationBeanPostProcessor -  [DUBBO] 1 annotated Dubbo's @Service Components { [Bean definition with name 'userServiceImpl': Generic bean: class [com.ran.demo.dubboproducer.UserServiceImpl]; scope=; abstract=false; lazyInit=false; autowireMode=0; dependencyCheck=0; autowireCandidate=true; primary=false; factoryBeanName=null; factoryMethodName=null; initMethodName=null; destroyMethodName=null; defined in file [/Users/ran/Documents/github_src/demoDubboProducer/demoDubboProducer-impl/target/classes/com/ran/demo/dubboproducer/UserServiceImpl.class]] } were scanned under package[com.ran.demo.dubboproducer], dubbo version: 2.6.2, current host: 172.16.42.209
15:58:39.656 demoLogback [main] INFO  o.s.c.s.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'com.alibaba.boot.dubbo.autoconfigure.DubboAutoConfiguration' of type [com.alibaba.boot.dubbo.autoconfigure.DubboAutoConfiguration$$EnhancerBySpringCGLIB$$1fabf535] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
15:58:39.667 demoLogback [main] INFO  o.s.c.s.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'relaxedDubboConfigBinder' of type [com.alibaba.boot.dubbo.autoconfigure.RelaxedDubboConfigBinder] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
15:58:39.668 demoLogback [main] INFO  o.s.c.s.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'relaxedDubboConfigBinder' of type [com.alibaba.boot.dubbo.autoconfigure.RelaxedDubboConfigBinder] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
15:58:39.993 demoLogback [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat initialized with port(s): 8080 (http)
15:58:40.007 demoLogback [main] INFO  o.a.coyote.http11.Http11NioProtocol - Initializing ProtocolHandler ["http-nio-8080"]
15:58:40.016 demoLogback [main] INFO  o.a.catalina.core.StandardService - Starting service [Tomcat]
15:58:40.016 demoLogback [main] INFO  o.a.catalina.core.StandardEngine - Starting Servlet engine: [Apache Tomcat/9.0.17]
15:58:40.110 demoLogback [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring embedded WebApplicationContext
15:58:40.110 demoLogback [main] INFO  o.s.web.context.ContextLoader - Root WebApplicationContext: initialization completed in 1261 ms
15:58:40.226 demoLogback [main] INFO  c.a.d.c.s.b.f.a.DubboConfigBindingBeanPostProcessor - The properties of bean [name : com.alibaba.dubbo.config.ApplicationConfig#0] have been binding by prefix of configuration properties : dubbo.application
15:58:40.232 demoLogback [main] INFO  c.a.d.c.s.b.f.a.DubboConfigBindingBeanPostProcessor - The properties of bean [name : com.alibaba.dubbo.config.RegistryConfig#0] have been binding by prefix of configuration properties : dubbo.registry
15:58:40.484 demoLogback [main] INFO  o.s.s.c.ThreadPoolTaskExecutor - Initializing ExecutorService 'applicationTaskExecutor'
15:58:40.941 demoLogback [main] INFO  c.a.dubbo.config.AbstractConfig -  [DUBBO] The service ready on spring started. service: com.ran.demo.dubboproducer.UserService, dubbo version: 2.6.2, current host: 172.16.42.209
15:58:41.022 demoLogback [main] INFO  c.a.dubbo.config.AbstractConfig -  [DUBBO] Export dubbo service com.ran.demo.dubboproducer.UserService to local registry, dubbo version: 2.6.2, current host: 172.16.42.209
15:58:41.023 demoLogback [main] INFO  c.a.dubbo.config.AbstractConfig -  [DUBBO] Export dubbo service com.ran.demo.dubboproducer.UserService to url dubbo://172.16.42.209:20880/com.ran.demo.dubboproducer.UserService?anyhost=true&application=demoDubboProducer&bind.ip=172.16.42.209&bind.port=20880&dubbo=2.6.2&generic=false&interface=com.ran.demo.dubboproducer.UserService&methods=getUserById,getUserByUser&pid=35933&side=provider&timestamp=1567756720953, dubbo version: 2.6.2, current host: 172.16.42.209
15:58:41.023 demoLogback [main] INFO  c.a.dubbo.config.AbstractConfig -  [DUBBO] Register dubbo service com.ran.demo.dubboproducer.UserService url dubbo://172.16.42.209:20880/com.ran.demo.dubboproducer.UserService?anyhost=true&application=demoDubboProducer&bind.ip=172.16.42.209&bind.port=20880&dubbo=2.6.2&generic=false&interface=com.ran.demo.dubboproducer.UserService&methods=getUserById,getUserByUser&pid=35933&side=provider&timestamp=1567756720953 to registry registry://127.0.0.1:2181/com.alibaba.dubbo.registry.RegistryService?application=demoDubboProducer&dubbo=2.6.2&pid=35933&registry=zookeeper&timestamp=1567756720947, dubbo version: 2.6.2, current host: 172.16.42.209
15:58:41.211 demoLogback [main] INFO  c.a.d.r.transport.AbstractServer -  [DUBBO] Start NettyServer bind /0.0.0.0:20880, export /172.16.42.209:20880, dubbo version: 2.6.2, current host: 172.16.42.209
15:58:41.221 demoLogback [main] INFO  c.a.d.r.zookeeper.ZookeeperRegistry -  [DUBBO] Load registry store file /Users/ran/.dubbo/dubbo-registry-demoDubboProducer-127.0.0.1:2181.cache, data: {com.ran.demo.dubboproducer.UserService=empty://172.16.42.209:20880/com.ran.demo.dubboproducer.UserService?anyhost=true&application=demoDubboProducer&category=configurators&check=false&dubbo=2.6.2&generic=false&interface=com.ran.demo.dubboproducer.UserService&methods=getUserById,getUserByUser&pid=35749&side=provider&timestamp=1567755707934}, dubbo version: 2.6.2, current host: 172.16.42.209
15:58:41.288 demoLogback [main] INFO  o.a.c.f.imps.CuratorFrameworkImpl - Starting
15:58:41.302 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:zookeeper.version=3.4.9-1757313, built on 08/23/2016 06:50 GMT
15:58:41.304 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:host.name=172.16.42.209
15:58:41.304 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:java.version=1.8.0_191
15:58:41.304 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:java.vendor=Oracle Corporation
15:58:41.304 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:java.home=/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre
15:58:41.304 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:java.class.path=/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/deploy.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/cldrdata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/dnsns.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/jaccess.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/jfxrt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/localedata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/nashorn.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/sunec.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/sunjce_provider.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/sunpkcs11.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/zipfs.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/javaws.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/jfxswt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/management-agent.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/plugin.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/ant-javafx.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/dt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/javafx-mx.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/jconsole.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/packager.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/sa-jdi.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/tools.jar:/Users/ran/Documents/github_src/demoDubboProducer/demoDubboProducer-impl/target/classes:/Users/ran/Documents/github_src/demoDubboProducer/demoDubboProducer-interface/target/classes:/Users/ran/mvnrepo/org/springframework/boot/spring-boot-starter-web/2.1.4.RELEASE/spring-boot-starter-web-2.1.4.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/boot/spring-boot-starter/2.1.4.RELEASE/spring-boot-starter-2.1.4.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/boot/spring-boot/2.1.4.RELEASE/spring-boot-2.1.4.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/boot/spring-boot-autoconfigure/2.1.4.RELEASE/spring-boot-autoconfigure-2.1.4.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/boot/spring-boot-starter-logging/2.1.4.RELEASE/spring-boot-starter-logging-2.1.4.RELEASE.jar:/Users/ran/mvnrepo/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar:/Users/ran/mvnrepo/ch/qos/logback/logback-core/1.2.3/logback-core-1.2.3.jar:/Users/ran/mvnrepo/org/apache/logging/log4j/log4j-to-slf4j/2.11.2/log4j-to-slf4j-2.11.2.jar:/Users/ran/mvnrepo/org/apache/logging/log4j/log4j-api/2.11.2/log4j-api-2.11.2.jar:/Users/ran/mvnrepo/org/slf4j/jul-to-slf4j/1.7.26/jul-to-slf4j-1.7.26.jar:/Users/ran/mvnrepo/javax/annotation/javax.annotation-api/1.3.2/javax.annotation-api-1.3.2.jar:/Users/ran/mvnrepo/org/yaml/snakeyaml/1.23/snakeyaml-1.23.jar:/Users/ran/mvnrepo/org/springframework/boot/spring-boot-starter-json/2.1.4.RELEASE/spring-boot-starter-json-2.1.4.RELEASE.jar:/Users/ran/mvnrepo/com/fasterxml/jackson/core/jackson-databind/2.9.8/jackson-databind-2.9.8.jar:/Users/ran/mvnrepo/com/fasterxml/jackson/core/jackson-annotations/2.9.0/jackson-annotations-2.9.0.jar:/Users/ran/mvnrepo/com/fasterxml/jackson/core/jackson-core/2.9.8/jackson-core-2.9.8.jar:/Users/ran/mvnrepo/com/fasterxml/jackson/datatype/jackson-datatype-jdk8/2.9.8/jackson-datatype-jdk8-2.9.8.jar:/Users/ran/mvnrepo/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.9.8/jackson-datatype-jsr310-2.9.8.jar:/Users/ran/mvnrepo/com/fasterxml/jackson/module/jackson-module-parameter-names/2.9.8/jackson-module-parameter-names-2.9.8.jar:/Users/ran/mvnrepo/org/springframework/boot/spring-boot-starter-tomcat/2.1.4.RELEASE/spring-boot-starter-tomcat-2.1.4.RELEASE.jar:/Users/ran/mvnrepo/org/apache/tomcat/embed/tomcat-embed-core/9.0.17/tomcat-embed-core-9.0.17.jar:/Users/ran/mvnrepo/org/apache/tomcat/embed/tomcat-embed-el/9.0.17/tomcat-embed-el-9.0.17.jar:/Users/ran/mvnrepo/org/apache/tomcat/embed/tomcat-embed-websocket/9.0.17/tomcat-embed-websocket-9.0.17.jar:/Users/ran/mvnrepo/org/hibernate/validator/hibernate-validator/6.0.16.Final/hibernate-validator-6.0.16.Final.jar:/Users/ran/mvnrepo/javax/validation/validation-api/2.0.1.Final/validation-api-2.0.1.Final.jar:/Users/ran/mvnrepo/org/jboss/logging/jboss-logging/3.3.2.Final/jboss-logging-3.3.2.Final.jar:/Users/ran/mvnrepo/com/fasterxml/classmate/1.4.0/classmate-1.4.0.jar:/Users/ran/mvnrepo/org/springframework/spring-web/5.1.6.RELEASE/spring-web-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/spring-beans/5.1.6.RELEASE/spring-beans-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/spring-webmvc/5.1.6.RELEASE/spring-webmvc-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/spring-aop/5.1.6.RELEASE/spring-aop-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/spring-context/5.1.6.RELEASE/spring-context-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/spring-expression/5.1.6.RELEASE/spring-expression-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/org/slf4j/slf4j-api/1.7.26/slf4j-api-1.7.26.jar:/Users/ran/mvnrepo/org/springframework/spring-core/5.1.6.RELEASE/spring-core-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/spring-jcl/5.1.6.RELEASE/spring-jcl-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/com/alibaba/boot/dubbo-spring-boot-starter/0.2.0/dubbo-spring-boot-starter-0.2.0.jar:/Users/ran/mvnrepo/com/alibaba/dubbo/2.6.2/dubbo-2.6.2.jar:/Users/ran/mvnrepo/org/javassist/javassist/3.20.0-GA/javassist-3.20.0-GA.jar:/Users/ran/mvnrepo/org/jboss/netty/netty/3.2.5.Final/netty-3.2.5.Final.jar:/Users/ran/mvnrepo/org/apache/zookeeper/zookeeper/3.4.9/zookeeper-3.4.9.jar:/Users/ran/mvnrepo/jline/jline/0.9.94/jline-0.9.94.jar:/Users/ran/mvnrepo/io/netty/netty/3.10.5.Final/netty-3.10.5.Final.jar:/Users/ran/mvnrepo/org/apache/curator/curator-framework/2.12.0/curator-framework-2.12.0.jar:/Users/ran/mvnrepo/org/apache/curator/curator-client/2.12.0/curator-client-2.12.0.jar:/Users/ran/mvnrepo/com/google/guava/guava/16.0.1/guava-16.0.1.jar:/Users/ran/mvnrepo/com/alibaba/boot/dubbo-spring-boot-autoconfigure/0.2.0/dubbo-spring-boot-autoconfigure-0.2.0.jar:/Users/ran/mvnrepo/org/projectlombok/lombok/1.18.8/lombok-1.18.8.jar:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar:/Users/ran/Library/Caches/IntelliJIdea2018.3/captureAgent/debugger-agent.jar
15:58:41.304 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:java.library.path=/Users/ran/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.
15:58:41.304 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:java.io.tmpdir=/var/folders/r5/j0w999qs2qj0tpmm19yx0_t00000gn/T/
15:58:41.304 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:java.compiler=<NA>
15:58:41.304 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:os.name=Mac OS X
15:58:41.304 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:os.arch=x86_64
15:58:41.304 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:os.version=10.13.6
15:58:41.304 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:user.name=ran
15:58:41.305 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:user.home=/Users/ran
15:58:41.305 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:user.dir=/Users/ran/Documents/github_src/demoDubboProducer
15:58:41.306 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Initiating client connection, connectString=127.0.0.1:2181 sessionTimeout=60000 watcher=org.apache.curator.ConnectionState@4c6b4ed7
15:58:41.316 demoLogback [main-SendThread(127.0.0.1:2181)] INFO  org.apache.zookeeper.ClientCnxn - Opening socket connection to server 127.0.0.1/127.0.0.1:2181. Will not attempt to authenticate using SASL (unknown error)
15:58:41.319 demoLogback [main] INFO  c.a.d.r.zookeeper.ZookeeperRegistry -  [DUBBO] Register: dubbo://172.16.42.209:20880/com.ran.demo.dubboproducer.UserService?anyhost=true&application=demoDubboProducer&dubbo=2.6.2&generic=false&interface=com.ran.demo.dubboproducer.UserService&methods=getUserById,getUserByUser&pid=35933&side=provider&timestamp=1567756720953, dubbo version: 2.6.2, current host: 172.16.42.209
15:58:41.326 demoLogback [main-SendThread(127.0.0.1:2181)] INFO  org.apache.zookeeper.ClientCnxn - Socket connection established to 127.0.0.1/127.0.0.1:2181, initiating session
15:58:41.342 demoLogback [main-SendThread(127.0.0.1:2181)] INFO  org.apache.zookeeper.ClientCnxn - Session establishment complete on server 127.0.0.1/127.0.0.1:2181, sessionid = 0x100002a4e9f0007, negotiated timeout = 40000
15:58:41.347 demoLogback [main-EventThread] INFO  o.a.c.f.state.ConnectionStateManager - State change: CONNECTED
15:58:41.371 demoLogback [main] INFO  c.a.d.r.zookeeper.ZookeeperRegistry -  [DUBBO] Subscribe: provider://172.16.42.209:20880/com.ran.demo.dubboproducer.UserService?anyhost=true&application=demoDubboProducer&category=configurators&check=false&dubbo=2.6.2&generic=false&interface=com.ran.demo.dubboproducer.UserService&methods=getUserById,getUserByUser&pid=35933&side=provider&timestamp=1567756720953, dubbo version: 2.6.2, current host: 172.16.42.209
15:58:41.384 demoLogback [main] INFO  c.a.d.r.zookeeper.ZookeeperRegistry -  [DUBBO] Notify urls for subscribe url provider://172.16.42.209:20880/com.ran.demo.dubboproducer.UserService?anyhost=true&application=demoDubboProducer&category=configurators&check=false&dubbo=2.6.2&generic=false&interface=com.ran.demo.dubboproducer.UserService&methods=getUserById,getUserByUser&pid=35933&side=provider&timestamp=1567756720953, urls: [empty://172.16.42.209:20880/com.ran.demo.dubboproducer.UserService?anyhost=true&application=demoDubboProducer&category=configurators&check=false&dubbo=2.6.2&generic=false&interface=com.ran.demo.dubboproducer.UserService&methods=getUserById,getUserByUser&pid=35933&side=provider&timestamp=1567756720953], dubbo version: 2.6.2, current host: 172.16.42.209
15:58:41.401 demoLogback [main] INFO  o.a.coyote.http11.Http11NioProtocol - Starting ProtocolHandler ["http-nio-8080"]
15:58:41.413 demoLogback [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port(s): 8080 (http) with context path ''
15:58:41.417 demoLogback [main] INFO  c.r.d.d.DemoDubboProducerApplication - Started DemoDubboProducerApplication in 3.046 seconds (JVM running for 3.582)
```

### 消费者启动日志

```
Connected to the target VM, address: '127.0.0.1:65217', transport: 'socket'
15:58:54.191 demoLogback [main] INFO  c.a.d.common.logger.LoggerFactory - using logger: com.alibaba.dubbo.common.logger.slf4j.Slf4jLoggerAdapter
15:58:54.194 demoLogback [main] INFO  c.a.b.d.c.e.WelcomeLogoApplicationListener - 

 :: Dubbo Spring Boot (v0.2.0) : https://github.com/apache/incubator-dubbo-spring-boot-project
 :: Dubbo (v2.6.2) : https://github.com/apache/incubator-dubbo
 :: Google group : dev@dubbo.incubator.apache.org

15:58:54.196 demoLogback [main] INFO  c.a.b.d.c.e.OverrideDubboConfigApplicationListener - Dubbo Config was overridden by externalized configuration {dubbo.application.name=dubboConsumer, dubbo.registry.address=zookeeper://127.0.0.1:2181}

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.1.4.RELEASE)

15:58:54.256 demoLogback [main] INFO  c.r.d.d.DemoDubboConsumerApplication - Starting DemoDubboConsumerApplication on RandeMacBook-Pro.local with PID 35936 (/Users/ran/Documents/github_src/demoDubboConsumer/target/classes started by ran in /Users/ran/Documents/github_src/demoDubboConsumer)
15:58:54.256 demoLogback [main] INFO  c.r.d.d.DemoDubboConsumerApplication - No active profile set, falling back to default profiles: default
15:58:54.608 demoLogback [main] INFO  c.a.d.c.s.c.a.DubboConfigBindingRegistrar - The dubbo config bean definition [name : com.alibaba.dubbo.config.ApplicationConfig#0, class : com.alibaba.dubbo.config.ApplicationConfig] has been registered.
15:58:54.608 demoLogback [main] INFO  c.a.d.c.s.c.a.DubboConfigBindingRegistrar - The BeanPostProcessor bean definition [com.alibaba.dubbo.config.spring.beans.factory.annotation.DubboConfigBindingBeanPostProcessor] for dubbo config bean [name : com.alibaba.dubbo.config.ApplicationConfig#0] has been registered.
15:58:54.608 demoLogback [main] INFO  c.a.d.c.s.c.a.DubboConfigBindingRegistrar - The dubbo config bean definition [name : com.alibaba.dubbo.config.RegistryConfig#0, class : com.alibaba.dubbo.config.RegistryConfig] has been registered.
15:58:54.608 demoLogback [main] INFO  c.a.d.c.s.c.a.DubboConfigBindingRegistrar - The BeanPostProcessor bean definition [com.alibaba.dubbo.config.spring.beans.factory.annotation.DubboConfigBindingBeanPostProcessor] for dubbo config bean [name : com.alibaba.dubbo.config.RegistryConfig#0] has been registered.
15:58:54.739 demoLogback [main] INFO  o.s.c.s.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'com.alibaba.boot.dubbo.autoconfigure.DubboAutoConfiguration' of type [com.alibaba.boot.dubbo.autoconfigure.DubboAutoConfiguration$$EnhancerBySpringCGLIB$$39303cc5] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
15:58:54.785 demoLogback [main] INFO  o.s.c.s.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'relaxedDubboConfigBinder' of type [com.alibaba.boot.dubbo.autoconfigure.RelaxedDubboConfigBinder] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
15:58:54.787 demoLogback [main] INFO  o.s.c.s.PostProcessorRegistrationDelegate$BeanPostProcessorChecker - Bean 'relaxedDubboConfigBinder' of type [com.alibaba.boot.dubbo.autoconfigure.RelaxedDubboConfigBinder] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
15:58:55.043 demoLogback [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat initialized with port(s): 8081 (http)
15:58:55.054 demoLogback [main] INFO  o.a.coyote.http11.Http11NioProtocol - Initializing ProtocolHandler ["http-nio-8081"]
15:58:55.062 demoLogback [main] INFO  o.a.catalina.core.StandardService - Starting service [Tomcat]
15:58:55.062 demoLogback [main] INFO  o.a.catalina.core.StandardEngine - Starting Servlet engine: [Apache Tomcat/9.0.17]
15:58:55.128 demoLogback [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring embedded WebApplicationContext
15:58:55.128 demoLogback [main] INFO  o.s.web.context.ContextLoader - Root WebApplicationContext: initialization completed in 840 ms
15:58:55.333 demoLogback [main] INFO  c.a.d.c.s.b.f.a.DubboConfigBindingBeanPostProcessor - The properties of bean [name : com.alibaba.dubbo.config.ApplicationConfig#0] have been binding by prefix of configuration properties : dubbo.application
15:58:55.339 demoLogback [main] INFO  c.a.d.c.s.b.f.a.DubboConfigBindingBeanPostProcessor - The properties of bean [name : com.alibaba.dubbo.config.RegistryConfig#0] have been binding by prefix of configuration properties : dubbo.registry
15:58:55.402 demoLogback [main] INFO  o.a.c.f.imps.CuratorFrameworkImpl - Starting
15:58:55.406 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:zookeeper.version=3.4.9-1757313, built on 08/23/2016 06:50 GMT
15:58:55.406 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:host.name=172.16.42.209
15:58:55.406 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:java.version=1.8.0_191
15:58:55.406 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:java.vendor=Oracle Corporation
15:58:55.406 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:java.home=/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre
15:58:55.406 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:java.class.path=/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/deploy.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/cldrdata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/dnsns.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/jaccess.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/jfxrt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/localedata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/nashorn.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/sunec.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/sunjce_provider.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/sunpkcs11.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/ext/zipfs.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/javaws.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/jfxswt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/management-agent.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/plugin.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/ant-javafx.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/dt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/javafx-mx.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/jconsole.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/packager.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/sa-jdi.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home/lib/tools.jar:/Users/ran/Documents/github_src/demoDubboConsumer/target/classes:/Users/ran/mvnrepo/org/springframework/boot/spring-boot-starter-web/2.1.4.RELEASE/spring-boot-starter-web-2.1.4.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/boot/spring-boot-starter/2.1.4.RELEASE/spring-boot-starter-2.1.4.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/boot/spring-boot/2.1.4.RELEASE/spring-boot-2.1.4.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/boot/spring-boot-autoconfigure/2.1.4.RELEASE/spring-boot-autoconfigure-2.1.4.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/boot/spring-boot-starter-logging/2.1.4.RELEASE/spring-boot-starter-logging-2.1.4.RELEASE.jar:/Users/ran/mvnrepo/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.jar:/Users/ran/mvnrepo/ch/qos/logback/logback-core/1.2.3/logback-core-1.2.3.jar:/Users/ran/mvnrepo/org/apache/logging/log4j/log4j-to-slf4j/2.11.2/log4j-to-slf4j-2.11.2.jar:/Users/ran/mvnrepo/org/apache/logging/log4j/log4j-api/2.11.2/log4j-api-2.11.2.jar:/Users/ran/mvnrepo/org/slf4j/jul-to-slf4j/1.7.26/jul-to-slf4j-1.7.26.jar:/Users/ran/mvnrepo/javax/annotation/javax.annotation-api/1.3.2/javax.annotation-api-1.3.2.jar:/Users/ran/mvnrepo/org/yaml/snakeyaml/1.23/snakeyaml-1.23.jar:/Users/ran/mvnrepo/org/springframework/boot/spring-boot-starter-json/2.1.4.RELEASE/spring-boot-starter-json-2.1.4.RELEASE.jar:/Users/ran/mvnrepo/com/fasterxml/jackson/core/jackson-databind/2.9.8/jackson-databind-2.9.8.jar:/Users/ran/mvnrepo/com/fasterxml/jackson/core/jackson-annotations/2.9.0/jackson-annotations-2.9.0.jar:/Users/ran/mvnrepo/com/fasterxml/jackson/core/jackson-core/2.9.8/jackson-core-2.9.8.jar:/Users/ran/mvnrepo/com/fasterxml/jackson/datatype/jackson-datatype-jdk8/2.9.8/jackson-datatype-jdk8-2.9.8.jar:/Users/ran/mvnrepo/com/fasterxml/jackson/datatype/jackson-datatype-jsr310/2.9.8/jackson-datatype-jsr310-2.9.8.jar:/Users/ran/mvnrepo/com/fasterxml/jackson/module/jackson-module-parameter-names/2.9.8/jackson-module-parameter-names-2.9.8.jar:/Users/ran/mvnrepo/org/springframework/boot/spring-boot-starter-tomcat/2.1.4.RELEASE/spring-boot-starter-tomcat-2.1.4.RELEASE.jar:/Users/ran/mvnrepo/org/apache/tomcat/embed/tomcat-embed-core/9.0.17/tomcat-embed-core-9.0.17.jar:/Users/ran/mvnrepo/org/apache/tomcat/embed/tomcat-embed-el/9.0.17/tomcat-embed-el-9.0.17.jar:/Users/ran/mvnrepo/org/apache/tomcat/embed/tomcat-embed-websocket/9.0.17/tomcat-embed-websocket-9.0.17.jar:/Users/ran/mvnrepo/org/hibernate/validator/hibernate-validator/6.0.16.Final/hibernate-validator-6.0.16.Final.jar:/Users/ran/mvnrepo/javax/validation/validation-api/2.0.1.Final/validation-api-2.0.1.Final.jar:/Users/ran/mvnrepo/org/jboss/logging/jboss-logging/3.3.2.Final/jboss-logging-3.3.2.Final.jar:/Users/ran/mvnrepo/com/fasterxml/classmate/1.4.0/classmate-1.4.0.jar:/Users/ran/mvnrepo/org/springframework/spring-web/5.1.6.RELEASE/spring-web-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/spring-beans/5.1.6.RELEASE/spring-beans-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/spring-webmvc/5.1.6.RELEASE/spring-webmvc-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/spring-aop/5.1.6.RELEASE/spring-aop-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/spring-context/5.1.6.RELEASE/spring-context-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/spring-expression/5.1.6.RELEASE/spring-expression-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/org/slf4j/slf4j-api/1.7.26/slf4j-api-1.7.26.jar:/Users/ran/mvnrepo/org/springframework/spring-core/5.1.6.RELEASE/spring-core-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/org/springframework/spring-jcl/5.1.6.RELEASE/spring-jcl-5.1.6.RELEASE.jar:/Users/ran/mvnrepo/com/alibaba/boot/dubbo-spring-boot-starter/0.2.0/dubbo-spring-boot-starter-0.2.0.jar:/Users/ran/mvnrepo/com/alibaba/dubbo/2.6.2/dubbo-2.6.2.jar:/Users/ran/mvnrepo/org/javassist/javassist/3.20.0-GA/javassist-3.20.0-GA.jar:/Users/ran/mvnrepo/org/jboss/netty/netty/3.2.5.Final/netty-3.2.5.Final.jar:/Users/ran/mvnrepo/org/apache/zookeeper/zookeeper/3.4.9/zookeeper-3.4.9.jar:/Users/ran/mvnrepo/jline/jline/0.9.94/jline-0.9.94.jar:/Users/ran/mvnrepo/io/netty/netty/3.10.5.Final/netty-3.10.5.Final.jar:/Users/ran/mvnrepo/org/apache/curator/curator-framework/2.12.0/curator-framework-2.12.0.jar:/Users/ran/mvnrepo/org/apache/curator/curator-client/2.12.0/curator-client-2.12.0.jar:/Users/ran/mvnrepo/com/google/guava/guava/16.0.1/guava-16.0.1.jar:/Users/ran/mvnrepo/com/alibaba/boot/dubbo-spring-boot-autoconfigure/0.2.0/dubbo-spring-boot-autoconfigure-0.2.0.jar:/Users/ran/mvnrepo/com/ran/demo/demoDubboProducer-interface/0.0.1-SNAPSHOT/demoDubboProducer-interface-0.0.1-SNAPSHOT.jar:/Users/ran/mvnrepo/org/projectlombok/lombok/1.18.8/lombok-1.18.8.jar:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar:/Users/ran/Library/Caches/IntelliJIdea2018.3/captureAgent/debugger-agent.jar
15:58:55.407 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:java.library.path=/Users/ran/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.
15:58:55.407 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:java.io.tmpdir=/var/folders/r5/j0w999qs2qj0tpmm19yx0_t00000gn/T/
15:58:55.407 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:java.compiler=<NA>
15:58:55.407 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:os.name=Mac OS X
15:58:55.407 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:os.arch=x86_64
15:58:55.407 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:os.version=10.13.6
15:58:55.407 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:user.name=ran
15:58:55.407 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:user.home=/Users/ran
15:58:55.407 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Client environment:user.dir=/Users/ran/Documents/github_src/demoDubboConsumer
15:58:55.407 demoLogback [main] INFO  org.apache.zookeeper.ZooKeeper - Initiating client connection, connectString=127.0.0.1:2181 sessionTimeout=60000 watcher=org.apache.curator.ConnectionState@123d7057
15:58:55.418 demoLogback [main-SendThread(127.0.0.1:2181)] INFO  org.apache.zookeeper.ClientCnxn - Opening socket connection to server 127.0.0.1/127.0.0.1:2181. Will not attempt to authenticate using SASL (unknown error)
15:58:55.430 demoLogback [main] INFO  c.a.d.r.zookeeper.ZookeeperRegistry -  [DUBBO] Register: consumer://172.16.42.209/com.ran.demo.dubboproducer.UserService?application=dubboConsumer&category=consumers&check=false&dubbo=2.6.2&interface=com.ran.demo.dubboproducer.UserService&methods=getUserById,getUserByUser&pid=35936&revision=0.0.1-SNAPSHOT&side=consumer&timestamp=1567756735341, dubbo version: 2.6.2, current host: 172.16.42.209
15:58:55.437 demoLogback [main-SendThread(127.0.0.1:2181)] INFO  org.apache.zookeeper.ClientCnxn - Socket connection established to 127.0.0.1/127.0.0.1:2181, initiating session
15:58:55.445 demoLogback [main-SendThread(127.0.0.1:2181)] INFO  org.apache.zookeeper.ClientCnxn - Session establishment complete on server 127.0.0.1/127.0.0.1:2181, sessionid = 0x100002a4e9f0008, negotiated timeout = 40000
15:58:55.449 demoLogback [main-EventThread] INFO  o.a.c.f.state.ConnectionStateManager - State change: CONNECTED
15:58:55.478 demoLogback [main] INFO  c.a.d.r.zookeeper.ZookeeperRegistry -  [DUBBO] Subscribe: consumer://172.16.42.209/com.ran.demo.dubboproducer.UserService?application=dubboConsumer&category=providers,configurators,routers&dubbo=2.6.2&interface=com.ran.demo.dubboproducer.UserService&methods=getUserById,getUserByUser&pid=35936&revision=0.0.1-SNAPSHOT&side=consumer&timestamp=1567756735341, dubbo version: 2.6.2, current host: 172.16.42.209
15:58:55.499 demoLogback [main] INFO  c.a.d.r.zookeeper.ZookeeperRegistry -  [DUBBO] Notify urls for subscribe url consumer://172.16.42.209/com.ran.demo.dubboproducer.UserService?application=dubboConsumer&category=providers,configurators,routers&dubbo=2.6.2&interface=com.ran.demo.dubboproducer.UserService&methods=getUserById,getUserByUser&pid=35936&revision=0.0.1-SNAPSHOT&side=consumer&timestamp=1567756735341, urls: [dubbo://172.16.42.209:20880/com.ran.demo.dubboproducer.UserService?anyhost=true&application=demoDubboProducer&dubbo=2.6.2&generic=false&interface=com.ran.demo.dubboproducer.UserService&methods=getUserById,getUserByUser&pid=35933&side=provider&timestamp=1567756720953, empty://172.16.42.209/com.ran.demo.dubboproducer.UserService?application=dubboConsumer&category=configurators&dubbo=2.6.2&interface=com.ran.demo.dubboproducer.UserService&methods=getUserById,getUserByUser&pid=35936&revision=0.0.1-SNAPSHOT&side=consumer&timestamp=1567756735341, empty://172.16.42.209/com.ran.demo.dubboproducer.UserService?application=dubboConsumer&category=routers&dubbo=2.6.2&interface=com.ran.demo.dubboproducer.UserService&methods=getUserById,getUserByUser&pid=35936&revision=0.0.1-SNAPSHOT&side=consumer&timestamp=1567756735341], dubbo version: 2.6.2, current host: 172.16.42.209
15:58:55.624 demoLogback [main] INFO  c.a.d.r.transport.AbstractClient -  [DUBBO] Successed connect to server /172.16.42.209:20880 from NettyClient 172.16.42.209 using dubbo version 2.6.2, channel is NettyChannel [channel=[id: 0x798256c5, /172.16.42.209:65230 => /172.16.42.209:20880]], dubbo version: 2.6.2, current host: 172.16.42.209
15:58:55.625 demoLogback [main] INFO  c.a.d.r.transport.AbstractClient -  [DUBBO] Start NettyClient RandeMacBook-Pro.local/172.16.42.209 connect to the server /172.16.42.209:20880, dubbo version: 2.6.2, current host: 172.16.42.209
15:58:55.659 demoLogback [main] INFO  c.a.dubbo.config.AbstractConfig -  [DUBBO] Refer dubbo service com.ran.demo.dubboproducer.UserService from url zookeeper://127.0.0.1:2181/com.alibaba.dubbo.registry.RegistryService?anyhost=true&application=dubboConsumer&check=false&dubbo=2.6.2&generic=false&interface=com.ran.demo.dubboproducer.UserService&methods=getUserById,getUserByUser&pid=35936&register.ip=172.16.42.209&remote.timestamp=1567756720953&revision=0.0.1-SNAPSHOT&side=consumer&timestamp=1567756735341, dubbo version: 2.6.2, current host: 172.16.42.209
15:58:55.664 demoLogback [main] INFO  c.a.d.c.s.b.f.a.ReferenceBeanBuilder - <dubbo:reference object="com.alibaba.dubbo.common.bytecode.proxy0@3e28fee1" singleton="true" interface="com.ran.demo.dubboproducer.UserService" uniqueServiceName="com.ran.demo.dubboproducer.UserService" generic="false" id="com.ran.demo.dubboproducer.UserService" /> has been built.
15:58:55.891 demoLogback [main] INFO  o.s.s.c.ThreadPoolTaskExecutor - Initializing ExecutorService 'applicationTaskExecutor'
15:58:56.088 demoLogback [main] INFO  o.a.coyote.http11.Http11NioProtocol - Starting ProtocolHandler ["http-nio-8081"]
15:58:56.102 demoLogback [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port(s): 8081 (http) with context path ''
15:58:56.106 demoLogback [main] INFO  c.r.d.d.DemoDubboConsumerApplication - Started DemoDubboConsumerApplication in 2.212 seconds (JVM running for 2.726)
16:01:53.794 demoLogback [http-nio-8081-exec-1] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] - Initializing Spring DispatcherServlet 'dispatcherServlet'
16:01:53.794 demoLogback [http-nio-8081-exec-1] INFO  o.s.web.servlet.DispatcherServlet - Initializing Servlet 'dispatcherServlet'
16:01:53.814 demoLogback [http-nio-8081-exec-1] INFO  o.s.web.servlet.DispatcherServlet - Completed initialization in 20 ms
```

### zookeeper 查看

启动 producer 和 consumer 后

```
[zk: localhost:2181(CONNECTED) 0] ls /
[dubbo, zookeeper]
[zk: localhost:2181(CONNECTED) 1] ls /dubbo
[com.ran.demo.dubboproducer.UserService]
[zk: localhost:2181(CONNECTED) 2] ls /dubbo/com.ran.demo.dubboproducer.UserService
[configurators, consumers, providers, routers]
[zk: localhost:2181(CONNECTED) 3] ls /dubbo/com.ran.demo.dubboproducer.UserService/providers
[dubbo%3A%2F%2F172.16.42.209%3A20880%2Fcom.ran.demo.dubboproducer.UserService%3Fanyhost%3Dtrue%26application%3DdemoDubboProducer%26dubbo%3D2.6.2%26generic%3Dfalse%26interface%3Dcom.ran.demo.dubboproducer.UserService%26methods%3DgetUserById%2CgetUserByUser%26pid%3D36555%26side%3Dprovider%26timestamp%3D1567762004927]
[zk: localhost:2181(CONNECTED) 4] ls /dubbo/com.ran.demo.dubboproducer.UserService/consumers
[consumer%3A%2F%2F172.16.42.209%2Fcom.ran.demo.dubboproducer.UserService%3Fapplication%3DdubboConsumer%26category%3Dconsumers%26check%3Dfalse%26dubbo%3D2.6.2%26interface%3Dcom.ran.demo.dubboproducer.UserService%26methods%3DgetUserById%2CgetUserByUser%26pid%3D36558%26revision%3D0.0.1-SNAPSHOT%26side%3Dconsumer%26timestamp%3D1567762015933]
[zk: localhost:2181(CONNECTED) 5] ls /dubbo/com.ran.demo.dubboproducer.UserService/configurators
[]
[zk: localhost:2181(CONNECTED) 6] ls /dubbo/com.ran.demo.dubboproducer.UserService/routers
[]
[zk: localhost:2181(CONNECTED) 7] get /dubbo/com.ran.demo.dubboproducer.UserService/providers/dubbo%3A%2F%2F172.16.42.209%3A20880%2Fcom.ran.demo.dubboproducer.UserService%3Fanyhost%3Dtrue%26application%3DdemoDubboProducer%26dubbo%3D2.6.2%26generic%3Dfalse%26interface%3Dcom.ran.demo.dubboproducer.UserService%26methods%3DgetUserById%2CgetUserByUser%26pid%3D36555%26side%3Dprovider%26timestamp%3D1567762004927
172.16.42.209
[zk: localhost:2181(CONNECTED) 8]
[zk: localhost:2181(CONNECTED) 8] get /dubbo/com.ran.demo.dubboproducer.UserService/consumers/consumer%3A%2F%2F172.16.42.209%2Fcom.ran.demo.dubboproducer.UserService%3Fapplication%3DdubboConsumer%26category%3Dconsumers%26check%3Dfalse%26dubbo%3D2.6.2%26interface%3Dcom.ran.demo.dubboproducer.UserService%26methods%3DgetUserById%2CgetUserByUser%26pid%3D36558%26revision%3D0.0.1-SNAPSHOT%26side%3Dconsumer%26timestamp%3D1567762015933
172.16.42.209
[zk: localhost:2181(CONNECTED) 9]
```
1. 可以看到 zk 节点下新增了 dubbo 节点, dubbo 节点下有了 UserService 节点
2. UserService 节点下有提供方 providers 和消费方 consumers
3. get 命令可以查看到提供方和消费方都各有一个实例

如果停止 producer 和 consumer, 
```
[zk: localhost:2181(CONNECTED) 9] ls /
[dubbo, zookeeper]
[zk: localhost:2181(CONNECTED) 10] ls /dubbo
[com.ran.demo.dubboproducer.UserService]
[zk: localhost:2181(CONNECTED) 11] ls /dubbo/com.ran.demo.dubboproducer.UserService
[configurators, consumers, providers, routers]
[zk: localhost:2181(CONNECTED) 12] ls /dubbo/com.ran.demo.dubboproducer.UserService/providers
[]
[zk: localhost:2181(CONNECTED) 13] ls /dubbo/com.ran.demo.dubboproducer.UserService/consumers
[]
[zk: localhost:2181(CONNECTED) 14] ls /dubbo/com.ran.demo.dubboproducer.UserService/configurators
[]
[zk: localhost:2181(CONNECTED) 15] ls /dubbo/com.ran.demo.dubboproducer.UserService/routers
[]
``` 
不再有 producer 和 consumer 节点