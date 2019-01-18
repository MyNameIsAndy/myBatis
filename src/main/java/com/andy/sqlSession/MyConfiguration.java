package com.andy.sqlSession;

import com.andy.bean.Function;
import com.andy.bean.MapperBean;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 读取与解析配置信息，并返回处理后的Environment
 */
public class MyConfiguration {
    //返回ApplicationClassLoader,其只加载classpath下的class文件。
     private static ClassLoader classLoader = ClassLoader.getSystemClassLoader();
     //读取配置文件
     public Connection build(String resource){
        try{
            InputStream resourceAsStream = classLoader.getResourceAsStream(resource);
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            return evalDataSource(rootElement);
        }catch (Exception e){
            throw new RuntimeException("读取配置文件异常失败");
        }
     }

    /**
     * 读取数据源
     * @param element
     * @return
     */
     public Connection evalDataSource(Element element) throws ClassNotFoundException{
        //判断标签
         if(!"database".equals(element.getName())){
             throw new RuntimeException("找不到<database>标签");
         }
         //获取属性
         String driverClassName = null;
         String url = null;
         String username =null;
         String password =null;
         for(Object object: element.elements("property")){
             Element node = (Element) object;
             //获取标签值
             String value = getValue(node);
             //标签名字
             String name = node.attributeValue("name");
             if("".equals(value) || "".equals(name)){
                 throw new RuntimeException("标签值或属性不匹配");
             }
             //根据名称找到对应值
             switch (name){
                 case "url" : url = value; break;
                 case "username" : username = value; break;
                 case "password" : password = value; break;
                 case "driverClassName" : driverClassName = value; break;
                 default:throw new RuntimeException("没有找到对应标签名称");
             }
         }
         //根据指定名称加载类
         Class.forName(driverClassName);
         Connection connection = null;
         try{
              connection = DriverManager.getConnection(url, username, password);
         }catch (SQLException e){
             throw new RuntimeException("加载数据库失败");
         }
         return connection;
     }
     //获取值
     private String getValue(Element element){
        return element.hasContent()?element.getText():element.attributeValue("value");
     }

    /**
     * 读取mapper文件
     * @param path
     * @return
     */
     public MapperBean readMapper(String path){
         MapperBean mapperBean = new MapperBean();
         try{
             InputStream resourceAsStream = classLoader.getResourceAsStream(path);
             SAXReader saxReader = new SAXReader();
             Document read = saxReader.read(resourceAsStream);
             //元素节点
             Element rootElement = read.getRootElement();
             //接口空间
             String nameSpace = rootElement.attributeValue("nameSpace").trim();
             mapperBean.setInterfaceName(nameSpace);
             //存放接口集合
             List<Function> functionList = new ArrayList<>();
             for(Iterator rootIter = rootElement.elementIterator();rootIter.hasNext();){
                 //存放结果
                 Function function = new Function();
                 Element next = (Element)rootIter.next();
                 String sqlType = next.getName();
                 String funcName = next.attributeValue("id").trim();//方法名称
                 String sql = next.getText().trim();//sql
                 String resultType = next.attributeValue("resultType").trim();
                 function.setSqltype(sqlType);//sql类型
                 function.setFuncName(funcName);
                 Object newInstance=null;
                 try {
                     newInstance = Class.forName(resultType).newInstance();
                 } catch (InstantiationException e1) {
                     e1.printStackTrace();
                 } catch (IllegalAccessException e1) {
                     e1.printStackTrace();
                 } catch (ClassNotFoundException e1) {
                     e1.printStackTrace();
                 }
                 function.setResultType(newInstance);//结果类型
                 function.setSql(sql);
                 functionList.add(function);
                 }
             mapperBean.setList(functionList);
         }catch (DocumentException e){
            e.printStackTrace();
         }
            return mapperBean;
     }
}
