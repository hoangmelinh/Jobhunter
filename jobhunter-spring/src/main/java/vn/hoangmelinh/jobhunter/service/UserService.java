package vn.hoangmelinh.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoangmelinh.jobhunter.domain.Company;
import vn.hoangmelinh.jobhunter.domain.Role;
import vn.hoangmelinh.jobhunter.domain.User;
import vn.hoangmelinh.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoangmelinh.jobhunter.domain.response.User.ResCreateUserDTO;
import vn.hoangmelinh.jobhunter.domain.response.User.ResUpdateUserDTO;
import vn.hoangmelinh.jobhunter.domain.response.User.ResUserDTO;
import vn.hoangmelinh.jobhunter.domain.response.User.ResCreateUserDTO.CompanyUser;
import vn.hoangmelinh.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user) {

        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyService.fetchCompanyById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
        }

        if (user.getRole() != null) {
            Role roleOptional = this.roleService.fetchById(user.getRole().getId());
            user.setRole(roleOptional != null ? roleOptional : null);
        }

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
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(userPage.getNumber() + 1);
        mt.setPageSize(userPage.getSize());
        mt.setPages(userPage.getTotalPages());
        mt.setTotal(userPage.getTotalElements());

        resultPaginationDTO.setMeta(mt);

        List<ResUserDTO> resUserDTOList = userPage.getContent()
                .stream()
                .map(item -> this.convertToRestUserDTO(item))
                .collect(Collectors.toList());

        resultPaginationDTO.setResult(resUserDTOList);

        return resultPaginationDTO;
    }

    public User handleUpdateUser(User user) {
        Optional<User> currentUserOptional = this.userRepository.findById(user.getId());
        Optional<Company> companyOptional = this.companyService.fetchCompanyById(user.getCompany().getId());
        if (currentUserOptional.isPresent()) {
            User currentUser = currentUserOptional.get();
            currentUser.setName(user.getName());
            currentUser.setAge(user.getAge());
            currentUser.setGender(user.getGender());
            currentUser.setAddress(user.getAddress());

            // check Company
            if (companyOptional.isPresent()) {
                currentUser.setCompany(companyOptional.get());
            }

            // check Role
            if (user.getRole() != null) {
                Role roleOptional = this.roleService.fetchById(user.getRole().getId());
                currentUser.setRole(roleOptional);
            }

            this.userRepository.save(currentUser);
            return currentUser;
        }
        return null;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser companyUser = new ResCreateUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreatedAt(user.getCreatedAt());

        if (user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }

        return res;
    }

    public ResUserDTO convertToRestUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser companyUser = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());

        if (user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }

        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }

        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.ComanyUser comanyUser = new ResUpdateUserDTO.ComanyUser();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setUpdatedAt(user.getUpdatedAt());

        if (user.getCompany() != null) {
            comanyUser.setId(user.getCompany().getId());
            comanyUser.setName(user.getCompany().getName());
            res.setCompany(comanyUser);
        }

        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.userRepository.findByEmail(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }
}
