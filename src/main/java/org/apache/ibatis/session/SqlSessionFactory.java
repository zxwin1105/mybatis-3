/**
 * Copyright 2009-2016 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.session;

import java.sql.Connection;

/**
 * 创建SqlSession的工厂接口
 * Creates an {@link SqlSession} out of a connection or a DataSource
 *
 * @author Clinton Begin
 */
public interface SqlSessionFactory {

    /**
     * 创建SqlSession，默认不开起事务
     *
     * @return SqlSession
     */
    SqlSession openSession();

    /**
     * 创建SqlSession，可以选择是否开启事务
     *
     * @param autoCommit 是否开启事务
     * @return SqlSession
     */
    SqlSession openSession(boolean autoCommit);

    /**
     * 创建SqlSession，传入自定义数据库连接信息
     *
     * @param connection 数据库连接信息
     * @return SqlSession
     */
    SqlSession openSession(Connection connection);

    /**
     * 创建SqlSession，指定事务隔离级别
     *
     * @param level 事务隔离级别
     * @return SqlSession
     */
    SqlSession openSession(TransactionIsolationLevel level);

    SqlSession openSession(ExecutorType execType);

    SqlSession openSession(ExecutorType execType, boolean autoCommit);

    SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level);

    SqlSession openSession(ExecutorType execType, Connection connection);

    Configuration getConfiguration();

}
