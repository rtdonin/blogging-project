/*
Created by: Margaret Donin
Date created:
Date revised:
*/

package blog.dao;

import java.util.List;
import blog.dto.Role;

public interface RoleDao {
    Role addRole(Role role);
    Role getRoleById(int id);
    Role getRoleByRole(String role);
    List<Role> getAllRoles();
    void editRole(Role role);
    void deleteRole(int id);
}
