package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Profile;
import org.yearup.models.User;
import org.yearup.service.ProfileService;
import org.yearup.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
@CrossOrigin
@PreAuthorize("hasRole('ROLE_USER')")
public class ProfileController {
    ProfileService profileService;
    UserService userService;

    @Autowired
    public ProfileController(ProfileService profileService, UserService userService) {
        this.profileService = profileService;
        this.userService = userService;
    }


    @GetMapping()
    public ResponseEntity<Profile> getProfile(Principal principal){
        String username = principal.getName();
        int userId = userService.getIdByUsername(username);
        Profile profile = profileService.getProfile(userId);
        if (profile == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(profile);
    }


    @PutMapping()
    public ResponseEntity<Profile> updateProfile(Principal principal,@RequestBody Profile profile){

        String username = principal.getName();
        int userId = userService.getIdByUsername(username);
        Profile updatedProfile = profileService.updateProfile(userId,profile);
        if (updatedProfile == null){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(updatedProfile);

    }

}
