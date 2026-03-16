package vn.hoangmelinh.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoangmelinh.jobhunter.domain.Company;
import vn.hoangmelinh.jobhunter.domain.Job;
import vn.hoangmelinh.jobhunter.domain.Skill;
import vn.hoangmelinh.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoangmelinh.jobhunter.domain.response.Job.ResCreateJobDTO;
import vn.hoangmelinh.jobhunter.repository.CompanyRepository;
import vn.hoangmelinh.jobhunter.repository.JobRepository;
import vn.hoangmelinh.jobhunter.repository.SkillRepository;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;

    }

    public ResCreateJobDTO handleCreateJob(Job job) {

        // check Skills

        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills().stream().map(skill -> skill.getId()).collect(Collectors.toList());
            List<Skill> skills = this.skillRepository.findAllById(reqSkills);
            job.setSkills(skills);
        }

        // check company
        if (job.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(job.getCompany().getId());
            if (cOptional.isPresent()) {
                job.setCompany(cOptional.get());
            }
        }

        // create a job
        Job currentJob = this.jobRepository.save(job);

        ResCreateJobDTO resCreateJobDTO = new ResCreateJobDTO();

        resCreateJobDTO.setId(currentJob.getId());
        resCreateJobDTO.setTitle(currentJob.getTitle());
        resCreateJobDTO.setLocation(currentJob.getLocation());
        resCreateJobDTO.setSalary(currentJob.getSalary());
        resCreateJobDTO.setQuantity(currentJob.getQuantity());
        resCreateJobDTO.setLevel(currentJob.getLevel());
        resCreateJobDTO.setDescription(currentJob.getDescription());
        resCreateJobDTO.setStartDate(currentJob.getStartDate());
        resCreateJobDTO.setEndDate(currentJob.getEndDate());
        resCreateJobDTO.setActive(currentJob.isActive());
        resCreateJobDTO.setCreatedBy(currentJob.getCreatedBy());
        resCreateJobDTO.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skillNames = currentJob.getSkills().stream().map(skill -> skill.getName())
                    .collect(Collectors.toList());
            resCreateJobDTO.setSkills(skillNames);
        }

        return resCreateJobDTO;
    }

    public ResCreateJobDTO handleUpdateJob(Job j, Job job) {

        // check Skills

        if (job.getSkills() != null) {
            List<Long> reqSkills = job.getSkills().stream().map(skill -> skill.getId())
                    .collect(Collectors.toList());
            List<Skill> skills = this.skillRepository.findAllById(reqSkills);
            job.setSkills(skills);
        }

        // check company
        if (j.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(j.getCompany().getId());
            if (cOptional.isPresent()) {
                job.setCompany(cOptional.get());
            }
        }

        // update correct info
        job.setName(j.getName());
        job.setTitle(j.getTitle());
        job.setDescription(j.getDescription());
        job.setSalary(j.getSalary());
        job.setQuantity(j.getQuantity());
        job.setLocation(j.getLocation());
        job.setLevel(j.getLevel());
        job.setStartDate(j.getStartDate());
        job.setEndDate(j.getEndDate());
        job.setActive(j.isActive());

        // update a job
        Job currentJob = this.jobRepository.save(job);

        // convert response
        ResCreateJobDTO resCreateJobDTO = new ResCreateJobDTO();

        resCreateJobDTO.setId(currentJob.getId());
        resCreateJobDTO.setTitle(currentJob.getTitle());
        resCreateJobDTO.setLocation(currentJob.getLocation());
        resCreateJobDTO.setSalary(currentJob.getSalary());
        resCreateJobDTO.setQuantity(currentJob.getQuantity());
        resCreateJobDTO.setLevel(currentJob.getLevel());
        resCreateJobDTO.setDescription(currentJob.getDescription());
        resCreateJobDTO.setStartDate(currentJob.getStartDate());
        resCreateJobDTO.setEndDate(currentJob.getEndDate());
        resCreateJobDTO.setActive(currentJob.isActive());
        resCreateJobDTO.setCreatedBy(currentJob.getCreatedBy());
        resCreateJobDTO.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skillNames = currentJob.getSkills().stream().map(skill -> skill.getName())
                    .collect(Collectors.toList());
            resCreateJobDTO.setSkills(skillNames);
        }

        return resCreateJobDTO;
    }

    public void deleteJob(Long id) {
        this.jobRepository.deleteById(id);
    }

    public Optional<Job> findJobById(Long id) {

        return this.jobRepository.findById(id);

    }

    public ResultPaginationDTO fetchAll(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = this.jobRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageJob.getNumber() + 1);
        mt.setPageSize(pageJob.getSize());
        mt.setPages(pageJob.getTotalPages());
        mt.setTotal(pageJob.getTotalElements());

        resultPaginationDTO.setMeta(mt);

        resultPaginationDTO.setResult(pageJob.getContent());

        return resultPaginationDTO;

    }

}
