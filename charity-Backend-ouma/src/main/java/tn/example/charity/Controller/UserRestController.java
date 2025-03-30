package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.User;
import tn.example.charity.Service.IDonService;
import tn.example.charity.Service.IUserService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserRestController {
    IUserService userService;

    @PostMapping("/add-user")
    public User addUser(@RequestBody User usr) {
        User user= userService.addUser(usr);
        return user;
    }

    @DeleteMapping("/remove-user/{user-id}")
    public void removeUser(@PathVariable("User-id")Long user) {

       userService.deleteUser(user);
    }


    @PutMapping("/modifyUser")
    public User modifyUser(@RequestBody User usr) {
        User user = userService.modifyUser(usr);
        return user;
    }
    @GetMapping("/retrieve-all-User")

    public List<User> getUser() {
        List<User> listUser = userService.getAllUser();
        return listUser;

    }

    @GetMapping("/get-User/{User-id}")
    public User getUser(@PathVariable("User-id") Long usr) {
        User user= userService.retrieveallUserbyid(usr);
        return user;
    }
}
