/**
 *    Copyright 2009-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.plugin;

import java.util.Properties;

/**
 * Mybatis的拦截器接口，自定义拦截器需要实现该接口
 *
 * @author Clinton Begin
 */
public interface Interceptor {

  /**
   * 拦截器真正的业务逻辑实现方法
   *
   * @param invocation invocation
   * @return Object
   * @throws Throwable
   */
  Object intercept(Invocation invocation) throws Throwable;

  /**
   * 拦截器链会调用拦截器的plugin方法
   * 该方法一般为固定写法return Plugin.wrap(target, this);
   * 通过Plugin.wrap()代理去执行#intercept() 方法
   * @param target 拦截的目标
   * @return
   */
  Object plugin(Object target);

  /**
   * 可以设置一些参数
   * @param properties 参数
   */
  void setProperties(Properties properties);

}
