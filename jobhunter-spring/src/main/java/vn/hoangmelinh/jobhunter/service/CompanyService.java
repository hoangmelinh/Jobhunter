package vn.hoangmelinh.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoangmelinh.jobhunter.domain.Company;
import vn.hoangmelinh.jobhunter.domain.User;
import vn.hoangmelinh.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoangmelinh.jobhunter.domain.response.User.ResUserDTO;
import vn.hoangmelinh.jobhunter.repository.CompanyRepository;
import vn.hoangmelinh.jobhunter.repository.UserRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public Optional<Company> fetchCompanyById(Long id) {
        return this.companyRepository.findById(id);
    }

    public ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> companyPage = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();

        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(companyPage.getTotalPages());
        mt.setTotal(companyPage.getTotalElements());

        resultPaginationDTO.setMeta(mt);
        resultPaginationDTO.setResult(companyPage.getContent());

        return resultPaginationDTO;

    }

    public void handleDeleteCompany(long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            Company com = companyOptional.get();
            // fetch all user belong this company
            List<User> userList = com.getUsers();
            this.userRepository.deleteAll(userList);
        }
        this.companyRepository.deleteById(id);
    }

    public Company handleUpdateCompany(Company company) {
        Optional<Company> currentCompanyOptional = this.companyRepository.findById(company.getId());
        if (currentCompanyOptional.isPresent()) {
            Company currentCompany = currentCompanyOptional.get();
            currentCompany.setName(company.getName());
            currentCompany.setLogo(company.getLogo());
            currentCompany.setDescription(company.getDescription());
            currentCompany.setAddress(company.getAddress());
            this.companyRepository.save(currentCompany);
            return currentCompany;
        }
        return null;
    }

    public Optional<Company> handleGetCompanyById(long id) {
        return this.companyRepository.findById(id);
    }

}
