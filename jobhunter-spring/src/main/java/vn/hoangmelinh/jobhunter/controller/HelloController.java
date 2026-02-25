package vn.hoangmelinh.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoangmelinh.jobhunter.util.error.IdInvalidException;

@RestController
public class HelloController {

    @GetMapping("/")
    public String getHelloWorld() throws IdInvalidException {

        if(true) {
            throw new IdInvalidException("Lỗi rồi");
        }
        return "Hello World (HoangMeLinh)";
    }
}
