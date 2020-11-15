/*
Created by: Margaret Donin
Date created: 10/20/20
Date revised:
 */
package masteryddwa.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class UserDaoDBTest {

    @Autowired
    private HashtagDao hashtagDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    private User userOne;
    private Role role;
    private Set<Role> roles = new HashSet<>();
//    private BufferedImage img = null;

    public UserDaoDBTest() {
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

        List<Role> dbroles = roleDao.getAllRoles();
        for (Role r : dbroles) {
            roleDao.deleteRole(r.getId());
        }

        List<User> users = userDao.getAllUsers();
        for (User u : users) {
            userDao.deleteUserById(u.getId());
        }

//        try {
//            img = ImageIO.read(new File("generic.png"));
//        } catch (IOException e) {
//        }
        role = new Role();
        role.setRole("admin");
        role = roleDao.addRole(role);

        roles.clear();
        roles.add(role);

        userOne = new User();
        userOne.setUsername("Mrs. Penguin");
        userOne.setEmail("pen1@guin.com");
        userOne.setPassword("password");
        userOne.setEnabled(true);
//        userOne.setImg(img);
        userOne.setRoles(roles);
        userOne = userDao.addUser(userOne);
    }

    @Test
    public void testAddGetUser() {
        // Arrange

        // Act
        User userTwo = userDao.getUserById(userOne.getId());

        // Assert
        assertNotNull(userTwo, "User two should be not null.");
        assertEquals(userOne, userTwo, "User one and two should be equal.");
    }

    @Test
    public void testGetUserByLogin() {
        // Arrange

        // Act
        User userTwo = userDao.getUserByLogin(userOne.getUsername());
        User userThree = userDao.getUserByLogin(userOne.getEmail());

        // Assert
        assertNotNull(userTwo, "User two should be not null.");
        assertEquals(userOne, userTwo, "User one and two should be equal.");
        assertEquals(userOne, userThree, "User one and three should be equal.");

    }

    @Test
    public void testGetAllUsers() {
        // Arrange
        User userTwo = new User();
        userTwo.setUsername("Ms. Penguin");
        userTwo.setEmail("pen94@guin.com");
        userTwo.setPassword("password");
        userTwo.setEnabled(true);
        userTwo.setRoles(roles);
//        userTwo.setImg(img);
        userTwo = userDao.addUser(userTwo);

        // Act
        List<User> users = userDao.getAllUsers();

        // Assert
        assertNotNull(users, "Lists should not be null.");
        assertEquals(2, users.size(), "List should have two users.");
        assertTrue(users.contains(userOne), "List should have userOne.");
        assertTrue(users.contains(userTwo), "List should have useTwo");
    }

    @Test
    public void testGetUsersByRoleId() {
        // Arrange
        int id = role.getId();
        Role roleTwo = new Role();
        roleTwo.setRole("user");
        roleTwo = roleDao.addRole(roleTwo);

        Set<Role> newRoles = new HashSet<>();
        newRoles.add(roleTwo);

        User userTwo = new User();
        userTwo.setUsername("Ms. Duck");
        userTwo.setEmail("duck@quack.com");
        userTwo.setPassword("password");
        userTwo.setEnabled(true);
        userTwo.setRoles(newRoles);
        userTwo = userDao.addUser(userTwo);

        // Act
        List<User> users = userDao.getUsersByRoleId(id);

        // Assert
        assertNotNull(users, "List should not be null.");
        assertEquals(1, users.size(), "List should have only one user.");
        assertTrue(users.contains(userOne), "List should only contains user one.");
        assertFalse(users.contains(userTwo), "List should not contain user two.");

    }

    @Test
    public void testGetUsersIfEnabled() {
        // Arrange
        User userTwo = new User();
        userTwo.setUsername("Ms. Duck");
        userTwo.setEmail("duck@quack.com");
        userTwo.setPassword("password");
        userTwo.setEnabled(false);
        userTwo.setRoles(roles);
        userTwo = userDao.addUser(userTwo);

        // Act
        List<User> users = userDao.getUsersIfEnabled();

        // Assert
        assertNotNull(users, "List should not be null.");
        assertEquals(1, users.size(), "List should have only one user.");
        assertTrue(users.contains(userOne), "List should only contains user one.");
        assertFalse(users.contains(userTwo), "List should not contain user two.");

    }

    @Test
    public void testUpdateUser() {
        // Arrange
        int id = userOne.getId();
        User userTwo = userDao.getUserById(id);
        userTwo.setPassword("LeaveMeAlone");
        
        // Act
        userDao.updateUser(userTwo);
        User userThree = userDao.getUserById(id);
        
        // Assert
        assertNotNull(userThree, "User three should not be null.");
        assertEquals(userTwo, userThree, "User two and three should be equal.");
        assertNotEquals(userOne, userThree, "User one and three should not be equal.");
    }

    @Test
    public void testDeleteUserById() {
        // Arrange
        User userTwo = new User();
        userTwo.setUsername("Ms. Duck");
        userTwo.setEmail("duck@quack.com");
        userTwo.setPassword("password");
        userTwo.setEnabled(false);
        userTwo.setRoles(roles);
        userTwo = userDao.addUser(userTwo);

        // Act
        userDao.deleteUserById(userOne.getId());
        List<User> users = userDao.getAllUsers();
        
        // Assert
        assertNotNull(users, "List should not be null.");
        assertEquals(1, users.size(), "List should have only one user.");
        assertFalse(users.contains(userOne), "List should not contain user one.");
        assertTrue(users.contains(userTwo), "List should contain user two.");
    }
}
