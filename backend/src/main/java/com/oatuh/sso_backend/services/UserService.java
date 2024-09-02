package com.oatuh.sso_backend.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.oatuh.sso_backend.dtos.UserDTO;
import com.oatuh.sso_backend.entitites.User;
import com.oatuh.sso_backend.repositories.UserRepository;





@Service

public class  UserService {


	@Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    
    public String registerAccount(UserDTO userDTO) throws Exception {

 
        System.out.print("sono prima di validate account\n");
        //validate data from client
        validateAccount(userDTO);
        System.out.print("sono dopo di validate account\n");
        User user = insertUser(userDTO);
        String response;
        try {
            userRepository.save(user);
       
            response = "Register account  successfully!";
        }catch (Exception e){
            response = "Service Unavailable";
            //throw new BaseException(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()), "Service Unavailable");
        }
        return response;
    }
    

    
    

    private User insertUser(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setPicture(userDTO.getPicture());
        //user.setProviderId(Provider.local.name());
        user.setProvider(userDTO.getProvider());
        return user;
    }

    private void validateAccount(UserDTO userDTO) throws Exception{
        if(ObjectUtils.isEmpty(userDTO)){
        	System.out.print("user is empty\n");
            throw new Exception();
        }



      Optional<User> user = userRepository.findByEmail(userDTO.getEmail());
      
        if(user.isPresent()){
        	System.out.print("user already exist\n");
            throw new Exception();
        }

    }




	public Optional<User> findByEmail(String email) {
		// TODO Auto-generated method stub
		Optional<User> userToFind = userRepository.findByEmail(email);
		return userToFind;
	}

	
}