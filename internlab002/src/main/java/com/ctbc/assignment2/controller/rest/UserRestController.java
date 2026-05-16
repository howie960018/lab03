package com.ctbc.assignment2.controller.rest;

import com.ctbc.assignment2.service.AppUserService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserRestController {

    private final AppUserService service;

    public UserRestController(AppUserService service) {
        this.service = service;
    }

    // GET /api/users?role=INSTRUCTOR
    @GetMapping
    public List<String> getUsersByRole(@RequestParam String role) {
        return service.findUsernamesByRole(role);
    }
}