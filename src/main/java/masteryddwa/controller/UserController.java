/*
Created by: Margaret Donin
Date created: 10/26/20
Date revised:
 */
package masteryddwa.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import masteryddwa.dao.PostDao;
import masteryddwa.dao.RoleDao;
import masteryddwa.dao.UserDao;
import masteryddwa.dto.Post;
import masteryddwa.dto.Role;
import masteryddwa.dto.User;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @Autowired
    private PostDao postDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    PasswordEncoder encoder;

    Set<ConstraintViolation<User>> violations = new HashSet<>();

    @GetMapping("usersAll")
    public String usersAll(Model model) {
        List<User> adminUsers = userDao.getUsersByRoleId(roleDao.getRoleByRole("ROLE_ADMIN").getId());
        List<User> employeeUsers = userDao.getUsersByRoleId(roleDao.getRoleByRole("ROLE_EMPLOYEE").getId());
        List<User> regUsers = userDao.getUsersByRoleId(roleDao.getRoleByRole("ROLE_USER").getId());
        List<Post> statics = postDao.getAllEnabledStatics();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userDao.getUserByLogin(authentication.getName());

        model.addAttribute("adminUsers", adminUsers);
        model.addAttribute("creatorUsers", employeeUsers);
        model.addAttribute("users", regUsers);
        model.addAttribute("statics", statics);
        model.addAttribute("userId", user.getId());

        violations.clear();

        return "usersAll";
    }

    @GetMapping("userAdd")
    public String userAdd(Model model, Integer error) {
        List<Role> roles = roleDao.getAllRoles();
        List<Post> statics = postDao.getAllEnabledStatics();

        model.addAttribute("roles", roles);
        model.addAttribute("statics", statics);
        model.addAttribute("errorSet", violations);

        if (error != null) {
            String message = "";

            switch(error) {
                case 2: message = "Username, password, and email can not be blank.";
                    break;
                case 3: message = "Username is taken. Select a different username.";
                    break;
                case 4: message = "Username is too long.";
                    break;
                default: message = "Could not save information please try again.";
            }

            model.addAttribute("errors", message);
        }

        return "userAdd";
    }

    @PostMapping(value = "userAdd")
    public String userAdd(String username, String password, String email) {

        if (password.isBlank() || username.isBlank() || email.isBlank()) {
            return "redirect:/userAdd?error=2";
        }

        if (username.length() > 100) {
            return "redirect:/userAdd?error=4";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setEmail(email);
        user.setEnabled(true);

        Set<Role> roles = new HashSet<>();
        roles.add(roleDao.getRoleByRole("ROLE_USER"));

        user.setRoles(roles);

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(user);
    
        if (violations.isEmpty()) {
            user = userDao.addUser(user);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User loggedIn = userDao.getUserByLogin(authentication.getName());

            if (user == null) {

                return "redirect:/userAdd?error=3";
            }

            if (loggedIn != null) {
                return "redirect:/usersAll";
            }

            return "redirect:/login";
        }
        
        return "userAdd";

    }

    @GetMapping("user")
    public String viewUser(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Post> statics = postDao.getAllEnabledStatics();
        User user = userDao.getUserByLogin(authentication.getName());

        model.addAttribute("statics", statics);
        model.addAttribute("userId", user.getId());

        violations.clear();

        return "user";
    }

    @GetMapping("userEdit")
    public String editUserDisplay(Integer userId, Model model, Integer error) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedIn = userDao.getUserByLogin(authentication.getName());
        User user = userDao.getUserById(userId);

        if (!loggedIn.getRoles().contains(roleDao.getRoleByRole("ROLE_ADMIN"))) {
            user = loggedIn;
        }

        List<Role> roleList = roleDao.getAllRoles();
        List<Post> statics = postDao.getAllEnabledStatics();

        model.addAttribute("roles", roleList);
        model.addAttribute("statics", statics);

        if (error != null) {
            if (error == 1) {
                model.addAttribute("errors", "Passwords did not match, password was not updated.");
            } else if (error == 2) {
                model.addAttribute("errors", "Password can not be blank.");
            }
        }

        model.addAttribute("errorEmail", violations);
        model.addAttribute("user", user);

        return "userEdit";

    }

    @PostMapping(value = "userEdit")
    public String editUserAction(String email, Integer emailId) {
        User user = userDao.getUserById(emailId);
        user.setEmail(email);

        Validator validate = Validation.buildDefaultValidatorFactory().getValidator();
        violations = validate.validate(user);

        if (violations.isEmpty()) {
            userDao.updateUser(user);
            return "redirect:/user";
        }

        return "redirect:/userEdit?userId=" + emailId;
    }

    /**
     * The userId is listed a few times on the associated HTML page and it is
     * easier to label the id's different names.
     *
     * @param passwordId
     * @param password
     * @param confirmPassword
     * @return
     */
    @PostMapping(value = "/editPassword")
    public String editPassword(Integer passwordId, String password, String confirmPassword) {
        User user = userDao.getUserById(passwordId);

        if (password.isBlank()) {
            return "redirect:/userEdit?userId=" + passwordId + "&error=2";

        } else if (password.equals(confirmPassword)) {
            user.setPassword(encoder.encode(password));
            userDao.updateUser(user);
            return "redirect:/user";

        } else {
            return "redirect:/userEdit?userId=" + passwordId + "&error=1";

        }
    }

    @PostMapping(value = "/userEditRoles")
    public String userEditRoles(Integer userId, String[] roleIdList) {
        User user = userDao.getUserById(userId);
        Set<Role> roleList = new HashSet<>();

        if (roleIdList == null) {
            Role role = roleDao.getRoleByRole("ROLE_USER");
            roleList.add(role);
        } else {
            for (String roleId : roleIdList) {
                Role role = roleDao.getRoleById(Integer.parseInt(roleId));
                roleList.add(role);
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedIn = userDao.getUserByLogin(authentication.getName());

        boolean editSelf = loggedIn.equals(user);

        user.setRoles(roleList);
        userDao.updateUser(user);

        if (editSelf) {
            return "redirect:/user";
        } else {
            return "redirect:/usersAll";
        }
    }

    @PostMapping(value = "/userEnableToggle")
    public String userEnableToggle(Integer userId) {
        User user = userDao.getUserById(userId);
        boolean status = user.isEnabled();
        user.setEnabled(!status);

        userDao.updateUser(user);

        return "redirect:/usersAll";
    }

    @PostMapping(value = "/userDelete")
    public String deleteUser(Integer id) {
        userDao.deleteUserById(id);

        return "redirect:/usersAll";
    }

}
