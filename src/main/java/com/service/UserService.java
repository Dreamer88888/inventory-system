package com.service;

import com.dto.CredentialDto;
import com.entity.User;
import com.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User validateCredential(CredentialDto credentialDto) {
        Optional<User> user = userRepository.findByUsername(credentialDto.getUsername());

        if (user.isPresent()) {
            if (user.get().getPassword().equals(credentialDto.getPassword())) {
                return user.get();
            }
        }
        log.error("User tidak ditemukan");

        return null;
    }

}
