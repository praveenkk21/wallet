package com.WalletProject.WalletProject.controller;

import com.WalletProject.WalletProject.dto.UpdateUser;
import com.WalletProject.WalletProject.service.UserService;
import com.WalletProject.WalletProject.dto.UserRequestDto;
import com.WalletProject.WalletProject.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
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

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<User> updateUser(@Validated(UpdateUser.class) @RequestBody UserRequestDto userRequestDto, @PathVariable int id){
        User existingUser = userService.getUser(id);
        if(existingUser == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        User updatedUser = userService.updateUser(id,userRequestDto);
        return new ResponseEntity<>(updatedUser,HttpStatus.OK);
    }

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> users = userService.getAllUsers();
        if(users.isEmpty()){
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/uploadImage/{id}")
    public ResponseEntity<String> uploadUserImage(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) {

        boolean success = userService.saveUserProfileImage(id, file);

        if (success) {
            return ResponseEntity.ok("Image uploaded successfully.");
        } else {
            return ResponseEntity.status(500).body("Failed to upload image.");
        }
    }

    @GetMapping("/getUserImage/{id}")
    public ResponseEntity<InputStreamResource> getUserProfilePicture(@PathVariable("id") Long id) {
        // Retrieve the InputStream for the image from the service (database)
        InputStream imageInputStream = userService.getUserProfileImage(id);

        // Create a response entity with the image input stream and headers
        if (imageInputStream == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)  // Or the image type based on the file
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=userProfile.jpg")  // Adjust filename if needed
                    .body(new InputStreamResource(imageInputStream));
        }
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id){
        User user = userService.getUser(id);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        userService.deleteUser(id);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }
}
