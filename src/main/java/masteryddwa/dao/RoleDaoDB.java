/*
Created by: Margaret Donin
Date created: 10/19/20
Date revised:
 */
package masteryddwa.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import masteryddwa.dto.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDaoDB implements RoleDao {

    @Autowired
    JdbcTemplate jdbc;

    @Override
    public Role addRole(Role role) {
        final String INSERT_ROLE = "INSERT INTO role (name) VALUES(?)";
        jdbc.update(INSERT_ROLE, role.getRole());
        int newId = jdbc.queryForObject("select LAST_INSERT_ID()", Integer.class);
        role.setId(newId);
        return role;
    }

    @Override
    public Role getRoleById(int id) {
        try {
            final String SELECT_ROLE_BY_ID = "SELECT * FROM role WHERE roleId = ?";
            return jdbc.queryForObject(SELECT_ROLE_BY_ID, new RoleMapper(), id);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    @Override
    public Role getRoleByRole(String role) {
        try {
            final String SELECT_ROLE_BY_ID = "SELECT * FROM role WHERE name = ?";
            return jdbc.queryForObject(SELECT_ROLE_BY_ID, new RoleMapper(), role);
        } catch (DataAccessException ex) {
            return null;
        }
    }
    
    @Override
    public List<Role> getAllRoles() {
        final String SELECT_ALL_ROLES = "SELECT * FROM role";
        return jdbc.query(SELECT_ALL_ROLES, new RoleMapper());
    }

    @Override
    public void editRole(Role role) {
        final String UPDATE_ROLE = "UPDATE Role SET name = ? WHERE roleId = ?;";
        jdbc.update(UPDATE_ROLE, role.getRole(), role.getId());
    }

    @Override
    public void deleteRole(int id) {
        final String DELETE_USER_ROLE = "DELETE FROM UserRole WHERE roleId = ?;";
        final String DELETE_ROLE = "DELETE FROM Role WHERE roleId = ?";
        jdbc.update(DELETE_USER_ROLE, id);
        jdbc.update(DELETE_ROLE, id);
    }

    protected static final class RoleMapper implements RowMapper<Role> {

        @Override
        public Role mapRow(ResultSet rs, int i) throws SQLException {
            Role role = new Role();
            role.setId(rs.getInt("roleId"));
            role.setRole(rs.getString("name"));
            return role;
        }
    }
}
