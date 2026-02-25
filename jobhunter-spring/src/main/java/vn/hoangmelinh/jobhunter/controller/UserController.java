package vn.hoangmelinh.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoangmelinh.jobhunter.domain.User;
import vn.hoangmelinh.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoangmelinh.jobhunter.service.UserService;
import vn.hoangmelinh.jobhunter.util.error.IdInvalidException;

@RestController
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User postmanUser) {

        String hashedPassword = passwordEncoder.encode(postmanUser.getPassword());
        postmanUser.setPassword(hashedPassword);
        User hoangUser = this.userService.handleCreateUser(postmanUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(hoangUser);
    }

    @GetMapping("/users")
    public ResponseEntity<ResultPaginationDTO> findAllUser(
        @Filter Specification<User> spec,
        Pageable pageable
    ) {
         
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.findAllUser(spec, pageable));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        if (id >= 1500) {
            throw new IdInvalidException("id not be greater than 1500");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok("hoang");
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> fetchUser(@PathVariable("id") Long id) {


        Optional<User> hoangUser = this.userService.fetchUserById(id);

        if (hoangUser.isPresent()) {
            return ResponseEntity.ok(hoangUser.get());
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);   
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {

        User hoangUser = this.userService.handleUpdateUser(user);

        if (hoangUser != null) {
            return ResponseEntity.ok(hoangUser);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    
}
