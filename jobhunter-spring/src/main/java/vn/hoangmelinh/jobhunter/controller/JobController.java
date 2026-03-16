package vn.hoangmelinh.jobhunter.controller;

import java.lang.StackWalker.Option;
import java.util.Optional;

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

import vn.hoangmelinh.jobhunter.domain.Job;
import vn.hoangmelinh.jobhunter.domain.Skill;
import vn.hoangmelinh.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoangmelinh.jobhunter.domain.response.Job.ResCreateJobDTO;
import vn.hoangmelinh.jobhunter.domain.response.Job.ResFetchJobDTO;
import vn.hoangmelinh.jobhunter.service.JobService;
import vn.hoangmelinh.jobhunter.util.annotation.ApiMessage;
import vn.hoangmelinh.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> createJob(@RequestBody Job job) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleCreateJob(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResCreateJobDTO> updateJob(@RequestBody Job job)
            throws IdInvalidException {

        Optional<Job> currentJob = this.jobService.findJobById(job.getId());
        if (!currentJob.isPresent()) {
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.handleUpdateJob(job, currentJob.get()));
    }

    @GetMapping("jobs/{id}")
    @ApiMessage("Find job by id")
    public ResponseEntity<?> findJobById(@PathVariable Long id) throws IdInvalidException {

        Optional<Job> job = this.jobService.findJobById(id);
        if (!job.isPresent()) {
            throw new IdInvalidException("Id " + id + "not found");
        }

        Job currentJob = job.get();
        ResFetchJobDTO res = new ResFetchJobDTO();
        res.setId(currentJob.getId());
        res.setName(currentJob.getName());
        res.setLocation(currentJob.getLocation());
        res.setSalary(currentJob.getSalary());
        res.setQuantity(currentJob.getQuantity());
        res.setLevel(currentJob.getLevel());
        res.setDescription(currentJob.getDescription() != null ? currentJob.getDescription() : "");
        res.setStartDate(currentJob.getStartDate());
        res.setEndDate(currentJob.getEndDate());
        res.setActive(currentJob.isActive());
        res.setCreatedAt(currentJob.getCreatedAt());
        res.setUpdatedAt(currentJob.getUpdatedAt());
        res.setCreatedBy(currentJob.getCreatedBy());
        res.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            java.util.List<String> skills = currentJob.getSkills()
                    .stream().map(Skill::getName)
                    .collect(java.util.stream.Collectors.toList());
            res.setSkills(skills);
        }

        if (currentJob.getCompany() != null) {
            ResFetchJobDTO.CompanyJob companyJob = new ResFetchJobDTO.CompanyJob();
            companyJob.setId(currentJob.getCompany().getId());
            companyJob.setName(currentJob.getCompany().getName());
            companyJob.setLogo(currentJob.getCompany().getLogo());
            res.setCompany(companyJob);
        }

        return ResponseEntity.ok(res);
    }

    @GetMapping("jobs")
    @ApiMessage("Fetch All Job")
    public ResponseEntity<ResultPaginationDTO> fetchAllJob(@Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.ok(this.jobService.fetchAll(spec, pageable));
    }

    @DeleteMapping("jobs/{id}")
    @ApiMessage("Delete Job by Id")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) throws IdInvalidException {

        Optional<Job> job = this.jobService.findJobById(id);
        if (job == null) {
            throw new IdInvalidException("Id " + id + "not found!");
        }
        this.jobService.deleteJob(id);

        return ResponseEntity.ok("delete success!");
    }
}
