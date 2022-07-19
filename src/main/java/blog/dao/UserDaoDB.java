/*
Created by: Margaret Donin
Date created: 10/19/20
Date revised:
 */
package blog.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import blog.dao.RoleDaoDB.RoleMapper;
import blog.dto.Role;
import blog.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDaoDB implements UserDao {

    @Autowired
    JdbcTemplate jdbc;

    @Transactional
    @Override
    public User addUser(User user) {
//        SerialBlob image = convertImageToByteArray(user.getImg());
//        final String ADD_USER = "INSERT INTO User (username, email, password, enabled, image VALUES (?,?,?,?,?);";

        try {
            final String ADD_USER = "INSERT INTO User (username, email, password, enabled) VALUES (?,?,?,?);";

        jdbc.update(ADD_USER, user.getUsername(), user.getEmail(), user.getPassword(), user.isEnabled());//, image);

        int newId = jdbc.queryForObject("SELECT LAST_INSERT_ID();", Integer.class);
        user.setId(newId);

        for (Role r : user.getRoles()) {
            final String ADD_USER_ROLE = "INSERT INTO UserRole (userId, roleId) VALUES (?,?);";
            jdbc.update(ADD_USER_ROLE, newId, r.getId());
        }

        return user;
        } catch (DuplicateKeyException ex) {
            return null;
        }
    }

    @Override
    public User getUserById(int userId) {
        try {
            final String GET_USER = "SELECT * FROM USER WHERE userId = ?;";

            User user = jdbc.queryForObject(GET_USER, new UserMapper(), userId);

            user.setRoles(getRolesForUser(userId));

            return user;
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public User getUserByLogin(String login) {
        try {
            final String GET_USER
                    = "SELECT * "
                    + "FROM User "
                    + "WHERE userName = ? "
                    + "     OR email = ?;";

            User user = jdbc.queryForObject(GET_USER, new UserMapper(), login, login);

            user.setRoles(getRolesForUser(user.getId()));

            return user;
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<User> getAllUsers() {
        final String GET_ALL_USERS = "SELECT * FROM User;";
        List<User> users = jdbc.query(GET_ALL_USERS, new UserMapper());

        for (User u : users) {
            u.setRoles(getRolesForUser(u.getId()));
        }

        return users;
    }

    @Override
    public List<User> getUsersByRoleId(int roleId) {
        final String GET_ALL_USERS
                = "SELECT u.* "
                + "FROM User u "
                + "JOIN UserRole ur "
                + "     ON u.userId = ur.userId "
                + "WHERE ur.roleId = ?;";
        List<User> users = jdbc.query(GET_ALL_USERS, new UserMapper(), roleId);

        return getRolesForListUser(users);
    }

    @Override
    public List<User> getUsersIfEnabled() {
        final String GET_ALL_USERS = "SELECT * FROM User WHERE enabled = TRUE";
        List<User> users = jdbc.query(GET_ALL_USERS, new UserMapper());

        return getRolesForListUser(users);
    }

    @Transactional
    @Override
    public void updateUser(User user) {
        int userId = user.getId();
//        SerialBlob image = convertImageToByteArray(user.getImg());

        final String UPDATE_USER = "UPDATE User SET email = ?, password = ?, enabled = ? " + /*, image = ? */ "WHERE userId = ?;";
        final String DELETE_USER_ROLE = "DELETE FROM UserRole WHERE userId = ?;";
        final String INSERT_USER_ROLE = "INSERT INTO UserRole(userId, roleId) VALUES(?,?)";

        jdbc.update(UPDATE_USER, user.getEmail(), user.getPassword(), user.isEnabled(), /*image, */ userId);
        jdbc.update(DELETE_USER_ROLE, userId);

        for (Role role : user.getRoles()) {
            jdbc.update(INSERT_USER_ROLE, userId, role.getId());
        }
    }

    @Transactional
    @Override
    public void deleteUserById(int id) {
        final String DELETE_USER_ROLE = "DELETE FROM UserRole WHERE userId = ?;";
        final String DELETE_USER_COMMENT = "DELETE FROM Comment WHERE userId = ?";
        final String DELETE_USER_POST = "DELETE FROM Post WHERE userId = ?;";
        final String DELETE_USER = "DELETE FROM User WHERE userId = ?;";
        
        jdbc.update(DELETE_USER_ROLE, id);
        jdbc.update(DELETE_USER_COMMENT, id);
        jdbc.update(DELETE_USER_POST, id);
        jdbc.update(DELETE_USER, id);
    }

    private Set<Role> getRolesForUser(int userId) {
        final String GET_USER_ROLES
                = "SELECT r.* "
                + "FROM Role r "
                + "JOIN UserRole ur "
                + "     ON r.roleId = ur.roleId "
                + "WHERE ur.userId = ?";

        return new HashSet(jdbc.query(GET_USER_ROLES, new RoleMapper(), userId));
    }

    private List<User> getRolesForListUser(List<User> users) {
        for (User u : users) {
            u.setRoles(getRolesForUser(u.getId()));
        }
        return users;
    }

//    private SerialBlob convertImageToByteArray(BufferedImage img) {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//        try {
//            ImageIO.write(img, "jpg", bos);
//        } catch (IOException ex) {
//        }
//
//        try {
//            ImageIO.write(img, "png", bos);
//        } catch (IOException ex) {
//        }
//
//        try {
//            img = ImageIO.read(new File("generic.png"));
//            ImageIO.write(img, "png", bos);
//        } catch (IOException ex) {
//        }
//
//        SerialBlob blob = null;
//        
//        try {
//            blob = new SerialBlob(bos.toByteArray());
//        } catch (SQLException ex) {
//            
//        }
//        
//        return blob;
//    }
    
    protected static final class UserMapper implements RowMapper<User> {

        @Override
        public User mapRow(ResultSet rs, int i) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("userId"));
            user.setEmail(rs.getString("email"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEnabled(rs.getBoolean("enabled"));
//
//            try {
//                user.setImg(ImageIO.read(rs.getBlob("image").getBinaryStream()));
//            } catch (IOException ex) {
//                // user doesn't get an image D:
//            }
            return user;
        }
    }

}
