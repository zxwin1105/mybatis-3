## 缓存介绍

mybatis中有两级缓存：一级缓存和二级缓存。一级缓存，又称会话级缓存，生命周期仅存在于当前会话，不可以直接关闭；二级缓存，应用级缓存，缓存对象存在于
整个应用的生命周期，可以跨线程使用。

### 1. 一级缓存命中条件

一级缓存命中必须满足特定命中参数，并且没有触发缓存清空。命中参数如下：

1. 多条查询SQL的参数相同
2. 多条查询SQL在同一会话
3. 相同的MapperStatementID
4. RowBounds行范围相同

触发一级缓存清空操作：

1. 手动调用clearCache
2. 执行提交回滚
3. 执行update操作
4. 配置flushCache=true
5. 缓存作用域为Statement


### 2. 一级缓存实现

一级缓存是由BaseExecutor内部控制的,属性如下：
```java
  /** 一级缓存，缓存当前SqlSession相同查询的结果数据 */
  protected PerpetualCache localCache;

  /** 一级缓存，缓存当前SqlSession存储过程的返回结果 */
  protected PerpetualCache localOutputParameterCache;
```
在每次查询之前，都会生成一个CacheKey，通过CacheKey去localCache中查询是否存在相同的查询结果，如果存在直接返回缓存中内容。
如果不存在，则去数据库中查询，并将结果缓存到localCache中。

### 3. 一级缓存Key

实际一级缓存通过PerpetualCache来进行存储，PerpetualCache内部是通过Map集合来进行缓存数据的，核心属性如下：

```java
  /** 缓存标识 */
  private final String id;

  private Map<Object, Object> cache = new HashMap<Object, Object>();
```

上文提到，一级缓存的命中条件，要确认两个查询完全一直需要确认查询语句一致，查询参数一致，rowBounds一致等，所有缓存的key不能通过简单的字符确定。
实际上缓存Map的key是一个CacheKey对象，最终通过CacheKey对象来确认是否为相同的查询。CacheKey的核心参数如下：

```java
  /** 参与hash计算的乘数 */
  private final int multiplier;

  /** CacheKey的hash值，在update函数中实时运算出来的 */
  private int hashcode;

  /** 校验和，hash值的和 */
  private long checksum;

  /** updateList的中元素个数 */
  private int count;
  
  /** 用于封装多个影响缓存项的因素 */
  private List<Object> updateList;
```
CacheKey的实现原理，是通过自定义hashcode的计算方式，通过hashcode去判断两个不同的CacheKey对象是否相同（相同代表符合一级缓存的命中条件）。
具体hashcode计算如下：

```java
public void update(Object object) {
    // 获取object的hashcode
    int baseHashCode = object == null ? 1 : ArrayUtil.hashCode(object); 
    // updateList数量+1
    count++;
    checksum += baseHashCode;
    baseHashCode *= count;
    // 计算哈希值
    hashcode = multiplier * hashcode + baseHashCode;

    updateList.add(object);
  }
```

mybatis中创建一个CacheKey的过程如下，将命中缓存的条件调用cacheKey.update()方法，进行hashcode计算。
```java
public CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql) {
    if (closed) {
      throw new ExecutorException("Executor was closed.");
    }
    CacheKey cacheKey = new CacheKey();
    cacheKey.update(ms.getId());
    cacheKey.update(rowBounds.getOffset());
    cacheKey.update(rowBounds.getLimit());
    cacheKey.update(boundSql.getSql());
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    TypeHandlerRegistry typeHandlerRegistry = ms.getConfiguration().getTypeHandlerRegistry();
    // mimic DefaultParameterHandler logic
    for (ParameterMapping parameterMapping : parameterMappings) {
      if (parameterMapping.getMode() != ParameterMode.OUT) {
        Object value;
        String propertyName = parameterMapping.getProperty();
        if (boundSql.hasAdditionalParameter(propertyName)) {
          value = boundSql.getAdditionalParameter(propertyName);
        } else if (parameterObject == null) {
          value = null;
        } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
          value = parameterObject;
        } else {
          MetaObject metaObject = configuration.newMetaObject(parameterObject);
          value = metaObject.getValue(propertyName);
        }
        cacheKey.update(value);
      }
    }
    if (configuration.getEnvironment() != null) {
      // issue #176
      cacheKey.update(configuration.getEnvironment().getId());
    }
    return cacheKey;
  }
```

> 对于基本类型的hash值一般都是类型值本身。（eg: int a = 100, a的hash值100）
> 
> String类型的hash值，两个字符完全相同，计算出的hash值相同

### 4. 二级缓存的命中条件

mybatis中一二级缓存都是通过CacheKey来事项命中的。二级缓存的命中条件如下：

1. 相同的statementId
2. 相同的Sql与参数
3. 返回行数范围相同 （RowBounds）
4. 没有使用ResultHandler来自定义返回数据
5. 没有配置UseCache=false 来关闭缓存
6. 没有配置FlushCache=true 来清空缓存
7. 在调用存储过程中不能使用出参，即Parameter中mode=out|inout

> 注意: 

### 5. 二级缓存的开启与使用

mybatis中二级缓存默认不开启，需要主动开启，开启步骤如下：

1. 设置开启二级缓存，在config配置文件中设置。（默认该值为true,可以省略）
```xml
<settings>
    <setting name="cacheEnabled" value="true"/>
</settings>
```

> 开启了enableCache之后，创建SqlSession时Executor会被委托给CachingExecutor，通过CachingExecutor事项二级缓存。
2. Mapper.xml中配置<cache>开启二级缓存 | 在Mapper接口上使用@CacheNamespaces注解开启

> 注意: xml配置不能和注解同时使用，会抛出异常。
> 
> @CacheNamespaces注解 只对使用mybatis注解模式生效，如果sql通过xml来实现，必须通过<cache>开启。

在开启缓存时可以配置一些缓存属性，如缓存失效时间，缓存淘汰策略等。