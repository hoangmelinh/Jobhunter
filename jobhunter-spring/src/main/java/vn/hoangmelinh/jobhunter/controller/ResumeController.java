package vn.hoangmelinh.jobhunter.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;

import vn.hoangmelinh.jobhunter.domain.Company;
import vn.hoangmelinh.jobhunter.domain.Job;
import vn.hoangmelinh.jobhunter.domain.Resume;
import vn.hoangmelinh.jobhunter.domain.User;
import vn.hoangmelinh.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoangmelinh.jobhunter.domain.response.Resume.ResCreateResumeDTO;
import vn.hoangmelinh.jobhunter.domain.response.Resume.ResFetchResumeDTO;
import vn.hoangmelinh.jobhunter.domain.response.Resume.ResUpdateResumeDTO;
import vn.hoangmelinh.jobhunter.service.ResumeService;
import vn.hoangmelinh.jobhunter.service.UserService;
import vn.hoangmelinh.jobhunter.util.SecurityUtil;
import vn.hoangmelinh.jobhunter.util.annotation.ApiMessage;
import vn.hoangmelinh.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;

    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService, UserService userService, FilterBuilder filterBuilder,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume)
            throws IdInvalidException {

        boolean isIdeExist = this.resumeService.checkResumeExistByUserAndJob(resume);

        if (!isIdeExist) {
            throw new IdInvalidException("user id/Job id not found");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.handleCreateResume(resume));
    }

    @PutMapping("/resumes/{id}")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@PathVariable Long id, @Valid @RequestBody Resume resume)
            throws IdInvalidException {

        Optional<Resume> reqResumeOptional = this.resumeService.findResumeById(id);

        if (reqResumeOptional.isEmpty()) {
            throw new IdInvalidException("Resume with id " + id + " not found");
        }

        Resume reqResume = reqResumeOptional.get();

        reqResume.setStatus(resume.getStatus());

        return ResponseEntity.ok(this.resumeService.handleUpdateResume(reqResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> deleteResume(@PathVariable Long id) throws IdInvalidException {
        Optional<Resume> reqResumeOptional = this.resumeService.findResumeById(id);

        if (reqResumeOptional.isEmpty()) {
            throw new IdInvalidException("Resume with id " + id + " not found");
        }

        this.resumeService.deleteResume(id);

        return ResponseEntity.ok(null);
    }

    @GetMapping("resumes/{id}")
    @ApiMessage("fetch a resume")
    public ResponseEntity<ResFetchResumeDTO> fetcgById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> reqResumeOptional = this.resumeService.findResumeById(id);

        if (reqResumeOptional.isEmpty()) {
            throw new IdInvalidException("Resume with id " + id + " not found");
        }

        return ResponseEntity.ok(this.resumeService.handleFetchResume(reqResumeOptional.get()));
    }

    @GetMapping("resumes")
    @ApiMessage("Fetch All Resume")
    public ResponseEntity<ResultPaginationDTO> fetchAllJob(@Filter Specification<Resume> spec, Pageable pageable) {
        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> finalSpec = jobInSpec.and(spec);
        return ResponseEntity.ok(this.resumeService.fetchAllResume(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchAllResumeByUser(Pageable pageable) {
        return ResponseEntity.ok(this.resumeService.fetchAllResumeByUser(pageable));
    }
}
