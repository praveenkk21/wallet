package com.WalletProject.WalletProject.service;

import com.WalletProject.CommonConstants;
import com.WalletProject.WalletProject.dto.UserRequestDto;
import com.WalletProject.WalletProject.exception.DuplicateEntryException;
import com.WalletProject.WalletProject.model.User;
import com.WalletProject.WalletProject.model.UserType;
import com.WalletProject.WalletProject.repository.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Value("${user.authority}")
    private String userAuthority;

    @Value("${admin.authority}")
    private String adminAuthority;

    @Autowired
    private UserRepo userRepo;

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

        User user=User.builder()
                .authority(userRequestDto.getAuthority())
                .email(userRequestDto.getEmail())
                .contactNo(userRequestDto.getContactNo())
                .name(userRequestDto.getName())
                .userType(UserType.ADMIN)
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .isAccountNonExpired(true).isAccountNonLocked(true).isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();

        User isExistingUser= userRepo.findByEmail(userRequestDto.getEmail());

        JSONObject currentUser=new JSONObject();
        currentUser.put(CommonConstants.CONTACT,user.getContactNo());
        currentUser.put(CommonConstants.EMAIL,user.getEmail());
        currentUser.put(CommonConstants.NAME,user.getName());

                if(isExistingUser!=null){
                    //no need to create wallet
                    currentUser.put(CommonConstants.IS_NEW_USER,"false");
                }
                else{
                    currentUser.put(CommonConstants.IS_NEW_USER,"true");
                }

        user = userRepo.save(user);
        currentUser.put(CommonConstants.USER_ID,user.getId());
        kafkaTemplate.send(CommonConstants.USER_ALTERNAIVE_TOPIC,objectMapper.writeValueAsString(currentUser));

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
        Optional<User> userValue= userRepo.findById(id);
        return userValue.orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }
}
