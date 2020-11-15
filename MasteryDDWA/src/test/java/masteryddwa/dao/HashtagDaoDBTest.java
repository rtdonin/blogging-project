/*
Created by: Margaret Donin
Date created: 10/19/20
Date revised:
 */
package masteryddwa.dao;

import java.time.LocalDate;
import java.util.ArrayList;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HashtagDaoDBTest {

    @Autowired
    private HashtagDao hashtagDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    private Hashtag hashtagOne;

    public HashtagDaoDBTest() {
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

        hashtagOne = new Hashtag();
        hashtagOne.setTag("Coffee");
        hashtagOne = hashtagDao.addHashtag(hashtagOne);

    }

    @Test
    public void testAddGetByIdHashtag() {
        // Arrange
        int hashtagId = hashtagOne.getId();

        // Act
        Hashtag hashtagTwo = hashtagDao.getHashtagById(hashtagId);
        Hashtag hashtagThree = hashtagDao.getHashtagById(hashtagId + 1);

        // Assert
        assertNotNull(hashtagOne, "Hashtag One should not be null.");
        assertNotNull(hashtagTwo, "Hashtag One should not be null.");
        assertEquals(hashtagOne, hashtagTwo, "Both hashtags should be the same.");
        assertNull(hashtagThree, "HashtagThree should be null.");
    }

    @Test
    public void testGetAllHashtags() {
        // Arrange
        Hashtag hashtagTwo = new Hashtag();
        hashtagTwo.setTag("Java");
        hashtagTwo = hashtagDao.addHashtag(hashtagTwo);

        // Act
        List<Hashtag> hashtags = hashtagDao.getAllHashtags();

        // Assert
        assertNotNull(hashtags, "Hashtags should not be null.");
        assertEquals(2, hashtags.size(), "Hashtags list should only have two hashtags.");
        assertTrue(hashtags.contains(hashtagOne), "Hashtag One should be on the list.");
        assertTrue(hashtags.contains(hashtagTwo), "Hashtag Two should be on the list.");
    }

    @Test
    public void testGetAllHashtagsByPostId() {
        // Arrange
        Hashtag hashtagTwo = new Hashtag();
        hashtagTwo.setTag("Coffee");
        hashtagTwo = hashtagDao.addHashtag(hashtagTwo);

        List<Hashtag> hashtags = new ArrayList<>();
        hashtags.add(hashtagOne);
        hashtags.add(hashtagTwo);

        Role role = new Role();
        role.setRole("User");
        role = roleDao.addRole(role);
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = new User();
        user.setUsername("Script");
        user.setEnabled(true);
        user.setPassword("password");
        user.setRoles(roles);
        user.setEmail("undercaffinated@gmail.com");
        user = userDao.addUser(user);

        Post post = new Post();
        post.setBody("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        post.setEnabled(true);
        post.setHashtags(hashtags);
        post.setStart(LocalDate.of(2015, 5, 10));
        post.setEnd(LocalDate.of(2015, 5, 20));
        post.setTitle("Dummy Text");
        post.setUser(user);
        post = postDao.addPost(post);

        // Act
        List<Hashtag> postHashtags = hashtagDao.getAllHashtagsByPostId(post.getId());

        // Assert
        assertNotNull(postHashtags, "List should now be null.");
        assertEquals(2, postHashtags.size(), "Post should have two hashtags.");
        assertTrue(postHashtags.contains(hashtagOne), "Hashtag One should be on the list.");
        assertTrue(postHashtags.contains(hashtagTwo), "Hashtag Two should be on the list.");
    }

    @Test
    public void testEditHashtag() {
        // Arrange
        String newTag = "Python";
        int id = hashtagOne.getId();
        Hashtag hashtagTwo = new Hashtag(id);
        hashtagTwo.setTag(newTag);
        
        // Act
        hashtagDao.editHashtag(hashtagTwo);
        Hashtag hashtagThree = hashtagDao.getHashtagById(id);
        
        // Assert
        assertNotNull(hashtagThree, "Retrieved hashtag should be not null.");
        assertEquals(hashtagTwo, hashtagThree, "Both hashtags should be the same.");
        assertEquals(newTag, hashtagThree.getTag(), "Both tags shold be Python.");
        assertNotEquals(hashtagOne, hashtagThree);
    }

    @Test
    public void testDeleteHashtagById() {
        // Arrange
        Hashtag hashtagTwo = new Hashtag();
        hashtagTwo.setTag("Java");
        hashtagTwo = hashtagDao.addHashtag(hashtagTwo);

        // Act
        hashtagDao.deleteHashtagById(hashtagOne.getId());
        List<Hashtag> hashtags = hashtagDao.getAllHashtags();

        // Assert
        assertNotNull(hashtags, "List should be not null.");
        assertEquals(1, hashtags.size(), "List should have only one hashtag.");
        assertTrue(hashtags.contains(hashtagTwo), "List should have hashtagTwo.");
        assertFalse(hashtags.contains(hashtagOne), "List should not contain hashtagOne.");
    }

}
