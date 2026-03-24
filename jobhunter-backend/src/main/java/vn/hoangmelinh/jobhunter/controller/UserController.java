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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoangmelinh.jobhunter.domain.User;
import vn.hoangmelinh.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoangmelinh.jobhunter.domain.response.User.ResCreateUserDTO;
import vn.hoangmelinh.jobhunter.domain.response.User.ResUpdateUserDTO;
import vn.hoangmelinh.jobhunter.domain.response.User.ResUserDTO;
import vn.hoangmelinh.jobhunter.service.UserService;
import vn.hoangmelinh.jobhunter.util.annotation.ApiMessage;
import vn.hoangmelinh.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User postmanUser)
            throws IdInvalidException {

        boolean isEmailExist = this.userService.isEmailExist(postmanUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("email" + postmanUser.getEmail() + "already exist");
        }
        String hashedPassword = passwordEncoder.encode(postmanUser.getPassword());
        postmanUser.setPassword(hashedPassword);
        User hoangUser = this.userService.handleCreateUser(postmanUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(hoangUser));
    }

    @GetMapping("/users")
    @ApiMessage("fetch all user")
    public ResponseEntity<ResultPaginationDTO> findAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.findAllUser(spec, pageable));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("delete a user by id")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {

        Optional<User> currentUser = this.userService.fetchUserById(id);

        if (currentUser.isEmpty()) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }

        this.userService.handleDeleteUser(id);

        return ResponseEntity.ok("Đã xóa user với id = " + id);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch a user by id")
    public ResponseEntity<ResUserDTO> fetchUser(@PathVariable("id") Long id) throws IdInvalidException {

        Optional<User> hoangUser = this.userService.fetchUserById(id);

        if (hoangUser.isPresent()) {
            return ResponseEntity.ok(this.userService.convertToRestUserDTO(hoangUser.get()));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PutMapping("/users")
    @ApiMessage("update a user by id")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {

        User hoangUser = this.userService.handleUpdateUser(user);

        if (hoangUser == null) {
            throw new IdInvalidException("User với id = " + user.getId() + " không tồn tại");
        }

        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(hoangUser));
    }

}
