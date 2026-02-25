package vn.hoangmelinh.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoangmelinh.jobhunter.domain.Company;
import vn.hoangmelinh.jobhunter.domain.dto.Meta;
import vn.hoangmelinh.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoangmelinh.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> companyPage = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(companyPage.getNumber() + 1);
        mt.setPageSize(companyPage.getSize());
        mt.setPages(companyPage.getTotalPages());
        mt.setTotal(companyPage.getTotalElements());
        
        resultPaginationDTO.setMeta(mt);
        resultPaginationDTO.setResult(companyPage.getContent());

        return resultPaginationDTO;
        
    }

    public void handleDeleteCompany(long id) {
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
    
}
