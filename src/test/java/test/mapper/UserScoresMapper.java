package test.mapper;

import org.apache.ibatis.annotations.Param;
import test.entity.UserScores;

import java.util.List;

/**
 * @author zxwin
 * @date 2022/12/3
 */
public interface UserScoresMapper{

    UserScores selectOne(@Param("id") Integer id);

    List<UserScores> selectAll();

    int updateScoreById(@Param("userScore") UserScores userScore);
}
