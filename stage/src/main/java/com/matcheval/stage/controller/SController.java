package com.matcheval.stage.controller;

import com.matcheval.stage.model.Users;
import com.matcheval.stage.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SController {
@Autowired
UserRepo userRepo;

    @GetMapping("/students")
    public List<Users> getStudents() {
        return userRepo.findAll();
    }



//    @PostMapping("/students")
//    public Users addStudent(@RequestBody Users student) {
//        userRepo.add(student);
//        return student;
//    }

}
