package com.andy.test;

import com.andy.bean.User;
import com.andy.mapper.UserMapper;
import com.andy.sqlSession.MySqlSession;

public class SqlTest {
    public static void main(String[] args) {
        MySqlSession sqlSession = new MySqlSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User userById = mapper.getUserById("1");
        System.out.println(userById.getEmail());
        System.out.println(userById.getHouseAddress());
        System.out.println(userById.getId());
        System.out.println(userById.getPassword());
        System.out.println(userById.getPhone());
    }
}
