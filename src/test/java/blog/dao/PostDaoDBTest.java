/*
Created by: Margaret Donin
Date created: 10/22/20
Date revised:
 */
package blog.dao;

import blog.dao.HashtagDao;
import blog.dao.UserDao;
import blog.dao.PostDao;
import blog.dao.CommentDao;
import blog.dao.RoleDao;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import blog.dto.Comment;
import blog.dto.Hashtag;
import blog.dto.Post;
import blog.dto.Role;
import blog.dto.User;
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
public class PostDaoDBTest {

    @Autowired
    private HashtagDao hashtagDao;

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private CommentDao commentDao;

    private User userOne;
    private Post postOne;
    private Hashtag hashtagOne;
    private List<Hashtag> hashtags;

    public PostDaoDBTest() {
    }

    @BeforeEach
    public void setUp() {
        List<Post> posts = postDao.getAllPosts();
        for (Post p : posts) {
            postDao.deletePost(p.getId());
        }

        List<Hashtag> DBhashtags = hashtagDao.getAllHashtags();
        for (Hashtag h : DBhashtags) {
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

        Role role = new Role();
        role.setRole("admin");
        role = roleDao.addRole(role);

        Set<Role> userRoles = new HashSet<>();
        roles.add(role);

        userOne = new User();
        userOne.setUsername("Mrs. Penguin");
        userOne.setEmail("pen1@guin.com");
        userOne.setPassword("password");
        userOne.setEnabled(true);
//        userOne.setImg(img);
        userOne.setRoles(userRoles);
        userOne = userDao.addUser(userOne);

        hashtagOne = new Hashtag();
        hashtagOne.setTag("JavaScript");
        hashtagOne = hashtagDao.addHashtag(hashtagOne);

        Hashtag hashtagTwo = new Hashtag();
        hashtagTwo.setTag("CoffeeScript");
        hashtagTwo = hashtagDao.addHashtag(hashtagTwo);

        hashtags = new ArrayList<>();
        hashtags.add(hashtagOne);

        postOne = new Post();
        postOne.setBody("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        postOne.setEnabled(true);
        postOne.setStaticPost(false);
        postOne.setHashtags(hashtags);
        postOne.setStart(LocalDate.of(2000, 5, 10));
        postOne.setEnd(LocalDate.of(2100, 5, 20));
        postOne.setTitle("Dummy Text");
        postOne.setUser(userOne);
        postOne = postDao.addPost(postOne);
    }

    @Test
    public void testAddGetPost() {
        // Arrange

        // Act
        Post postTwo = postDao.getPostById(postOne.getId());

        // Assert
        assertNotNull(postTwo, "Post two should be not null.");
        assertEquals(postOne, postTwo, "Post one and post two should be equal.");
    }

    @Test
    public void testGetAllPosts() {
        // Arrange
        Post postTwo = new Post();
        postTwo.setBody("Nunc faucibus sem sit amet est ornare, mattis cursus leo finibus.");
        postTwo.setEnabled(true);
        postTwo.setStaticPost(false);
        postTwo.setHashtags(hashtags);
        postTwo.setStart(LocalDate.of(2015, 5, 10));
        postTwo.setEnd(LocalDate.of(2015, 5, 20));
        postTwo.setTitle("Dummy Text");
        postTwo.setUser(userOne);
        postTwo = postDao.addPost(postTwo);

        // Act
        List<Post> posts = postDao.getAllPosts();

        // Assert
        assertNotNull(posts, "List should not be null.");
        assertEquals(2, posts.size(), "List should have two posts.");
        assertTrue(posts.contains(postOne), "List should contain postOne.");
        assertTrue(posts.contains(postTwo), "List should contain postTwo.");

    }

    @Test
    public void testGetAllEnabledBlogs() {
        // Arrange
        Post postTwo = new Post();
        postTwo.setBody("Nunc faucibus sem sit amet est ornare, mattis cursus leo finibus.");
        postTwo.setEnabled(false);
        postTwo.setStaticPost(false);
        postTwo.setHashtags(hashtags);
        postTwo.setStart(LocalDate.of(2015, 5, 10));
        postTwo.setEnd(LocalDate.of(2100, 5, 20));
        postTwo.setTitle("Dummy Text");
        postTwo.setUser(userOne);
        postTwo = postDao.addPost(postTwo);

        Post postThree = new Post();
        postThree.setBody("Nunc faucibus sem sit amet est ornare, mattis cursus leo finibus.");
        postThree.setEnabled(true);
        postThree.setStaticPost(false);
        postThree.setHashtags(hashtags);
        postThree.setStart(LocalDate.of(2015, 5, 10));
        postThree.setEnd(LocalDate.of(2015, 5, 20));
        postThree.setTitle("Dummy Text");
        postThree.setUser(userOne);
        postThree = postDao.addPost(postThree);

        // Act
        List<Post> posts = postDao.getAllEnabledBlogs();

        // Assert
        assertNotNull(posts, "List should not be null.");
        assertEquals(1, posts.size(), "List should have one posts.");
        assertTrue(posts.contains(postOne), "List should contain postOne.");
        assertFalse(posts.contains(postTwo), "List should not contain postTwo.");
        assertFalse(posts.contains(postThree), "List should not contain postThree.");

    }

    @Test
    public void testGetAllEnabledStatics() {
        // Arrange
        Post postTwo = new Post();
        postTwo.setBody("Nunc faucibus sem sit amet est ornare, mattis cursus leo finibus.");
        postTwo.setEnabled(true);
        postTwo.setStaticPost(true);
        postTwo.setHashtags(hashtags);
        postTwo.setStart(LocalDate.of(2015, 5, 10));
        postTwo.setEnd(LocalDate.of(2100, 5, 20));
        postTwo.setTitle("Dummy Text");
        postTwo.setUser(userOne);
        postTwo = postDao.addPost(postTwo);

        Post postThree = new Post();
        postThree.setBody("Nunc faucibus sem sit amet est ornare, mattis cursus leo finibus.");
        postThree.setEnabled(true);
        postThree.setStaticPost(true);
        postThree.setHashtags(hashtags);
        postThree.setStart(LocalDate.of(2015, 5, 10));
        postThree.setEnd(LocalDate.of(2015, 5, 20));
        postThree.setTitle("Dummy Text");
        postThree.setUser(userOne);
        postThree = postDao.addPost(postThree);

        Post postFour = new Post();
        postFour.setBody("Nunc faucibus sem sit amet est ornare, mattis cursus leo finibus.");
        postFour.setEnabled(false);
        postFour.setStaticPost(true);
        postFour.setHashtags(hashtags);
        postFour.setStart(LocalDate.of(2015, 5, 10));
        postFour.setEnd(LocalDate.of(2100, 5, 20));
        postFour.setTitle("Dummy Text");
        postFour.setUser(userOne);
        postFour = postDao.addPost(postFour);

        // Act
        List<Post> posts = postDao.getAllEnabledStatics();

        // Assert
        assertNotNull(posts, "List should not be null.");
        assertEquals(1, posts.size(), "List should have one posts.");
        assertFalse(posts.contains(postOne), "List should not contain postOne.");
        assertTrue(posts.contains(postTwo), "List should contain postTwo.");
        assertFalse(posts.contains(postThree), "List should contain postThree.");
        assertFalse(posts.contains(postFour), "List should not contain postFour.");

    }

    @Test
    public void testGetAllPostsByUserId() {
        // Arrange
        User userTwo = new User();
        userTwo.setUsername("Ms. Penguin");
        userTwo.setEmail("pen94@guin.com");
        userTwo.setPassword("password");
        userTwo.setEnabled(true);
        userTwo.setRoles(userOne.getRoles());
//        userTwo.setImg(img);
        userTwo = userDao.addUser(userTwo);

        Post postTwo = new Post();
        postTwo.setBody("Nunc faucibus sem sit amet est ornare, mattis cursus leo finibus.");
        postTwo.setEnabled(true);
        postTwo.setStaticPost(true);
        postTwo.setHashtags(hashtags);
        postTwo.setStart(LocalDate.of(2015, 5, 10));
        postTwo.setEnd(LocalDate.of(2100, 5, 20));
        postTwo.setTitle("Dummy Text");
        postTwo.setUser(userTwo);
        postTwo = postDao.addPost(postTwo);

        // Act
        List<Post> posts = postDao.getAllPostsByUserId(userTwo.getId());

        // Assert
        assertNotNull(posts, "List should not be null.");
        assertEquals(1, posts.size(), "List should have one posts.");
        assertFalse(posts.contains(postOne), "List should not contain postOne.");
        assertTrue(posts.contains(postTwo), "List should contain postTwo.");

    }

    @Test
    public void testGetAllPostsByHashtagId() {
        // Arrange
        Hashtag hashtagTwo = new Hashtag();
        hashtagTwo.setTag("CoffeeScript");
        hashtagTwo = hashtagDao.addHashtag(hashtagTwo);

        List<Hashtag> hashtagList = new ArrayList<>();
        hashtagList.add(hashtagTwo);

        Post postTwo = new Post();
        postTwo.setBody("Nunc faucibus sem sit amet est ornare, mattis cursus leo finibus.");
        postTwo.setEnabled(true);
        postTwo.setStaticPost(true);
        postTwo.setHashtags(hashtagList);
        postTwo.setStart(LocalDate.of(2015, 5, 10));
        postTwo.setEnd(LocalDate.of(2100, 5, 20));
        postTwo.setTitle("Dummy Text");
        postTwo.setUser(userOne);
        postTwo = postDao.addPost(postTwo);

        // Act
        List<Post> posts = postDao.getAllPostsByHashtagId(hashtagTwo.getId());

        // Assert
        assertNotNull(posts, "List should not be null.");
        assertEquals(1, posts.size(), "List should have one posts.");
        assertFalse(posts.contains(postOne), "List should not contain postOne.");
        assertTrue(posts.contains(postTwo), "List should contain postTwo.");

    }

    @Test
    public void testEditPost() {
        // Arrange
        int id = postOne.getId();
        Post postTwo = postDao.getPostById(id);
        postTwo.setBody("Nunc faucibus sem sit amet est ornare, mattis cursus leo finibus.");

        // Act
        postDao.editPost(postTwo);
        Post postThree = postDao.getPostById(id);

        // Assert
        assertNotNull(postThree, "Post three should not be null.");
        assertEquals(postTwo, postThree, "Posts two and three should be the same.");
        assertNotEquals(postOne, postThree, "Posts one and three should not be the same.");
    }

    @Test
    public void testDeletePost() {
        // Arrange
        int id = postOne.getId();
        Comment comment = new Comment();
        comment.setPostId(id);
        comment.setUser(postOne.getUser());
        comment.setText("A comment.");
        comment = commentDao.addComment(comment);

        // Act
        postDao.deletePost(id);
        Post postTwo = postDao.getPostById(id);
        Comment commentTwo = commentDao.getCommmentById(comment.getId());

        // Assert
        assertNull(postTwo, "Post two should be null.");
        assertNull(commentTwo, "Should be deleted because the post was delted.");
    }

    @Test
    public void testDeleteExpiredPosts() {
        // Arrange
        Post postTwo = new Post();
        postTwo.setBody("Nunc faucibus sem sit amet est ornare, mattis cursus leo finibus.");
        postTwo.setEnabled(true);
        postTwo.setStaticPost(false);
        postTwo.setHashtags(hashtags);
        postTwo.setStart(LocalDate.of(2015, 5, 10));
        postTwo.setEnd(LocalDate.of(2015, 5, 20));
        postTwo.setTitle("Dummy Text");
        postTwo.setUser(userOne);
        postTwo = postDao.addPost(postTwo);

        // Act
        postDao.deleteExpiredPosts();
        List<Post> posts = postDao.getAllPosts();

        // Assert
        assertNotNull(posts, "List should not be null.");
        assertEquals(1, posts.size(), "List should have one posts.");
        assertTrue(posts.contains(postOne), "List should contain postOne.");
        assertFalse(posts.contains(postTwo), "List should not contain postTwo.");
    }

}
