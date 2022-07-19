/*
Created by: Margaret Donin
Date created:
Date revised:
*/

package blog.controller;

import java.util.List;
import blog.dao.PostDao;
import blog.dto.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    
    @Autowired
    private PostDao postDao;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        List<Post> posts = postDao.getAllEnabledStatics();
        model.addAttribute("statics", posts);
        
        return "login";
    }
}
