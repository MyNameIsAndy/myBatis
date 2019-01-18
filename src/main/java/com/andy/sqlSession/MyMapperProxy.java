package com.andy.sqlSession;

import com.andy.bean.Function;
import com.andy.bean.MapperBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * myBatis的代理类
 * Created by admin on 2019-01-18.
 */
public class MyMapperProxy implements InvocationHandler{
    private MyConfiguration myConfiguration;
    private MySqlSession mySqlSession;
    public MyMapperProxy(MySqlSession mySqlSession, MyConfiguration myConfiguration){
        this.myConfiguration = myConfiguration;
        this.mySqlSession = mySqlSession;
    }
    /**
     * proxy:代理类代理的真实代理对象com.sun.proxy.$Proxy0
     * method:我们所要调用某个对象真实的方法的Method对象
     * args:指代代理对象方法传递的参数
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MapperBean mapperBean = myConfiguration.readMapper("UserMapper.xml");
        //接口名称是否相对应
        if(!mapperBean.getInterfaceName().equals(method.getDeclaringClass().getName())){
            return null;
        }
        List<Function> functionList = mapperBean.getList();
        if(null != functionList || 0!=functionList.size()){
            for(Function fun : functionList){
                if(method.getName().equals(fun.getFuncName())){
                    return mySqlSession.selectOne(fun.getSql(),String.valueOf(args[0]));
                }
            }
        }
        return null;
    }
}
