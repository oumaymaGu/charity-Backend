package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.URole;
import tn.example.charity.Entity.User;
import tn.example.charity.Repository.UserRepository;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j

public class UserServiceImpl implements IUserService{


    PasswordEncoder passwordEncoder;
    UserRepository userRepository;

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long idUser) {
        userRepository.deleteById(idUser );

    }

    @Override
    public User modifyUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public List<User> retreiveallUser(User User) {
        return null;
    }

    @Override
    public User retrieveallUserbyid(Long idUser) {
        return userRepository.findById(idUser).get();
    }

    public Set<URole> getRoles(String username) {
        User u = userRepository.findByUsername(username).get();

        return u.getRoles();
    }



    public User CreateForReset(User u) {
        u.setPassword(this.passwordEncoder.encode(u.getPassword()));
        userRepository.save(u);
        return u;
    }


    public User findByResetToken(String resetToken) {
        return userRepository.findByResetToken(resetToken);
    }
}
