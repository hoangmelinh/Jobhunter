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

import jakarta.validation.Valid;
import vn.hoangmelinh.jobhunter.domain.Skill;
import vn.hoangmelinh.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoangmelinh.jobhunter.service.SkillService;
import vn.hoangmelinh.jobhunter.util.annotation.ApiMessage;
import vn.hoangmelinh.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create a new skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {

        if (this.skillService.isSkillExist(skill.getName())) {
            throw new IdInvalidException("Skill with name " + skill.getName() + " already exists");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.handleCreateSkill(skill));
    }

    @PutMapping("/skills/{id}")
    @ApiMessage("Update a skill")
    public ResponseEntity<Skill> updateSkill(@PathVariable Long id, @RequestBody Skill skill)
            throws IdInvalidException {
        Skill updatedSkill = this.skillService.handleUpdateSkill(id, skill);
        return ResponseEntity.ok(updatedSkill);
    }

    @GetMapping("/skills")
    @ApiMessage("Fetch all skills")
    public ResponseEntity<ResultPaginationDTO> fetchAllSkills(@Filter Specification<Skill> spec,
            Pageable pageable) {

        return ResponseEntity.ok(this.skillService.fetchAllSkills(spec, pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) throws IdInvalidException {

        Optional<Skill> skill = this.skillService.findSkillById(id);

        if (skill.isEmpty()) {
            throw new IdInvalidException("Skill với Id " + id + " không tồn tại!");
        }

        this.skillService.deleteSkillById(id);

        return ResponseEntity.ok("delete succsessfully with id " + id + " !");
    }

}