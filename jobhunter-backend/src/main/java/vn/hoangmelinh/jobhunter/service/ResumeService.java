package vn.hoangmelinh.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import vn.hoangmelinh.jobhunter.domain.Job;
import vn.hoangmelinh.jobhunter.domain.Resume;
import vn.hoangmelinh.jobhunter.domain.User;
import vn.hoangmelinh.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoangmelinh.jobhunter.domain.response.Job.ResCreateJobDTO;
import vn.hoangmelinh.jobhunter.domain.response.Resume.ResCreateResumeDTO;
import vn.hoangmelinh.jobhunter.domain.response.Resume.ResFetchResumeDTO;
import vn.hoangmelinh.jobhunter.domain.response.Resume.ResUpdateResumeDTO;
import vn.hoangmelinh.jobhunter.repository.JobRepository;
import vn.hoangmelinh.jobhunter.repository.ResumeRepository;
import vn.hoangmelinh.jobhunter.repository.UserRepository;
import vn.hoangmelinh.jobhunter.util.SecurityUtil;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final FilterParser filterParser;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository,
            JobRepository jobRepository, FilterParser filterParser,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.filterParser = filterParser;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    public Optional<Resume> findResumeById(Long id) {
        return this.resumeRepository.findById(id);
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        if (resume.getUser() == null) {
            return false;
        }

        Optional<User> uOptional = this.userRepository.findById(resume.getUser().getId());
        if (uOptional.isEmpty()) {
            return false;
        }

        if (resume.getJob() == null) {
            return false;
        }

        Optional<Job> jOptional = this.jobRepository.findById(resume.getJob().getId());
        if (jOptional.isEmpty()) {
            return false;
        }

        return true;
    }

    public ResCreateResumeDTO handleCreateResume(Resume resume) {

        resume = this.resumeRepository.save(resume);

        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());

        return res;
    }

    public ResUpdateResumeDTO handleUpdateResume(Resume resume) {
        resume = this.resumeRepository.save(resume);

        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        return res;
    }

    public void deleteResume(Long id) {
        this.resumeRepository.deleteById(id);
    }

    public ResFetchResumeDTO handleFetchResume(Resume resume) {
        ResFetchResumeDTO res = new ResFetchResumeDTO();

        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus().toString());
        res.setCreatedAt(resume.getCreatedAt());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedBy(resume.getUpdatedBy());

        if (resume.getJob() != null) {
            res.setCompanyName(resume.getJob().getCompany().getName());
        }

        res.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        res.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));

        return res;
    }

    public ResultPaginationDTO fetchAllResume(Specification<Resume> spec, Pageable page) {

        Page<Resume> userPage = this.resumeRepository.findAll(spec, page);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(userPage.getNumber() + 1);
        mt.setPageSize(userPage.getSize());
        mt.setPages(userPage.getTotalPages());
        mt.setTotal(userPage.getTotalElements());

        resultPaginationDTO.setMeta(mt);

        List<ResFetchResumeDTO> resResumeDTOList = userPage.getContent().stream().map(this::handleFetchResume)
                .collect(Collectors.toList());

        resultPaginationDTO.setResult(resResumeDTOList);

        return resultPaginationDTO;
    }

    public ResultPaginationDTO fetchAllResumeByUser(Pageable pageable) {

        // query builder
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        FilterNode node = filterParser.parse("email = '" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageResume.getNumber() + 1);
        mt.setPageSize(pageResume.getSize());
        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        resultPaginationDTO.setMeta(mt);

        List<ResFetchResumeDTO> resResumeDTOList = pageResume.getContent().stream().map(this::handleFetchResume)
                .collect(Collectors.toList());

        resultPaginationDTO.setResult(resResumeDTOList);

        return resultPaginationDTO;
    }

}
