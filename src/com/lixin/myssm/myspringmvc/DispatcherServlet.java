package com.lixin.myssm.myspringmvc;

import com.lixin.myssm.ioc.BeanFactory;
import com.lixin.myssm.util.StringUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 中央控制器类:
 * 1.响应所有的Servlet请求;
 * 2.根据url定位到能够处理这个请求的controller类(当然要有一个Map容器存储这种对应关系)
 * 3.调用controller组件中的方法;
 */

@WebServlet("*.do")//响应所有的Servlet请求
public class DispatcherServlet extends ViewBaseServlet {

    private BeanFactory beanFactory;//bean工厂

    public DispatcherServlet() {
    }

    @Override
    public void init() throws ServletException {
        //执行一下父类(ViewBaseServlet)中的init()方法,防止ServletContext对象为空
        super.init();
        /*
         * 一.填充beanMap:使用DOM技术解析XML文件
         */
        //之前是在此处主动创建IOC容器(+依赖注入),现在优化为从application保存作用域去获取
        //this.beanFactory = new ClassPathXmlApplicationContext();//优化为在此类处理
        ServletContext application = getServletContext();
        Object beanFactoryObj = application.getAttribute("beanFactory");
        if (beanFactoryObj != null) {
            this.beanFactory = (BeanFactory) beanFactoryObj;
        } else {
            throw new RuntimeException("IOC容器获取失败~");
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {
        //设置编码亘古不变->优化为由CharacterEncodingFilter过滤器去设置编码
        //request.setCharacterEncoding("UTF-8");
        /*
         * 二.根据url定位到能够处理这个请求的controller类
         */
        //处理流程:/fruit.do->fruit->FruitController
        String servletPath = request.getServletPath();//"/fruit.do"
        //这里需要注意的是:/fruit.do?pageNo=2&fid=3,所以不能截取servletPath后三个字符之前的
        servletPath = servletPath.substring(1, servletPath.lastIndexOf(".do"));//"fruit"
        Object controllerBeanObj = beanFactory.getBean(servletPath);//成功获取到controller对象
        /*
         * 三.调用controller组件中的方法
         */
        String operate = request.getParameter("operate");
        if (StringUtil.isEmpty(operate)) {//1|6|7时operate都会为空->operate="index"
            operate = "index";
        }
        /*
         * 全部的Servlet请求:共7种
         * 1.回到index(包括第一次请求);
         * 2.edit;
         * 3.add;
         * 4.del;
         * 5.update;
         * 6.模糊查询;
         * 7.分页(包括首尾页/上下页);
         */
        try {
            //==规定operate的值对应方法名==
            Method[] methods = controllerBeanObj.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (operate.equals(method.getName())) {//成功获取到方法
                    //3.1统一获取请求参数
                    //获取当前方法的参数,返回参数数组
                    Parameter[] parameters = method.getParameters();
                    //创建Object数组存储参数的值
                    Object[] paramValues = new Object[parameters.length];
                    for (int i = 0; i < parameters.length; i++) {
                        String parameterName = parameters[i].getName();//参数名称(你自己写的)
                        //==规定参数名称符合规范且设置IDEA->"-parameters"==
                        //参数名是session|request|response就不可以从请求中获取
                        switch (parameterName) {
                            case "request":
                                paramValues[i] = request;
                                break;
                            case "response":
                                paramValues[i] = response;
                                break;
                            case "session":
                                paramValues[i] = request.getSession();
                                break;
                            default:
                                //其它情况从请求中获取参数值->类型有Integer和String
                                String paramValue = request.getParameter(parameterName);
                                if (StringUtil.isNotEmpty(paramValue)
                                        && parameters[i].getType().getName().equals("java.lang.Integer")) {
                                    paramValues[i] = Integer.parseInt(paramValue);
                                } else {
                                    paramValues[i] = paramValue;
                                }
                        }
                    }
                    //3.2controller组件中的方法调用
                    method.setAccessible(true);
                    Object returnObj = method.invoke(controllerBeanObj, paramValues);
                    //3.3视图处理
                    String methodReturnStr = (String) returnObj;
                    if (methodReturnStr.startsWith("redirect:")) {
                        response.sendRedirect(methodReturnStr.substring("redirect:".length()));
                    } else {
                        super.processTemplate(methodReturnStr, request, response);
                    }
                }
            }
        } catch (InvocationTargetException | IllegalAccessException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("DispatcherServlet-service方法错误~");
        }
    }

}