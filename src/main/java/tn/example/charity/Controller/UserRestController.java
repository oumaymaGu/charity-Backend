package tn.example.charity.Controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.example.charity.Entity.Don;
import tn.example.charity.Entity.Role;
import tn.example.charity.Entity.URole;
import tn.example.charity.Entity.User;
import tn.example.charity.Repository.RoleRepository;
import tn.example.charity.Repository.UserRepository;
import tn.example.charity.Security.JwtUtils;
import tn.example.charity.Service.IDonService;
import tn.example.charity.Service.IUserService;
import tn.example.charity.Service.PasswordValidatorService;
import tn.example.charity.Service.UserDetailsImpl;
import tn.example.charity.dto.JwtResponse;
import tn.example.charity.dto.LoginRequest;
import tn.example.charity.dto.MessageResponse;
import tn.example.charity.dto.SignupRequest;
import tn.example.charity.exception.BadRequestException;
import tn.example.charity.exception.NotFoundException;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class UserRestController {
    IUserService userService;
    JwtUtils jwtUtils;
    PasswordEncoder encoder;
    AuthenticationManager authenticationManager;
    PasswordValidatorService passwordValidatorService;
    RoleRepository roleRepository;
    SessionRegistry sessionRegistry;
    UserRepository userRepository;


    @GetMapping("/getRole/{username}")
    public Set<URole> getRoles(@PathVariable String username) {
        return userService.getRoles(username);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {

            throw new BadRequestException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {

            throw new BadRequestException("Error: Email is already in use!");
        }
        if (!passwordValidatorService.validatePassword(signUpRequest.getPassword())) {

            throw new BadRequestException("Error: Password is not strong enough!");
        }
        // Create new user's account
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<URole> roles = new HashSet<>();

        if (strRoles == null) {
            URole userRole = roleRepository.findByRole(Role.ROLE_SIMPLE_USER)
                    .orElseThrow(() -> new NotFoundException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        URole adminRole = roleRepository.findByRole(Role.ROLE_ADMIN)
                                .orElseThrow(() -> new NotFoundException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "chef":
                        URole chefRole = roleRepository.findByRole(Role.ROLE_CHEF)
                                .orElseThrow(() -> new NotFoundException("Error: Role is not found."));
                        roles.add(chefRole);

                        break;
                    default:
                        URole userRole = roleRepository.findByRole(Role.ROLE_SIMPLE_USER)
                                .orElseThrow(() -> new NotFoundException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

    }




    @PostMapping("/add-user")
    public User addUser (@RequestBody User usr){
        User user = userService.addUser(usr);
        return user;
    }

    @DeleteMapping("/remove-user/{user-id}")
    public void removeUser (@PathVariable("User-id") Long user){

        userService.deleteUser(user);
    }


    @PutMapping("/modifyUser")
    public User modifyUser (@RequestBody User usr){
        User user = userService.modifyUser(usr);
        return user;
    }
    @GetMapping("/retrieve-all-User")

    public List<User> getUser () {
        List<User> listUser = userService.getAllUser();
        return listUser;

    }

    @GetMapping("/get-User/{User-id}")
    public User getUser (@PathVariable("User-id") Long usr){
        User user = userService.retrieveallUserbyid(usr);
        return user;
    }


    // Accessible only by ADMIN
    @GetMapping("/admin-only")
    public String adminOnlyRoute() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return String.format("Hello %s! This route is accessible by ADMIN only.", username);
    }

    // Accessible by both ADMIN and USER
    @GetMapping("/admin-user")
    public String adminAndUserRoute() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return String.format("Hello %s! This route is accessible by both ADMIN and USER.", username);
    }
    @PutMapping("/affecter-user-to-event/{User-id}/{Event-id}")
    public User assignUserToEvent(@PathVariable("User-id") Long UserId,
                                  @PathVariable("Event-id") Long EventId) {
        return userService.affecterUserToEvent(UserId, EventId);
    }
    @DeleteMapping("/deaffecter-user-from-event/{email}/{event-id}")
    public User deassignUserFromEvent(@PathVariable("email") String email,
                                      @PathVariable("event-id") Long eventId) {
        return userService.deaffecterUserFromEventByEmail(eventId, email);
    }

    @GetMapping("/getUserIdByEmail")
    public ResponseEntity<Long> getUserIdByEmail(@RequestParam String email) {
        Long userId = userService.getUserIdByEmail(email).getIdUser();
        return ResponseEntity.ok(userId);
    }
}

