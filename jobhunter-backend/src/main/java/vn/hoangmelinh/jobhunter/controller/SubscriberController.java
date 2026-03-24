package vn.hoangmelinh.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoangmelinh.jobhunter.domain.Subscriber;
import vn.hoangmelinh.jobhunter.service.SubscriberService;
import vn.hoangmelinh.jobhunter.util.SecurityUtil;
import vn.hoangmelinh.jobhunter.util.annotation.ApiMessage;
import vn.hoangmelinh.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create subscriber")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber)
            throws IdInvalidException {

        // check email
        boolean checkEmail = subscriberService.existsByEmail(subscriber.getEmail());
        if (checkEmail == true) {
            throw new IdInvalidException("Email " + subscriber.getEmail() + " already exists");
        }
        return ResponseEntity.ok(subscriberService.save(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update subscriber")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriber) throws IdInvalidException {
        Subscriber subscriDB = subscriberService.findById(subscriber.getId());
        if (subscriber == null) {
            throw new IdInvalidException("Subscriber " + subscriber.getId() + " not found");
        }
        return ResponseEntity.ok(subscriberService.update(subscriDB, subscriber));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscriberSkills() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        return ResponseEntity.ok(this.subscriberService.findByEmail(email));
    }

}
