package com.WalletProject.WalletProject.controller;

import com.WalletProject.WalletProject.service.UserService;
import com.WalletProject.WalletProject.dto.UserRequestDto;
import com.WalletProject.WalletProject.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/addUpdate")
    public ResponseEntity<User> addUpdateUser(@Valid @RequestBody UserRequestDto userRequestDto) throws JsonProcessingException {
        User updatedUser=userService.addUpdateUser(userRequestDto);
        if(updatedUser == null ){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return  new ResponseEntity<>(updatedUser,HttpStatus.OK);
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id){
        User user = userService.getUser(id);
        if(user == null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(user,HttpStatus.OK);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        if(users.isEmpty()){
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
