package tn.example.charity.Service;

import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.User;

import java.util.List;

public interface IUserService {
    User addUser(User user);
    void deleteUser(Long idUser);
    User modifyUser(User user);
    List<User> getAllUser();
    List<User> retreiveallUser(User User);
    User retrieveallUserbyid(Long idUser);
}
