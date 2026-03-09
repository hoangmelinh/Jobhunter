package vn.hoangmelinh.jobhunter.controller;

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

    @PutMapping("/jobs/{id}")
    @ApiMessage("Update a job")
    public ResponseEntity<ResCreateJobDTO> updateJob(@PathVariable Long id, @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.handleUpdateJob(id, job));
    }

    @GetMapping("jobs/{id}")
    @ApiMessage("Find job by id")
    public ResponseEntity<?> findJobById(@PathVariable Long id) throws IdInvalidException {

        Optional<Job> job = this.jobService.findJobById(id);
        if (!job.isPresent()) {
            throw new IdInvalidException("Id " + id + "not found");
        }

        return ResponseEntity.ok(this.jobService.findJobById(id));
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
