package tn.example.charity.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.example.charity.Entity.Event;
import tn.example.charity.Entity.URole;
import tn.example.charity.Entity.User;
import tn.example.charity.Repository.EventRepository;
import tn.example.charity.Repository.UserRepository;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j

public class UserServiceImpl implements IUserService{


    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    EventRepository eventRepository;

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

    @Override

    public User affecterUserToEvent(Long idUser, Long eventId) {
        User user = userRepository.findById(idUser).get();
        Event event = eventRepository.findById(eventId).get();
        user.getEvents().add(event);
        return userRepository.save(user);
    }

    public User findByResetToken(String resetToken) {
        return userRepository.findByResetToken(resetToken);
    }

    @Override
    public User getUserIdByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User deaffecterUserFromEventByEmail(Long eventId, String email) {
        User user = userRepository.findByEmail(email);
        Event event = eventRepository.findById(eventId).get();
        if (user.getEvents().contains(event)) {
            user.getEvents().remove(event);
            return userRepository.save(user);
        } else {
            // Si l'utilisateur n'est pas associé à l'événement, tu peux retourner une erreur ou un message
            throw new RuntimeException("L'utilisateur n'est pas associé à cet événement.");
        }

    }


}
