package com.WalletProject.WalletProject.service;

import com.WalletProject.CommonConstants;
import com.WalletProject.WalletProject.dto.UserRequestDto;
import com.WalletProject.WalletProject.exception.DuplicateEntryException;
import com.WalletProject.WalletProject.model.User;
import com.WalletProject.WalletProject.model.UserProfileImage;
import com.WalletProject.WalletProject.model.UserType;
import com.WalletProject.WalletProject.repository.UserProfileImageRepo;
import com.WalletProject.WalletProject.repository.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    @Value("${user.authority}")
    private String userAuthority;

    @Value("${admin.authority}")
    private String adminAuthority;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserProfileImageRepo userProfileImageRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public User addUpdateUser(UserRequestDto userRequestDto) throws JsonProcessingException {
        Map<String, String> errors = new HashMap<>();

        // Check if email already exists
        if (userRepo.findByEmail(userRequestDto.getEmail()) != null) {
            errors.put("email", "Email already exists. Please use a different one.");
        }

        // Check if contactNo already exists
        if (userRepo.findByContactNo(userRequestDto.getContactNo()) != null) {
            errors.put("contactNo", "Contact number already exists. Please use a different one.");
        }

        // If there are errors, return them
        if (!errors.isEmpty()) {
            throw new DuplicateEntryException(errors);
        }

        User user = User.builder()
                .authority(userRequestDto.getAuthority())
                .email(userRequestDto.getEmail())
                .contactNo(userRequestDto.getContactNo())
                .name(userRequestDto.getName())
                .userType(UserType.ADMIN)
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .isAccountNonExpired(true).isAccountNonLocked(true).isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();

        User isExistingUser = userRepo.findByEmail(userRequestDto.getEmail());

        JSONObject currentUser = new JSONObject();
        currentUser.put(CommonConstants.CONTACT, user.getContactNo());
        currentUser.put(CommonConstants.EMAIL, user.getEmail());
        currentUser.put(CommonConstants.NAME, user.getName());

        if (isExistingUser != null) {
            //no need to create wallet
            currentUser.put(CommonConstants.IS_NEW_USER, "false");
        } else {
            currentUser.put(CommonConstants.IS_NEW_USER, "true");
        }

        user = userRepo.save(user);
        currentUser.put(CommonConstants.USER_ID, user.getId());
        kafkaTemplate.send(CommonConstants.USER_ALTERNAIVE_TOPIC, objectMapper.writeValueAsString(currentUser));

        return user;
    }


    public UserDetails loadUserByContactNo(String contactNo) throws UsernameNotFoundException {
        return userRepo.findByContactNo(contactNo);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmail(username);

    }

    public User getUser(Integer id) {
        Optional<User> userValue = userRepo.findById(id);
        return userValue.orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User updateUser(Integer id, @Valid UserRequestDto userRequestDto) {
        Map<String, String> errors = new HashMap<>();

        // Check if email already exists
        if (userRepo.findByEmail(userRequestDto.getEmail()) != null) {
            errors.put("email", "Email already exists. Please use a different one.");
        }

        // Check if contactNo already exists
        if (userRepo.findByContactNo(userRequestDto.getContactNo()) != null) {
            errors.put("contactNo", "Contact number already exists. Please use a different one.");
        }

        // If there are errors, return them
        if (!errors.isEmpty()) {
            throw new DuplicateEntryException(errors);
        }
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (userRequestDto.getContactNo() == null || userRequestDto.getContactNo().isEmpty()) {
            // If contactNo is not provided, use the existing contactNo from the user object
            userRequestDto.setContactNo(user.getContactNo());
        }

        user.setContactNo(userRequestDto.getContactNo());

        if (userRequestDto.getEmail() == null || userRequestDto.getEmail().isEmpty()) {
            // If contactNo is not provided, use the existing contactNo from the user object
            userRequestDto.setEmail(user.getEmail());
        }
        user.setEmail(userRequestDto.getEmail());

        if (userRequestDto.getName() == null || userRequestDto.getName().isEmpty()) {
            // If contactNo is not provided, use the existing contactNo from the user object
            userRequestDto.setName(user.getName());
        }
        user.setName(userRequestDto.getName());
        //user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

        if (userRequestDto.getAuthority() == null || userRequestDto.getAuthority().isEmpty()) {
            // If contactNo is not provided, use the existing contactNo from the user object
            userRequestDto.setAuthority(user.getAuthority());
        }

        user.setAuthority(userRequestDto.getAuthority());

        user = userRepo.save(user);
        return user;
    }

    public boolean saveUserProfileImage(Long id, MultipartFile file) {
        try {
            // Convert the MultipartFile to a BLOB
            byte[] imageBytes = file.getBytes();
            SerialBlob imageBlob = new SerialBlob(imageBytes);

            UserProfileImage userProfileImage = UserProfileImage.builder()
                    .userId(id)
                    .image(imageBlob)
                    .contentType(file.getContentType())
                    .build();

            if (userProfileImageRepo.existsByUserId(id))
                userProfileImageRepo.deleteByUserId(id);

            // Save the image in the database
            userProfileImageRepo.save(userProfileImage);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getUserProfileImage(Long userId) {
        Optional<UserProfileImage> userProfileImageOptional = userProfileImageRepo.findByUserId(userId);

        if (userProfileImageOptional.isPresent()) {
            UserProfileImage userProfileImage = userProfileImageOptional.get();
            Blob imageBlob = userProfileImage.getImage();

            try {
                // Return the image as an InputStream
                return imageBlob.getBinaryStream();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error reading the image from the database.");
            }
        } else {
            throw new RuntimeException("Image not found for user ID: " + userId);
        }
    }

    public void deleteUser(int id) {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepo.delete(user);
        if (userProfileImageRepo.existsByUserId((long) id)) {
            userProfileImageRepo.deleteByUserId((long) id);
        }
    }
}
