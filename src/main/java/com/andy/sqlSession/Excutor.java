package com.andy.sqlSession;

import java.sql.SQLException;

/**
 * 执行器
 * Created by admin on 2019-01-18.
 */
public interface Excutor {
    public <T> T query(String statement,Object parameter);
}
