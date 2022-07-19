/*
Created by: Margaret Donin
Date created: 10/26/20
Date revised:
 */
package blog.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import blog.dao.CommentDao;
import blog.dao.HashtagDao;
import blog.dao.PostDao;
import blog.dao.RoleDao;
import blog.dao.UserDao;
import blog.dto.Comment;
import blog.dto.Hashtag;
import blog.dto.Post;
import blog.dto.Role;
import blog.dto.User;
import blog.service.HashtagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PostController {

    @Autowired
    private PostDao postDao;

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private HashtagDao hashtagDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private HashtagService hashtagService;

    Set<ConstraintViolation<Post>> postViolations = new HashSet<>();
    Set<ConstraintViolation<Comment>> commentViolations = new HashSet<>();

    @GetMapping({"/", "/home"})
    public String displayHomePage(Model model) {
        List<Post> blogs = postDao.getAllEnabledBlogs();
        List<Post> statics = postDao.getAllEnabledStatics();

        model.addAttribute("blogs", blogs);
        model.addAttribute("statics", statics);
        commentViolations = new HashSet<>();
        
        return "index";
    }

    @GetMapping("postsAll")
    public String postsAll(Model model) {
        List<Post> posts = postDao.getAllPosts();
        List<Post> statics = postDao.getAllEnabledStatics();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userDao.getUserByLogin(authentication.getName());

        model.addAttribute("posts", posts);
        model.addAttribute("statics", statics);
        model.addAttribute("userId", user.getId());

        postViolations = new HashSet<>();

        return "postsAll";
    }

    @GetMapping("/postsByHashtag")
    public String postsByHashtag(Integer hashtagId, Model model) {
        List<Post> blogs = postDao.getAllPostsByHashtagId(hashtagId);
        List<Post> statics = postDao.getAllEnabledStatics();

        Hashtag hashtag = hashtagDao.getHashtagById(hashtagId);

        model.addAttribute("hashtag", hashtag);
        model.addAttribute("blogs", blogs);
        model.addAttribute("statics", statics);
        
        commentViolations = new HashSet<>();

        return "postsByHashtag";
    }

    @GetMapping("/postsByCreator")
    public String postsByCreator(Integer userId, Model model) {
        List<Post> blogs = postDao.getAllPostsByUserId(userId);
        List<Post> statics = postDao.getAllEnabledStatics();

        User user = userDao.getUserById(userId);

        model.addAttribute("blogs", blogs);
        model.addAttribute("statics", statics);
        model.addAttribute("user", user);
        
        commentViolations = new HashSet<>();

        return "postsByCreator";
    }

    @GetMapping("post")
    public String displayPost(Integer id, Model model) {
        List<Post> statics = postDao.getAllEnabledStatics();
        Post post = postDao.getPostById(id);
        List<Comment> comments = commentDao.getAllEnabledCommentsByPostId(id);

        model.addAttribute("statics", statics);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("errors", commentViolations);

        postViolations = new HashSet<>();

        return "post";
    }

    @GetMapping("postAdd")
    public String postAdd(Model model) {

        List<Post> statics = postDao.getAllEnabledStatics();
        model.addAttribute("statics", statics);
        model.addAttribute("errors", postViolations);

        return "postAdd";
    }

    @PostMapping("postAdd")
    public String postAddAction(String title, String text, String start, String end,
            boolean enabled, boolean staticPost, String hashtags) {

        List<Hashtag> tags = hashtagService.getHashtags(hashtags);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userDao.getUserByLogin(authentication.getName());

        Post post = new Post();
        post.setEnabled(enabled);
        post.setStaticPost(staticPost);
        post.setBody(text);
        post.setTitle(title);
        post.setUser(user);
        post.setHashtags(tags);

        try {
            post.setStart(LocalDate.parse(start, DateTimeFormatter.ISO_DATE));
        } catch (DateTimeParseException ex) {
            post.setStart(null);
        }
        try {
            post.setEnd(LocalDate.parse(end, DateTimeFormatter.ISO_DATE));
        } catch (DateTimeParseException ex) {
            post.setEnd(null);
        }

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        postViolations = validate.validate(post);

        if (postViolations.isEmpty()) {
            postDao.addPost(post);
            return "redirect:/post?id=" + post.getId();
        }
        
        return "redirect:/postAdd";
    }

    @PostMapping(value = "/commentAdd")
    public String addComment(Integer postId, String text) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUser(userDao.getUserByLogin(authentication.getName()));
        comment.setText(text);

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        commentViolations = validate.validate(comment);

        if (commentViolations.isEmpty()) {
            
            commentDao.addComment(comment);
        }
        
        return "redirect:/post?id=" + postId;
    }

    @GetMapping("postEdit")
    public String postEdit(Integer id, Model model) {
        List<Post> statics = postDao.getAllEnabledStatics();
        Post post = postDao.getPostById(id);
        String hashtags = hashtagService.stringifyHashtags(post.getHashtags());

        model.addAttribute("statics", statics);
        model.addAttribute("post", post);
        model.addAttribute("hashtags", hashtags);
        model.addAttribute("errors", postViolations);

        return "postEdit";
    }

    @PostMapping(value = "/postEdit")
    public String postEditAction(Integer id, String title, Boolean enabled,
            Boolean staticPost, String text, String start,
            String end, String hashtags) {

        List<Hashtag> tags = hashtagService.getHashtags(hashtags);
        
        Post post = postDao.getPostById(id);
        post.setBody(text);
        post.setStart(LocalDate.parse(start, DateTimeFormatter.ISO_DATE));
        post.setEnd(LocalDate.parse(end, DateTimeFormatter.ISO_DATE));
        post.setTitle(title);
        post.setHashtags(tags);

        if (staticPost != null) {
            post.setStaticPost(staticPost);
        } else {
            post.setStaticPost(false);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userDao.getUserByLogin(authentication.getName());
        Role role = roleDao.getRoleByRole("ROLE_ADMIN");

        if (user.getRoles().contains(role) && enabled != null) {
            post.setEnabled(enabled);
        } else {
            post.setEnabled(false);
        }

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        postViolations = validate.validate(post);

        if (postViolations.isEmpty()) {
            postDao.editPost(post);
            return "redirect:/post?id=" + id;
        }

        return "redirect:/postEdit?id=" + id;
    }

    @PostMapping(value = "/postDelete")
    public String postDelete(Integer id) {
        postDao.deletePost(id);

        return "redirect:/postsAll";
    }

    @GetMapping("postDeleteExpired")
    public String postDelete() {
        postDao.deleteExpiredPosts();

        return "redirect:/postsAll";
    }

}
