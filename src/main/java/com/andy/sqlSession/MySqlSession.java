package com.andy.sqlSession;

import java.lang.reflect.Proxy;

/**
 * Created by admin on 2019-01-18.
 */
public class MySqlSession {
    private Excutor excutor = new MyExcutor();
    private MyConfiguration myConfiguration = new MyConfiguration();

    /**
     * 方法
     * @param sql
     * @param parameter
     * @param <T>
     * @return
     */
    public <T> T selectOne(String sql,String parameter){
        return excutor.query(sql,parameter);
    }
    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> clas){
        //动态代理调用该方法用于为指定类装载器、一组接口及调用处理器生成动态代理类实例
        return (T) Proxy.newProxyInstance(clas.getClassLoader(),new Class[]{clas},
                new MyMapperProxy(this,myConfiguration));
    }
}
