package com.ctbc.assignment2.controller.rest;

import com.ctbc.assignment2.bean.AppUser;
import com.ctbc.assignment2.controller.rest.dto.ProfileDto;
import com.ctbc.assignment2.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/profile")
public class ProfileRestController {

    @Autowired
    private AppUserService userService;

    @GetMapping
    public ProfileDto getProfile(Principal principal) {
        AppUser user = userService.findByUsername(principal.getName());
        ProfileDto dto = new ProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        return dto;
    }
}
