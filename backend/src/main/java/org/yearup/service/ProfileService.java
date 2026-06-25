package org.yearup.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Profile;
import org.yearup.models.User;
import org.yearup.repository.ProfileRepository;

import java.util.List;

@Service
public class ProfileService
{
    private final ProfileRepository profileRepository;


    public ProfileService(ProfileRepository profileRepository)
    {
        this.profileRepository = profileRepository;
    }

    public Profile create(Profile profile)
    {
        return profileRepository.save(profile);
    }

    public Profile getProfile(int userId){
        return profileRepository.findById(userId).orElse(null);
    }

    public Profile updateProfile(int userId, Profile profile){
        Profile currentProfile = profileRepository.findById(userId).orElse(null);
        if (currentProfile == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        currentProfile.setAddress(profile.getAddress());
        currentProfile.setCity(profile.getCity());
        currentProfile.setEmail(profile.getEmail());
        currentProfile.setPhone(profile.getPhone());
        currentProfile.setFirstName(profile.getFirstName());
        currentProfile.setLastName(profile.getLastName());
        currentProfile.setState(profile.getState());
        currentProfile.setZip(profile.getZip());
        profileRepository.save(currentProfile);
        return currentProfile;
    }
}
