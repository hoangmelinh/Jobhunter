package vn.hoangmelinh.jobhunter.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import vn.hoangmelinh.jobhunter.domain.Job;
import vn.hoangmelinh.jobhunter.domain.Skill;
import vn.hoangmelinh.jobhunter.domain.Subscriber;
import vn.hoangmelinh.jobhunter.domain.response.email.RestEmailJob;
import vn.hoangmelinh.jobhunter.repository.JobRepository;
import vn.hoangmelinh.jobhunter.repository.SkillRepository;
import vn.hoangmelinh.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
            JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public boolean existsByEmail(String email) {
        return subscriberRepository.existsByEmail(email);
    }

    public Subscriber save(Subscriber subscriber) {
        if (subscriber.getSkills() != null) {
            List<Long> skillIds = subscriber.getSkills().stream().map(Skill::getId).collect(Collectors.toList());
            List<Skill> skills = skillRepository.findAllById(skillIds);
            subscriber.setSkills(skills);
        }
        return subscriberRepository.save(subscriber);
    }

    public Subscriber findById(Long id) {
        return subscriberRepository.findById(id).orElse(null);
    }

    public Subscriber update(Subscriber subscriDB, Subscriber subscriber) {

        // check skills

        if (subscriber.getSkills() != null) {
            List<Long> skillIds = subscriber.getSkills().stream().map(Skill::getId).collect(Collectors.toList());
            List<Skill> skills = skillRepository.findAllById(skillIds);
            subscriber.setSkills(skills);
        }
        return this.subscriberRepository.save(subscriber);
    }

    public RestEmailJob convertJobToSendEmail(Job job) {
        RestEmailJob restEmailJob = new RestEmailJob();
        restEmailJob.setName(job.getName());
        restEmailJob.setSalary(job.getSalary());
        restEmailJob.setCompany(new RestEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<RestEmailJob.SkillEmail> skillEmails = new ArrayList<>();
        if (skills != null && skills.size() > 0) {
            for (Skill skill : skills) {
                skillEmails.add(new RestEmailJob.SkillEmail(skill.getName()));
            }
        }
        restEmailJob.setSkills(skillEmails);
        return restEmailJob;
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {

                        List<RestEmailJob> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());

                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                listJobs);
                    }
                }
            }
        }
    }

    public Subscriber findByEmail(String email) {
        return this.subscriberRepository.findByEmail(email).orElse(null);
    }

}
