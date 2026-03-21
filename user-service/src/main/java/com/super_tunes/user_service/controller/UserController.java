package com.super_tunes.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.super_tunes.user_service.entity.User;
import com.super_tunes.user_service.service.UserService;

import jakarta.validation.Valid;
import com.super_tunes.user_service.dto.request.UserCreateRequest;
import com.super_tunes.user_service.dto.request.UserUpdateRequest;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
   private final UserService userService;
   
   public UserController(UserService userService){
    this.userService=userService;
   }

   @GetMapping
   public ResponseEntity<List<User>> getAllUsers(){
    return ResponseEntity.ok(userService.getAllUsers());
   }

   @GetMapping("/{id}")
   public ResponseEntity<User> getUserById(@PathVariable Long id){
    return userService.getUserById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
   }

   @GetMapping("/email/{email}")
   public ResponseEntity<User> getUserByEmail(@PathVariable String email){
    return userService.getUserByEmail(email).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
   }

   @PostMapping
   public ResponseEntity<User> createUser(@RequestBody @Valid UserCreateRequest request){
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
   }

   @PutMapping("/{id}")
   public ResponseEntity<User> updateUser(@PathVariable Long id,@RequestBody @Valid UserUpdateRequest request){
    return ResponseEntity.ok(userService.updateUser(id,request));
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<User> deleteUser(@PathVariable Long id){
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
   }
}
