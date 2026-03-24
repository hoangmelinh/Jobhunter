package vn.hoangmelinh.jobhunter.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import vn.hoangmelinh.jobhunter.service.EmailService;
import vn.hoangmelinh.jobhunter.service.SubscriberService;
import vn.hoangmelinh.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("Email sent successfully")
    @Scheduled(cron = "*/10 * * * * *")
    @Transactional
    public String sendEmail() {

        System.out.println("Send email");
        // this.emailService.sendEmail();
        // this.emailService.sendEmailSync("hoangk91234@gmail.com", "Test send email",
        // "<h1> <b> Hello HoangMeLinh</b> </h1>", false, true);
        this.subscriberService.sendSubscribersEmailJobs();
        return "Email sent successfully";
    }
}
