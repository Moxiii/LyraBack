package org.georges.georges.api;

import org.georges.georges.pojos.User;
import org.georges.georges.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserApiController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserService userService;

    @GetMapping("")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping(path = {"/{id}"})
    public String deleteUserById(@PathVariable int id,  Model model){
        logger.info("l'id est :" + id);
         //userService.deleteUserById(id);
        model.addAttribute("userId", id);
        return "delete";

    }
}
