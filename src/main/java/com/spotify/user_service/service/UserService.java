package com.spotify.user_service.service;

import com.spotify.user_service.entity.User;
import com.spotify.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

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
    public User createUser(User user){
        if (userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email Already exists: "+user.getEmail());
        }
        if (userRepository.existsById(user.getId())){
            throw new RuntimeException("User Name Already Exists: "+user.getId());
        }
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, User updatedUser){
        return userRepository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
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
