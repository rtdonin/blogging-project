/*
Created by: Margaret Donin
Date created: 10/19/20
Date revised:
 */
package masteryddwa.dao;

import java.util.List;
import masteryddwa.dto.Hashtag;
import masteryddwa.dto.Post;
import masteryddwa.dto.Role;
import masteryddwa.dto.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleDaoDBTest {
    
    @Autowired
    private HashtagDao hashtagDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    private Role roleOne;
    
    public RoleDaoDBTest() {
    }
    
    @BeforeEach
    public void setUp() {

        List<Post> posts = postDao.getAllPosts();
        for (Post p : posts) {
            postDao.deletePost(p.getId());
        }
        
        List<Hashtag> hashtags = hashtagDao.getAllHashtags();
        for (Hashtag h : hashtags) {
            hashtagDao.deleteHashtagById(h.getId());
        }

        List<Role> roles = roleDao.getAllRoles();
        for (Role r : roles) {
            roleDao.deleteRole(r.getId());
        }
        
        List<User> users = userDao.getAllUsers();
        for (User u : users) {
            userDao.deleteUserById(u.getId());
        }

        roleOne = new Role();
        roleOne.setRole("admin");
        roleOne = roleDao.addRole(roleOne);

    }

    @Test
    public void testAddGetRole() {
        // Arrange
        
        // Act
        Role roleTwo = roleDao.getRoleById(roleOne.getId());
        
        // Assert
        assertNotNull(roleTwo, "Role two is not null.");
        assertEquals(roleOne, roleTwo, "Role one and role two should be equal.");
    }

    @Test
    public void testGetAllRoles() {
        // Arrange
        Role roleTwo = new Role();
        roleTwo.setRole("employee");
        roleTwo = roleDao.addRole(roleTwo);
        
        // Act
        List<Role> roles = roleDao.getAllRoles();
        
        // Assert
        assertNotNull(roles, "List should not be null.");
        assertEquals(2, roles.size(), "List should have two roles.");
        assertTrue(roles.contains(roleOne), "List should contain role one.");
        assertTrue(roles.contains(roleTwo), "List should contain role two.");
    }

    @Test
    public void testEditRole() {
        // Arrange
        int id = roleOne.getId();
        String role = "employee";
        Role roleTwo = new Role();
        roleTwo.setId(id);
        roleTwo.setRole(role);
        
        // Act
        roleDao.editRole(roleTwo);
        Role roleThree = roleDao.getRoleById(id);
        
        // Assert
        assertEquals(roleTwo, roleThree, "Roles two and three should be equal");
        assertNotEquals(roleOne, roleThree, "Should be equal.");
    }

    @Test
    public void testDeleteRole() {
        // Arrange
        Role roleTwo = new Role();
        roleTwo.setRole("employee");
        roleTwo = roleDao.addRole(roleTwo);
        
        // Act
        roleDao.deleteRole(roleOne.getId());
        List<Role> roles = roleDao.getAllRoles();
        
        // Assert
        assertNotNull(roles, "List should not be null.");
        assertEquals(1, roles.size(), "List should have one role.");
        assertFalse(roles.contains(roleOne), "List should not contain role one.");
        assertTrue(roles.contains(roleTwo), "List should contain role two.");
    }
    
}
