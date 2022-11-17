/**
 * Copyright 2009-2015 the original author or authors.
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

import org.apache.ibatis.executor.ReuseExecutor;

/**
 * Mybatis中Sql执行的模式枚举
 * 有3中执行模式，对应3中执行器：
 * SIMPLE: SimpleExecutor
 * RESUE: ReuseExecutor
 * BATCH: BatchExecutor
 * {@link org.apache.ibatis.executor.SimpleExecutor}
 * {@link org.apache.ibatis.executor.ReuseExecutor}
 * {@link org.apache.ibatis.executor.BatchExecutor}
 * @author Clinton Begin
 */
public enum ExecutorType {
    /**
     * 该模式下它每次执行都会创建一个statement，用完后关闭
     */
    SIMPLE,

    /**
     * 将statement存入map中，操作map中的statement而不会重复创建statement
     */
    REUSE,

    /**
     * #doUpdate()预处理存储过程或批处理操作，#doQuery()提交并执行过程。
     */
    BATCH
}
