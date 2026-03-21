package com.super_tunes.user_service.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.super_tunes.user_service.dto.request.UserCreateRequest;
import com.super_tunes.user_service.dto.request.UserUpdateRequest;
import com.super_tunes.user_service.entity.User;
import com.super_tunes.user_service.repository.UserRepository;
import com.super_tunes.user_service.dto.response.UserResponse;

import java.util.List;
import java.util.Optional;


@Service
public class UserService {

    private UserResponse toUserResponse(User user){
        return new UserResponse(user.getId(),user.getUsername(),user.getEmail(),user.getCreatedAt(),user.getUpdatedAt());
    }
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<UserResponse> getUserResponseByEmail(String email){
        return userRepository.findByEmail(email).map(this::toUserResponse);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsersResponses(){
        return userRepository.findAll().stream().map(this::toUserResponse).toList();
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserResponseById(Long id){
        return userRepository.findById(id).map(this::toUserResponse);
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request){
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
        userRepository.save(user);
        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request){
        User usr=userRepository.findById(id).map(user -> {
            user.setUsername(request.username());
            user.setEmail(request.email());
            user.setPassword(request.password());
            return user;
        }).orElseThrow(()->new RuntimeException("User Not Found with id: "+id));
        return toUserResponse(usr);
    }

    @Transactional
    public void deleteUser(Long id){
        if (!userRepository.existsById(id)){
            throw new RuntimeException("User Not found with Id: "+id);
        }
        userRepository.deleteById(id);
    }
}
