/*
Created by: Margaret Donin
Date created:
Date revised:
*/

package blog.dao;

import java.util.List;
import blog.dto.User;

public interface UserDao {
    User addUser(User user);
    User getUserById(int id);
    User getUserByLogin(String login);
    List<User> getAllUsers();
    List<User> getUsersByRoleId(int roleId);
    List<User> getUsersIfEnabled();
    void updateUser(User user);
    void deleteUserById(int id);
}
