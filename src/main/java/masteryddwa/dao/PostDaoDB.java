/*
Created by: Margaret Donin
Date created: 10/19/20
Date revised:
 */
package masteryddwa.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import masteryddwa.dao.HashtagDaoDB.HashtagMapper;
import masteryddwa.dao.UserDaoDB.UserMapper;
import masteryddwa.dto.Hashtag;
import masteryddwa.dto.Post;
import masteryddwa.dto.Role;
import masteryddwa.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PostDaoDB implements PostDao {

    @Autowired
    JdbcTemplate jdbc;

    @Transactional
    @Override
    public Post addPost(Post post) {
        final String ADD_POST = "INSERT INTO Post (title, enabled, static, body, "
                + "start, end, userId) VALUES (?,?,?,?,?,?,?);";
        jdbc.update(ADD_POST, post.getTitle(), post.isEnabled(), post.isStaticPost(), post.getBody(),
                Date.valueOf(post.getStart()), Date.valueOf(post.getEnd()), post.getUser().getId());

        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID();", Integer.class);
        post.setId(newId);

        for (Hashtag h : post.getHashtags()) {
            final String ADD_USER_ROLE = "INSERT INTO PostHashtag (postId, hashtagId) VALUES (?,?);";
            jdbc.update(ADD_USER_ROLE, newId, h.getId());
        }

        return post;
    }

    @Override
    public Post getPostById(int id) {
        try {
            final String GET_POST = "SELECT * FROM Post WHERE postId = ?;";

            Post post = jdbc.queryForObject(GET_POST, new PostMapper(), id);

            post.setHashtags(getHashtagsForPost(id));
            post.setUser(getUserForPost(id));

            return post;
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Post> getAllPosts() {

        final String GET_ALL_POSTS = "SELECT * FROM Post;";
        List<Post> posts = jdbc.query(GET_ALL_POSTS, new PostMapper());

        return setUserListPost(setListHashtags(posts));
    }

    @Override
    public List<Post> getAllEnabledBlogs() {
        try {
            final String GET_ALL_ENABLED_BLOGS
                    = "SELECT * "
                    + "FROM Post "
                    + "WHERE enabled = TRUE "
                    + "     AND static = FALSE "
                    + "     AND ? BETWEEN start AND end;";

            List<Post> posts = jdbc.query(GET_ALL_ENABLED_BLOGS, new PostMapper(), LocalDate.now());

            return setUserListPost(setListHashtags(posts));

        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Post> getAllEnabledStatics() {
        try {
            final String GET_ALL_ENABLED_STATICS
                    = "SELECT * "
                    + "FROM Post "
                    + "WHERE enabled = TRUE "
                    + "     AND static = TRUE "
                    + "     AND ? BETWEEN start AND end;";

            List<Post> posts = jdbc.query(GET_ALL_ENABLED_STATICS, new PostMapper(), LocalDate.now());

            return setUserListPost(setListHashtags(posts));

        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Post> getAllPostsByUserId(int userId) {
        try {
            final String GET_POSTS_BY_USER
                    = "SELECT * "
                    + "FROM Post "
                    + "WHERE userId = ?"
                    + "     AND enabled =  TRUE"
                    + "     AND ? BETWEEN start AND end;";

            List<Post> posts = jdbc.query(GET_POSTS_BY_USER, new PostMapper(), userId, LocalDate.now());

            return setUserListPost(setListHashtags(posts));

        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<Post> getAllPostsByHashtagId(int hashtagId) {
        try {
            final String GET_HASHTAG_POSTS
                    = "SELECT p.* "
                    + "FROM Post p "
                    + "JOIN PostHashtag ph "
                    + "     ON p.postId = ph.postId "
                    + "WHERE ph.hashtagId = ?"
                    + "     AND p.enabled =  TRUE"
                    + "     AND ? BETWEEN p.start AND p.end;";

            List<Post> posts = jdbc.query(GET_HASHTAG_POSTS, new PostMapper(), hashtagId, LocalDate.now());

            return setUserListPost(setListHashtags(posts));

        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public void editPost(Post post) {
        int postId = post.getId();

        final String EDIT_POST
                = "UPDATE Post "
                + "SET title = ?, enabled = ?, static = ?, body = ?, start = ?, end = ? "
                + "WHERE postId = ?;";
        final String DELETE_ALL_HASHTAGS = "DELETE FROM PostHashtag WHERE postId = ?;";
        final String ADD_HASHTAGS = "INSERT INTO PostHashtag (postId, hashtagId) VALUES (?,?);";

        jdbc.update(EDIT_POST, post.getTitle(), post.isEnabled(), post.isStaticPost(), post.getBody(), Date.valueOf(post.getStart()), Date.valueOf(post.getEnd()), postId);
        jdbc.update(DELETE_ALL_HASHTAGS, postId);

        for (Hashtag h : post.getHashtags()) {
            jdbc.update(ADD_HASHTAGS, postId, h.getId());
        }

    }

    @Override
    public void deletePost(int postId) {
        final String DELETE_ALL_HASHTAGS = "DELETE FROM PostHashtag WHERE postId = ?;";
        final String DELETE_ALL_COMMENTS = "DELETE FROM Comment WHERE postId = ?;";
        final String DELETE_ALL_POSTS = "DELETE FROM Post WHERE postId = ?;";

        jdbc.update(DELETE_ALL_HASHTAGS, postId);
        jdbc.update(DELETE_ALL_COMMENTS, postId);
        jdbc.update(DELETE_ALL_POSTS, postId);
    }

    @Override
    public void deleteExpiredPosts() {
        final String GET_ALL_EXPIRED_POSTS = "SELECT * FROM Post WHERE end < CURRENT_DATE();";
        final String DELETE_ALL_HASHTAGS = "DELETE FROM PostHashtag WHERE postId = ?;";
        final String DELETE_ALL_COMMENTS = "DELETE FROM Comment WHERE postId = ?;";
        final String DELETE_EXPIRED_POSTS = "DELETE FROM Post WHERE end < CURRENT_DATE();";

        List<Post> posts = jdbc.query(GET_ALL_EXPIRED_POSTS, new PostMapper());

        for (Post p : posts) {
            jdbc.update(DELETE_ALL_HASHTAGS, p.getId());
            jdbc.update(DELETE_ALL_COMMENTS, p.getId());
        }
        jdbc.update(DELETE_EXPIRED_POSTS);

    }

    private List<Hashtag> getHashtagsForPost(int postId) {
        final String GET_POST_HASHTAGS
                = "SELECT h.* "
                + "FROM Hashtag h "
                + "JOIN PostHashtag ph "
                + "     ON h.hashtagId = ph.hashtagId "
                + "WHERE ph.postId = ?";
        return jdbc.query(GET_POST_HASHTAGS, new HashtagMapper(), postId);
    }

    private List<Post> setListHashtags(List<Post> posts) {
        for (Post p : posts) {
            p.setHashtags(getHashtagsForPost(p.getId()));
        }

        return posts;
    }

    private User getUserForPost(int postId) {
        final String GET_POST_USER
                = "SELECT u.* "
                + "FROM User u "
                + "JOIN Post p "
                + "     ON u.userId = p.userId "
                + "WHERE p.postId = ?;";

        final String GET_USER_ROLES
                = "SELECT r.* "
                + "FROM Role r "
                + "JOIN UserRole ur "
                + "     ON r.roleId = ur.roleId "
                + "WHERE ur.userId = ?";

        User user = jdbc.queryForObject(GET_POST_USER, new UserMapper(), postId);
        Set<Role> roles = new HashSet(jdbc.query(GET_USER_ROLES, new RoleDaoDB.RoleMapper(), user.getId()));
        user.setRoles(roles);

        return user;
    }

    private List<Post> setUserListPost(List<Post> posts) {
        for (Post p : posts) {
            p.setUser(getUserForPost(p.getId()));
        }

        return posts;
    }

    private static final class PostMapper implements RowMapper<Post> {

        @Override
        public Post mapRow(ResultSet rs, int i) throws SQLException {
            Post post = new Post();
            post.setId(rs.getInt("postId"));
            post.setTitle(rs.getString("title"));
            post.setStaticPost(rs.getBoolean("static"));
            post.setEnabled(rs.getBoolean("enabled"));
            post.setBody(rs.getString("body"));
            post.setStart(rs.getDate("start").toLocalDate());
            post.setEnd(rs.getDate("end").toLocalDate());
            post.setUser(new User(rs.getInt("userId")));

            return post;
        }
    }
}
