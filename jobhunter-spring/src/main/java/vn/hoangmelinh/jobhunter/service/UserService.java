package vn.hoangmelinh.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoangmelinh.jobhunter.domain.User;
import vn.hoangmelinh.jobhunter.domain.dto.Meta;
import vn.hoangmelinh.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoangmelinh.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public Optional<User> fetchUserById(long id) {
        return userRepository.findById(id);
    }


    public ResultPaginationDTO findAllUser(Specification<User> spec, Pageable page) {

        Page<User> userPage = this.userRepository.findAll(spec, page);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(userPage.getNumber() + 1);
        mt.setPageSize(userPage.getSize());
        mt.setPages(userPage.getTotalPages());
        mt.setTotal(userPage.getTotalElements());
        
        resultPaginationDTO.setMeta(mt);
        resultPaginationDTO.setResult(userPage.getContent());

        return resultPaginationDTO;
    }

    public User handleUpdateUser(User user) {
        Optional<User> currentUserOptional = this.userRepository.findById(user.getId());
        if (currentUserOptional.isPresent()) {
            User currentUser = currentUserOptional.get();
            currentUser.setEmail(user.getEmail());
            currentUser.setName(user.getName());
            currentUser.setPassword(user.getPassword());
            this.userRepository.save(currentUser);
            return currentUser;
        }
        return null;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
 }
