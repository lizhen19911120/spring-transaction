package com.imooc.spring;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.mongodb.MongoClientURI;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.rmi.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by lizhen on 2018/9/21.
 */
@SpringBootApplication(scanBasePackages = {"com.imooc.spring"})
//@EnableWebMvc
//@Configuration
@PropertySource(value = "classpath:mongo.properties",ignoreResourceNotFound = true)
/**
 * 实现WebMvcConfigurer来定义Spring MVC的运行环境，比如这里实现addArgumentResolvers()方法增加自定义的参数接收处理器
 */
public class MongoConfig implements WebMvcConfigurer {

 @Value("${mongo.database}")
 private String databaseName;

 @Value("${mongo.server}")
 private String uri;

 @Value("${mongo.username}")
 private String userName;

 @Value("${mongo.password}")
 private String password;

 @Value("${mongo.options}")
 private String options;

 @Autowired
 private MongoTemplate mongoTemplate;


 /**
  * 定义默认内置tomcat的http连接属性
  * @version
  * @author liuyi  2016年7月20日 下午7:59:41
  */
 class GwsTomcatConnectionCustomizer implements TomcatConnectorCustomizer {

  public GwsTomcatConnectionCustomizer() {
  }

  @Override
  public void customize(Connector connector) {
   connector.setPort(8082);
  }
 }

 /**
  * 定义springboot启动的内置tomcat容器
  * @return
  */
 @Bean
 public ServletWebServerFactory servletContainer() {
  TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
  tomcat.addConnectorCustomizers(new GwsTomcatConnectionCustomizer());
  return tomcat;
 }

 /**
  * MongoDb连接工厂
  * @return
  * @throws UnknownHostException
  */
 @Bean
 public MongoDbFactory mongoDbFactory() throws UnknownHostException {
  String uriStr="mongodb://"+userName+":"+password+"@"+uri+"/"+databaseName+"?"+options;
  System.out.println(uriStr);
  MongoClientURI mongoClientURI=new MongoClientURI(uriStr);
  MongoDbFactory mongoDbFactory=new SimpleMongoDbFactory(mongoClientURI);
  return mongoDbFactory;
 }


 /**
  *自定义一个针对LocalDateTime(yyyy-MM-dd HH:mm:ss)、LocalDate(yyyy-MM-dd)、LocalTime(HH:mm:ss)这些时间格式的json序列化/反序列化的转化器ObjectMapper
  * 解决方法入参（前端json字符串转为实体类对象）/出参时（后端查询出的实体类对象转为json字符串）的转换格式问题
  * @return
  */
 @Bean(name = "mapperObject")
 public ObjectMapper getObjectMapper() {
  ObjectMapper om = new ObjectMapper();
//  om.findAndRegisterModules();
  JavaTimeModule javaTimeModule = new JavaTimeModule();
  javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
  javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
  javaTimeModule.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  javaTimeModule.addDeserializer(LocalDate.class,new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
  javaTimeModule.addDeserializer(LocalTime.class,new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));


  /**
   * 对LocalDateTime增加yyyy-MM-dd的反序列化格式，不可以对LocalDateTime重复设置...可以探究如何动态根据json字符串格式来决定使用哪一个序列化工具？
   */
//  javaTimeModule.addDeserializer(LocalDateTime.class,new LocalDateTimeDeserializer(new DateTimeFormatterBuilder().append(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//          .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
//          .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
//          .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
//          .toFormatter()));

  om.registerModule(javaTimeModule);

  /**
   * Include.ALWAYS  是序列化对像所有属性
     Include.NON_NULL 只有不为null的字段才被序列化
     Include.NON_EMPTY 如果为null或者 空字符串和空集合都不会被序列化
   */
  om.setSerializationInclusion(JsonInclude.Include.ALWAYS);
  //反序列化的时候如果多了其他属性,不抛出异常
  om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  //如果是空对象的时候,不抛异常
  om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
  //取消时间的转化格式,默认是时间戳,可以取消,同时需要设置要表现的时间格式
  om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  return om;
 }

 /**
  * 不需要手动装配这个ObjectMapper到 MappingJackson2HttpMessageConverter中，会自动装配
  * @see HttpMessageConvertersAutoConfiguration
  * @see JacksonHttpMessageConvertersConfiguration
  * @param objectMapper
  * @return
  */
// @Bean
 public HttpMessageConverter mappingJackson2HttpMessageConverter(@Qualifier("mapperObject") ObjectMapper objectMapper) {
  MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
  converter.setObjectMapper(objectMapper);
//  List<MediaType> supportedMediaTypes=new ArrayList<>();
//  supportedMediaTypes.add(MediaType.valueOf("application/x-www-form-urlencoded;charset=UTF-8"));
//  supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
//  converter.setSupportedMediaTypes(supportedMediaTypes);
  return converter;
 }

// @Bean
 public String test(){
  TestDomain domain = new TestDomain();
  domain.setCmamCreateTime(LocalDateTime.now());
  domain.setCmamEra(LocalDateTime.now().toLocalDate());
  mongoTemplate.save(domain,"cm_testDomain");
  return "success";
 }

 @Bean
 public String test1(@Qualifier("mapperObject") ObjectMapper objectMapper){
  TestDomain testDomain = mongoTemplate.findById("5bad90cecd1d551a00f221b3",TestDomain.class,"cm_testDomain");
  System.out.println("testDomain.getCmamCreateTime: "+testDomain.getCmamCreateTime());
  ObjectMapper om = new ObjectMapper()
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .enable(MapperFeature.USE_ANNOTATIONS);
  try {
   System.out.println(om.writeValueAsString(testDomain));
   System.out.println(objectMapper.writeValueAsString(testDomain));
   TestDomain testDomainRe = objectMapper.readValue("{\"id\":\"5baa080ecd1d551ee87baa80\",\"cmamCreateTime\":\"2018-09-25 18:03:58\",\"cmamEra\":\"2018-09-25\",\"cmamYear\":\"2018-10-01\",\"time\":\"18:10:01\"}",TestDomain.class);
   System.out.println(testDomainRe.getCmamCreateTime());
   System.out.println(testDomainRe.getCmamEra());
   System.out.println(testDomainRe.getTime());
  } catch (JsonProcessingException e) {
   e.printStackTrace();
  } catch (IOException e) {
   e.printStackTrace();
  }

  return "success";
 }

 @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
  // 注册FormObjArgumentResolver的参数分解器
  argumentResolvers.add(new FormObjArgumentResolver());
 }

 protected static Logger logger= LoggerFactory.getLogger(MongoConfig.class);

 public static void main(String[] args) {
  ApplicationContext context = new SpringApplicationBuilder(MongoConfig.class)
          .run(args);
  logger.info("SpringBoot Start Success");
//  System.out.println(context.getBean(PropertySourcesPlaceholderConfigurer.class));

//  TestDomain testDomain = ((MongoTemplate)context.getBean("mongoTemplate")).findById("5baa080ecd1d551ee87baa80",TestDomain.class,"cm_testDomain");
//  System.out.println("testDomain.getCmamCreateTime: "+testDomain.getCmamCreateTime());
//  ObjectMapper om = new ObjectMapper()
//          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
//          .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//          .enable(MapperFeature.USE_ANNOTATIONS);
//  try {
//   System.out.println(om.writeValueAsString(testDomain));
//   System.out.println(((ObjectMapper)context.getBean("mapperObject")).writeValueAsString(testDomain));
//   System.out.println(((ObjectMapper)context.getBean("mapperObject")).readValue("2018-09-25",LocalDate.class));
//  } catch (JsonProcessingException e) {
//   e.printStackTrace();
//  } catch (IOException e) {
//   e.printStackTrace();
//  }
 }

}
