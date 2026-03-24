package vn.hoangmelinh.jobhunter.service;

import java.lang.StackWalker.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoangmelinh.jobhunter.domain.Skill;
import vn.hoangmelinh.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoangmelinh.jobhunter.repository.SkillRepository;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Optional<Skill> findSkillById(Long id) {
        return this.skillRepository.findById(id);
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public boolean isSkillExist(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill handleUpdateSkill(Long id, Skill skill) {
        Optional<Skill> currentSkill = this.skillRepository.findById(id);
        if (currentSkill.isPresent()) {
            Skill sk = currentSkill.get();
            sk.setName(skill.getName());
            return this.skillRepository.save(sk);
        }
        return null;
    }

    public ResultPaginationDTO fetchAllSkills(Specification<Skill> spec, Pageable page) {

        Page<Skill> userPage = this.skillRepository.findAll(spec, page);
        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(userPage.getNumber() + 1);
        mt.setPageSize(userPage.getSize());
        mt.setPages(userPage.getTotalPages());
        mt.setTotal(userPage.getTotalElements());

        resultPaginationDTO.setMeta(mt);

        List<Skill> resUserDTOList = userPage.getContent();

        resultPaginationDTO.setResult(resUserDTOList);

        return resultPaginationDTO;
    }

    public void deleteSkillById(Long id) {
        // delete job inside job_skill table

        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        // delete subscriber inside subscriber_skill table
        currentSkill.getSubscribers().forEach(subscriber -> subscriber.getSkills().remove(currentSkill));

        this.skillRepository.deleteById(id);
    }
}
