package test.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zxwin
 * @date 2022/12/3
 */
public class UserScores implements Serializable {

    private Integer id;

    private String username;

    private Integer courseid;

    private Integer scores;

    private Date date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getCourseid() {
        return courseid;
    }

    public void setCourseid(Integer courseid) {
        this.courseid = courseid;
    }

    public Integer getScores() {
        return scores;
    }

    public void setScores(Integer scores) {
        this.scores = scores;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "UserScores{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", courseId=" + courseid +
                ", scores=" + scores +
                ", date=" + date +
                '}';
    }
}
