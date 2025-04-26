package tn.example.charity.Service;

import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.URole;
import tn.example.charity.Entity.User;

import java.util.List;
import java.util.Set;

public interface IUserService {

    Set<URole> getRoles(String username);
    User addUser(User user);
    void deleteUser(Long idUser);
    User modifyUser(User user);
    List<User> getAllUser();
    List<User> retreiveallUser(User User);
    User retrieveallUserbyid(Long idUser);
    User CreateForReset(User u);

    User findByResetToken(String resetToken);
}
