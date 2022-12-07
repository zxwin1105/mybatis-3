package test.demo;

import org.apache.ibatis.executor.BatchExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.ReuseExecutor;
import org.apache.ibatis.executor.SimpleExecutor;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.Transaction;
import org.junit.Before;
import org.junit.Test;
import test.entity.UserScores;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * @author zxwin
 * @date 2022/12/3
 */
public class ExecutorTest {

    private SqlSessionFactory factory;

    @Before
    public void init() throws IOException {

        InputStream resourceAsStream = Resources.getResourceAsStream("resources/Mybatis-config.xml");
        factory = new SqlSessionFactoryBuilder().build(resourceAsStream);
    }

    /* 关于SimpleExecutor、ReuseExecutor、BatchExecutor不同 */

    /**
     * SimpleExecutor在每次执行SQL时都需要创建statement(PreparedStatement）
     *
     * @throws SQLException SQLException
     */
    @Test
    public void testSimpleExecutor() throws SQLException {
        Configuration configuration = factory.getConfiguration();
        DataSource dataSource = configuration.getEnvironment().getDataSource();
        Transaction transaction = configuration.getEnvironment().getTransactionFactory().newTransaction(dataSource.getConnection());

        Executor executor = new SimpleExecutor(configuration, transaction);
        executor.query(configuration.getMappedStatement("test.mapper.UserScoresMapper.selectOne"), 1, RowBounds.DEFAULT, null);
        executor.query(configuration.getMappedStatement("test.mapper.UserScoresMapper.selectOne"), 2, RowBounds.DEFAULT, null);
        executor.query(configuration.getMappedStatement("test.mapper.UserScoresMapper.selectOne"), 3, RowBounds.DEFAULT, null);

    }

    /**
     * ReuseExecutor对于相同的查询语句，使用同一statement
     *
     * @throws SQLException SQLException
     */
    @Test
    public void testReuseExecutor() throws SQLException {
        Configuration configuration = factory.getConfiguration();
        DataSource dataSource = configuration.getEnvironment().getDataSource();
        Transaction transaction = configuration.getEnvironment().getTransactionFactory().newTransaction(dataSource.getConnection());

        Executor executor = new ReuseExecutor(configuration, transaction);
        executor.query(configuration.getMappedStatement("test.mapper.UserScoresMapper.selectOne"), 1, RowBounds.DEFAULT, null);
        executor.query(configuration.getMappedStatement("test.mapper.UserScoresMapper.selectOne"), 2, RowBounds.DEFAULT, null);
        executor.query(configuration.getMappedStatement("test.mapper.UserScoresMapper.selectOne"), 3, RowBounds.DEFAULT, null);
    }


    /**
     * BatchExecutor仅对于修改操作优化，多条修改语句会被一次编译执行
     * @throws SQLException
     */
    @Test
    public void testBatchExecutor() throws SQLException {
        Configuration configuration = factory.getConfiguration();
        DataSource dataSource = configuration.getEnvironment().getDataSource();
        Transaction transaction = configuration.getEnvironment().getTransactionFactory().newTransaction(dataSource.getConnection());

        Executor executor = new BatchExecutor(configuration, transaction);
        UserScores scores = new UserScores();
        scores.setScores(100);
        scores.setId(1);
        executor.update(configuration.getMappedStatement("test.mapper.UserScoresMapper.updateScoreById"), scores);
        scores.setScores(100);
        scores.setId(2);
        executor.update(configuration.getMappedStatement("test.mapper.UserScoresMapper.updateScoreById"), scores);
        executor.flushStatements();
    }


}
