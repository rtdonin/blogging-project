/*
Created by: Margaret Donin
Date created:
Date revised:
*/

package masteryddwa.controller;

import java.util.List;
import masteryddwa.dao.HashtagDao;
import masteryddwa.dao.PostDao;
import masteryddwa.dao.UserDao;
import masteryddwa.dto.Hashtag;
import masteryddwa.dto.Post;
import masteryddwa.dto.User;
import masteryddwa.service.HashtagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HashtagController {
    @Autowired
    private PostDao postDao;
    
    @Autowired
    private HashtagDao hashtagDao;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private HashtagService service;

    @GetMapping("/hashtagsAll")
    public String usersAll(Model model) {
        List<Post> statics = postDao.getAllEnabledStatics();
        List<Hashtag> hashtags = hashtagDao.getAllHashtags();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userDao.getUserByLogin(authentication.getName());
        
        model.addAttribute("hashtags", hashtags);
        model.addAttribute("statics", statics);
        model.addAttribute("userId", user.getId());
        
        return "hashtagsAll";
    }

    @PostMapping("/hashtagAdd")
    public String userAdd(String hashtag) {
        service.getHashtags(hashtag);

        return "redirect:/hashtagsAll";
    }

    @PostMapping(value = "/hashtagDelete")
    public String hashtagDelete(Integer hashtagId){
        hashtagDao.deleteHashtagById(hashtagId);
        
        return "redirect:/hashtagsAll";
    }

}
