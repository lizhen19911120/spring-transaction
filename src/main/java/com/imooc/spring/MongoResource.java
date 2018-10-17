package com.imooc.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by lizhen on 2018/9/26.
 */

@RestController
@RequestMapping("/mongo")
public class MongoResource {

    @Autowired
    private MongoTemplate mongoTemplate;

    @GetMapping("/queryTestDomain")
    public TestDomain queryTestDomain(@RequestParam(value = "id",required = false) String id){
        TestDomain testDomain = mongoTemplate.findById(id,TestDomain.class,"cm_testDomain");
        return testDomain;
    }

    /**
     * 接收请求体中的json字符串，通过HttpMessageConverter转为对应实体类？——非也。
     * HttpMessageConverter只是起到其中的格式化时间作用，本质上是RequestResponseBodyMethodProcessor负责接收转化的
     * @RequestBody
     * @param testDomain
     * @return
     */
    @PostMapping("/saveTestDomain")
    public TestDomain saveTestDomain(@RequestBody TestDomain testDomain){
        mongoTemplate.save(testDomain,"cm_testDomain");
        System.out.println(testDomain.getCmamCreateTime());
        System.out.println(testDomain.getCmamEra());
        return testDomain;
    }

    /**
     * 使用springmvc的接收参数机制配合@DateTimeFormat注解，将前端的时间参数注入到TestDomain里
     * @param testDomain
     * @return
     */
    @PostMapping("/saveTestDomain1")
    public TestDomain saveTestDomain1(TestDomain testDomain){
        mongoTemplate.save(testDomain,"cm_testDomain");
        System.out.println(testDomain.getCmamCreateTime());
        System.out.println(testDomain.getCmamEra());
        return testDomain;
    }

    /**
     * 接收前端参数为LocalDateTime类对象
     * @RequestParam&@DateTimeFormat
     * @param cmamCreateTime
     * @return
     */
    @GetMapping("/convertDateTime")
    public LocalDateTime convertDateTime(@RequestParam(value = "cmamCreateTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime cmamCreateTime){
        System.out.println(cmamCreateTime);
        return cmamCreateTime;
    }

    @RequestMapping("/saveEmployee")
    public Employee saveEmployee(@RequestBody Employee e) {
        Dept dept = new Dept();
        dept.setId(1);
        dept.setName("A");
        e.setDept(dept);
        mongoTemplate.save(e,"cm_employee");
        return e;
    }

    @RequestMapping("/saveDept")
    public Dept saveDept(@RequestBody Dept dept) {
        mongoTemplate.save(dept,"cm_dept");
        return dept;
    }


    /**
     * 交给RequestResponseBodyMethodProcessor处理，会根据contentType然后选择合适的消息转换器进行读取
     * 消息转换器只有默认的那些跟部分json以及xml转换器，且传递的参数name=1&age=3，传递的头部中没有content-type，默认使用了application/octet-stream，因此触发了HttpMediaTypeNotSupportedException异常
     * 解放方案： 我们将传递数据改成json，同时http请求的Content-Type改成application/json即可
     * 仅支持body参数？！
     * @param e
     * @return
     */
    @RequestMapping("/testRb")
    public Employee testRb(@RequestBody Employee e) {
        return e;
    }

    /**
     * 交给ServletModelAttributeMethodProcessor(true)处理,绑定到对应的bean上
     * 在绑定参数前，它会调用bean的默认构造函数创建，然后再绑定参数，而且只能读取url中的参数...
     * 仅支持url参数？！
     * @param e
     * @return
     */
    @RequestMapping("/testCustomObj")
    public Employee testCustomObj(Employee e) {
        return e;
    }

    @RequestMapping(value = "/testCustomObj1")
    public Employee testCustomObj1(@ModelAttribute("employee") Employee e) {
        return e;
    }

    /**
     * testCustomObj2执行前，会将一个Date放到Model中去，且key="aaaa",
     * testCustomObj2在绑定参数前，它不调用Date的默认构造函数创建，而是从Model中获取，然后再绑定参数
     * @param e
     * @return
     */
    @ModelAttribute("aaaa")
    public Date init( Date e) {
        return new Date();
    }

    /**
     * @ModelAttribute
     * 放在方法上的话，会在每个@RequestMapping注解的方法执行前执行，如果有返回值会默认放到Model中；
     * 方在参数上的话，会联系前端的请求参数/返回的参数属性key与方法的参数/返回值
     *
     * 如果有@ModelAttribute注解，会使用ServletModelAttributeMethodProcessor(false)来处理，会进入
     *
     * ModelAttributeMethodProcessor的
     * {...
     * Object attribute = constructAttribute(ctor, attributeName, binderFactory, webRequest);
     * ...}
     * 这个代码段，会调用参数类型的默认构造函数
     *
     * 也就是说前端参数为aaaa=2018-11-10,会像@RequestMapping("/testCustomObj")一样接收返回；
     * 如果前端参数不是aaaa=××××的形式的话，结果返回 new Date() {this(System.currentTimeMillis());}
     * 因为它还是被当做一个bean来处理的，所以找不到的属性就没有被赋值了。
     * 本来很奇怪的是time=××××或者date=××××会报错，验证因为诸如time/date是在Date类中有setter方法，会被databinder验证，
     * 这里报错是因为time/date这些只能接收int类型....改正后不报错但是一样被跳过返回new Date() {this(System.currentTimeMillis());}
     * @param e
     * @return
     */
    @RequestMapping(value = "/testCustomObj2")
    public Date testCustomObj2(@ModelAttribute("aaaa") Date e) {
        return e;
    }

    /**
     * 因为这些参数是预定义的简单参数类型，所以RequestMappingHandlerAdapter使用RequestParamMethodArgumentResolver来处理
     * 同时对于这些简单类型，不需要@RequestParam注解，默认使用方法参数名作为请求参数名
     * @param date
     * @return
     */
    @RequestMapping("/testDate")
    public Date testDate(Date date) {
        return date;
    }

    /**
     * 简单参数类型可以混合接收
     * @param date
     * @param str
     * @return
     */
    @RequestMapping("/testComposite")
    public String testComposite(Date date,String str) {
        return date+str;
    }

    /**
     * 如果是多复杂参数混合接收，则不能正常绑定
     * @param e
     * @param dept
     * @return
     */
    @RequestMapping("/testComposite1")
    public String testComposite1(Employee e,Dept dept) {
        return e.toString()+dept.toString();
    }

    /**
     * 自定义注解@FormObj、撰写FormObjArgumentResolver这个自定义HandlerMethodArgumentResolver
     * 并将它注册进RequestMappingHandlerAdapter的argumentResolvers，实现多复杂参数混合接收
     * 顺序不要求、默认请求参数为方法参数名、参数放入Model中
     * @param dept
     * @param emp
     * @return
     */
    @RequestMapping("/test1")
    public String test1(@FormObj Dept dept, @FormObj Employee emp) {
        return dept.toString()+emp.toString();
    }

    /**
     * 参数名定为d，e
     * @param dept
     * @param emp
     * @return
     */
    @RequestMapping("/test2")
    public String test2(@FormObj("d") Dept dept, @FormObj("e") Employee emp) {
        return dept.toString()+emp.toString();
    }

    /**
     * 将key为"d"的Dept不放入Model中->前端页面无法取到了
     * @param dept
     * @param emp
     * @return
     */
    @RequestMapping("/test3")
    public String test3(@FormObj(value = "d", show = false) Dept dept, @FormObj("e") Employee emp) {
        return dept.toString()+emp.toString();
    }


    /**
     * 给RequestMappingHandlerAdapter使用的RequestParamMethodArgumentResolver这个解析简单类型参数的
     * 处理器绑定一个Databinder处理提交的参数，转为对应的简单类型参数
     * 对于Date类型参数，这里效果和@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")这个注解一样，
     * 不过实测下来这种更灵活，即使参数是“2018-10-17aaa”也不影响
     * @param binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

}
