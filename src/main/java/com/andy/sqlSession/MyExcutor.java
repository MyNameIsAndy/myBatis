package com.andy.sqlSession;

import com.andy.bean.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by admin on 2019-01-18.
 */
public class MyExcutor implements Excutor {
    private static MyConfiguration myConfiguration = new MyConfiguration();

    /**
     * 获取链接
     * @return
     */
    private Connection getConnection(){
        Connection connection;
        try{
            connection = myConfiguration.build("config.xml");
        }catch (Exception e){
            throw new RuntimeException("链接数据库失败");
        }
        return connection;
    }

    /**
     * 查询
     * @param statement
     * @param parameter
     * @param <T>
     * @return
     */
    @Override
    public <T> T query(String statement, Object parameter)  {
        Connection connection = getConnection();
        ResultSet set = null;
        PreparedStatement preparedStatement = null;
        try{
            preparedStatement = connection.prepareStatement(statement);
            //设置参数
            preparedStatement.setString(1,parameter.toString());
            //执行查询
            set = preparedStatement.executeQuery();
            User user = new User();
            while (set.next()){
                user.setId(set.getString(1));
                user.setName(set.getString(2));
                user.setPassword(set.getString(3));
                user.setEmail(set.getString(4));
                user.setPhone(set.getString(5));
                user.setHouseAddress(set.getString(6));
            }
            return (T)user;
        }catch (SQLException  e){
            e.printStackTrace();
        }finally {
            try{
                if(set!=null){
                    set.close();
                }if(preparedStatement!=null){
                    preparedStatement.close();
                }if(connection!=null){
                    connection.close();
                }
            }catch(Exception e2){
                e2.printStackTrace();
            }
        }
        return null;
    }
}
