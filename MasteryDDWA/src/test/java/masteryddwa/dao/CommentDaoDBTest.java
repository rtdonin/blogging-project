/*
Created by: Margaret Donin
Date created: 10/20/20
Date revised:
 */
package masteryddwa.dao;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import masteryddwa.dto.Comment;
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
public class CommentDaoDBTest {

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

    private Comment commentOne;
    private User userOne;
    private Post postOne;

    public CommentDaoDBTest() {
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
        
        List<Comment> comments = commentDao.getAllComments();
        for(Comment c: comments) {
            commentDao.deleteCommentById(c.getId());
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

        postOne = new Post();
        postOne.setBody("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        postOne.setEnabled(true);
        postOne.setHashtags(hashtags);
        postOne.setStart(LocalDate.of(2015, 5, 10));
        postOne.setEnd(LocalDate.of(2015, 5, 20));
        postOne.setTitle("Dummy Text");
        postOne.setUser(userOne);
        postOne = postDao.addPost(postOne);

        commentOne = new Comment();
        commentOne.setUser(userOne);
        commentOne.setPostId(postOne.getId());
        commentOne.setText("Vivamus erat mauris tincidunt.");
        commentOne = commentDao.addComment(commentOne);
    }

    @Test
    public void testAddGetComment() {
        // Arrange

        // Act
        Comment commentTwo = commentDao.getCommmentById(commentOne.getId());
        
        // Assert
        assertNotNull(commentTwo, "Comment two should be not null.");
        assertEquals(commentOne, commentTwo, "Comment one and two should be the same.");
    }

    @Test
    public void testGetAllComments() {
        // Arrange
        Comment commentTwo = new Comment();
        commentTwo.setUser(userOne);
        commentTwo.setPostId(postOne.getId());
        commentTwo.setText("Nunc faucibus sem sit amet est ornare, mattis cursus leo finibus.");
        commentTwo = commentDao.addComment(commentTwo);
        
        // Act
        List<Comment> comments = commentDao.getAllComments();
        
        // Assert
        assertNotNull(comments, "List should not be null.");
        assertEquals(2, comments.size(), "List should have two comments.");
        assertTrue(comments.contains(commentOne), "List should have comment one.");
        assertTrue(comments.contains(commentTwo), "List should have comment two.");
    }

    @Test
    public void testGetAllEnabledCommentsByPostId() {
        // Arrange
        int postId = postOne.getId();
        
        User userTwo = new User();
        userTwo.setUsername("Ms. Duck");
        userTwo.setEmail("duck@quack.com");
        userTwo.setPassword("password");
        userTwo.setEnabled(false);
//        userTwo.setImg(img);
        userTwo.setRoles(userOne.getRoles());
        userTwo = userDao.addUser(userTwo);
        
        Comment commentTwo = new Comment();
        commentTwo.setUser(userTwo);
        commentTwo.setPostId(postId);
        commentTwo.setText("Nunc faucibus sem sit amet est ornare, mattis cursus leo finibus.");
        commentTwo = commentDao.addComment(commentTwo);

        // Act
        List<Comment> comments = commentDao.getAllEnabledCommentsByPostId(postId);
        
        // Assert
        assertNotNull(comments, "List should not be null.");
        assertEquals(1, comments.size(), "List should have two comments.");
        assertTrue(comments.contains(commentOne), "List should have comment one.");
        assertFalse(comments.contains(commentTwo), "List should not have comment two.");
    }

    @Test
    public void testEditComment() {
        // Arrange
        int id = commentOne.getId();
        Comment commentTwo = commentDao.getCommmentById(id);
        commentTwo.setText("Nunc faucibus sem sit amet est ornare, mattis cursus leo finibus.");

        // Act
        commentDao.editComment(commentTwo);
        Comment commentThree = commentDao.getCommmentById(id);
        
        // Assert
        assertNotNull(commentThree, "Comment three should not be null.");
        assertEquals(commentTwo, commentThree, "Comment two and three should be the same.");
        assertNotEquals(commentOne, commentThree, "Comment one and three should not be equal.");
    }

    @Test
    public void testDeleteCommentById() {
        // Arrange
        Comment commentTwo = new Comment();
        commentTwo.setUser(userOne);
        commentTwo.setPostId(postOne.getId());
        commentTwo.setText("Nunc faucibus sem sit amet est ornare, mattis cursus leo finibus.");
        commentTwo = commentDao.addComment(commentTwo);
        
        // Act
        commentDao.deleteCommentById(commentOne.getId());
        List<Comment> comments = commentDao.getAllComments();
        
        // Assert
        assertNotNull(comments, "List should not be null.");
        assertEquals(1, comments.size(), "List should have two comments.");
        assertFalse(comments.contains(commentOne), "List should not have comment one.");
        assertTrue(comments.contains(commentTwo), "List should have comment two.");
    }

}
