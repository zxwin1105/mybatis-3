package test.demo;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import test.entity.UserScores;
import test.mapper.UserScoresMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author zxwin
 * @date 2022/12/5
 */
public class CacheTest {

    private SqlSessionFactory factory;

    @Before
    public void init() throws IOException {

        InputStream resourceAsStream = Resources.getResourceAsStream("resources/Mybatis-config.xml");
        factory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    }

    /**
     * 二级缓存测试
     */
    @Test
    public void cachingTest(){
        SqlSession sqlSession = factory.openSession();
        UserScoresMapper mapper = sqlSession.getMapper(UserScoresMapper.class);
        UserScores scores = mapper.selectOne(1);
        sqlSession.commit();

        SqlSession sqlSession1 = factory.openSession();
        UserScoresMapper mapper1 = sqlSession1.getMapper(UserScoresMapper.class);
        UserScores scores1 = mapper1.selectOne(1);
        sqlSession1.commit();
    }

}
