package com.super_tunes.user_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.super_tunes.user_service.dto.request.UserCreateRequest;
import com.super_tunes.user_service.dto.request.UserUpdateRequest;
import com.super_tunes.user_service.entity.User;
import com.super_tunes.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }

    @Transactional
    public User createUser(UserCreateRequest request){
        if (userRepository.existsByEmail(request.email())){
            throw new RuntimeException("Email Already exists: "+request.email());
        }
        if (userRepository.existsByUsername(request.username())){
            throw new RuntimeException("User Name Already Exists: "+request.username());
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(request.password());
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, UserUpdateRequest request){
        return userRepository.findById(id).map(user -> {
            user.setUsername(request.username());
            user.setEmail(request.email());
            user.setPassword(request.password());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User Not Found with id: "+id));
    }

    @Transactional
    public void deleteUser(Long id){
        if (!userRepository.existsById(id)){
            throw new RuntimeException("User Not found with Id: "+id);
        }
        userRepository.deleteById(id);
    }
}
