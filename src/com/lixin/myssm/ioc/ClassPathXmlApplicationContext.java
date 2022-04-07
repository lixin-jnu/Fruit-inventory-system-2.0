package com.lixin.myssm.ioc;

import com.lixin.myssm.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ClassPathXmlApplicationContext implements BeanFactory {

    /*
     * 存储从applicationContext.xml解析到的servletpath和controller类"实际对象"的对应关系;
     * 这样当收到Servlet请求时,就可以找到对应的controller来处理;
     * 迭代优化后功能进一步增加:承担起"依赖注入"对应关系和依赖关系获取+控制反转的作用;
     */
    private Map<String, Object> beanMap = new HashMap<>();

    public ClassPathXmlApplicationContext() {
        this("applicationContext.xml");
    }

    public ClassPathXmlApplicationContext(String path) {
        if (StringUtil.isEmpty(path)) {
            throw new RuntimeException("IOC容器的配置文件路径错误->" + path);
        }
        try {
            //资源绑定器
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
            //1.创建DocumentBuilderFactory
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            //2.创建DocumentBuilder对象
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            //3.创建Document对象
            Document document = documentBuilder.parse(inputStream);
            //4.获取所有的bean节点
            NodeList beans = document.getElementsByTagName("bean");
            //5.对单个bean节点进行处理
            for (int i = 0; i < beans.getLength(); i++) {
                Node beanNode = beans.item(i);
                //5.1存储bean之中的对应关系
                if (beanNode.getNodeType() == Node.ELEMENT_NODE) {//Node.ELEMENT_NODE:指一个元素节点,例如<p>或<div>
                    Element beanElement = (Element) beanNode;
                    beanMap.put(beanElement.getAttribute("id"),
                            //这里是存储了一个controller实例对象
                            Class.forName(beanElement.getAttribute("class")).newInstance());
                }
            }
            //5.2反射注入bean之间的依赖关系
            for (int i = 0; i < beans.getLength(); i++) {
                Node beanNode = beans.item(i);//单个bean节点
                if (beanNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element beanElement = (Element) beanNode;
                    String beanID = beanElement.getAttribute("id");//记录ID,后续需要在该ID对应的实例对象中对某个Field赋值
                    NodeList beanChildNodes = beanElement.getChildNodes();//获取单个bean节点所有的孩子节点(property节点)
                    for (int j = 0; j < beanChildNodes.getLength(); j++) {
                        Node beanChildNode = beanChildNodes.item(j);
                        if (beanChildNode.getNodeType() == Node.ELEMENT_NODE && "property".equals(beanChildNode.getNodeName())) {
                            Element propertyElement = (Element) beanChildNode;//单个property节点
                            String propertyName = propertyElement.getAttribute("name");
                            String propertyRef = propertyElement.getAttribute("ref");
                            //beanID对应的实例对象需要依赖的实例对象->例如FruitController中的FruitService,FruitService中的FruitDAO
                            Object refObj = beanMap.get(propertyRef);
                            //beanID对应的实例对象,含有propertyName属性
                            Object beanObj = beanMap.get(beanID);
                            Field propertyField = beanObj.getClass().getDeclaredField(propertyName);
                            propertyField.setAccessible(true);
                            propertyField.set(beanObj, refObj);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(String id) {
        return beanMap.get(id);
    }

}