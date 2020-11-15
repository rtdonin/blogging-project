/*
Created by: Margaret Donin
Date created: 10/19/20
Date revised:
 */
package masteryddwa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import masteryddwa.dao.UserDaoDB.UserMapper;
import masteryddwa.dto.Comment;
import masteryddwa.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CommentDaoDB implements CommentDao {

    @Autowired
    JdbcTemplate jdbc;

    @Transactional
    @Override
    public Comment addComment(Comment comment) {
        final String ADD_COMMENT = "INSERT INTO Comment (text, userId, postId) VALUES (?,?,?);";
        jdbc.update(ADD_COMMENT, comment.getText(), comment.getUser().getId(), comment.getPostId());
        
        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID();", Integer.class);
        final String GET_COMMENT = "SELECT * FROM Comment WHERE commentId = ?;";
        
        comment = jdbc.queryForObject(GET_COMMENT, new CommentMapper(), newId);
        comment.setUser(getUserForComment(newId));

        return comment;
    }

    @Override
    public Comment getCommmentById(int id) {
        try {
            final String GET_COMMENT = "SELECT * FROM Comment WHERE commentId = ?;";
            Comment comment = jdbc.queryForObject(GET_COMMENT, new CommentMapper(), id);
            comment.setUser(getUserForComment(comment.getId()));

            return comment;
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Comment> getAllComments() {
        final String GET_ALL_COMMENTS = "SELECT * FROM Comment;";
        List<Comment> comments = jdbc.query(GET_ALL_COMMENTS, new CommentMapper());

        return getUsersForCommentList(comments);
    }

    @Override
    public List<Comment> getAllEnabledCommentsByPostId(int postId) {
        final String GET_ALL_ENABLED_COMMENTS
                = "SELECT c.* "
                + "FROM Comment c "
                + "JOIN User u "
                + "     ON c.userId = u.userId "
                + "JOIN Post p "
                + "     ON c.postId = p.postId "
                + "WHERE u.enabled = TRUE "
                + "     AND p.postId = ?;";

        List<Comment> comments = jdbc.query(GET_ALL_ENABLED_COMMENTS, new CommentMapper(), postId);

        return getUsersForCommentList(comments);
    }

    @Override
    public void editComment(Comment comment) {
        final String EDIT_COMMENT = "UPDATE Comment SET text = ? WHERE commentId = ?;";
        jdbc.update(EDIT_COMMENT, comment.getText(), comment.getId());
    }

    @Override
    public void deleteCommentById(int id) {
        final String DELETE_COMMENT = "DELETE FROM Comment WHERE commentId = ?;";
        jdbc.update(DELETE_COMMENT, id);
    }

    private User getUserForComment(int commentId) {
        final String GET_USER_FOR_COMMENT
                = "SELECT u.* "
                + "FROM User u "
                + "JOIN Comment c "
                + "     ON u.userId = c.userId "
                + "WHERE c.commentId = ?;";

        final String GET_USER_ROLES
                = "SELECT r.* "
                + "FROM Role r "
                + "JOIN UserRole ur "
                + "     ON r.roleId = ur.roleId "
                + "WHERE ur.userId = ?";

        User user = jdbc.queryForObject(GET_USER_FOR_COMMENT, new UserMapper(), commentId);
        user.setRoles(new HashSet(jdbc.query(GET_USER_ROLES, new RoleDaoDB.RoleMapper(), user.getId())));

        return user;
    }

    private List<Comment> getUsersForCommentList(List<Comment> comments) {
        for (Comment c : comments) {
            c.setUser(getUserForComment(c.getId()));
        }

        return comments;
    }

    protected static final class CommentMapper implements RowMapper<Comment> {

        @Override
        public Comment mapRow(ResultSet rs, int i) throws SQLException {
            Comment comment = new Comment();
            comment.setId(rs.getInt("commentId"));
            comment.setLdt(rs.getTimestamp("datetime").toLocalDateTime());
            comment.setText(rs.getString("text"));
            comment.setUser(new User(rs.getInt("userId")));
            comment.setPostId(rs.getInt("postId"));

            return comment;
        }
    }
}
