package org.unisalento.iotproject.restcontrollers;

import org.unisalento.iotproject.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.unisalento.iotproject.dto.UserDTO;
import org.unisalento.iotproject.repositories.UserRepository;

import static org.unisalento.iotproject.configuration.SecurityConfig.passwordEncoder;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/doctor",method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO postDoctor(@RequestBody UserDTO userDTO) {

        User user = new User();
        userDTO.setRole(User.DOCTOR);

        user.setNome(userDTO.getNome());
        user.setCognome(userDTO.getCognome());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder().encode(userDTO.getPassword()));
        user.setAddress(userDTO.getAddress());
        user.setTelephoneNumber(userDTO.getTelephoneNumber());
        user.setBirthdate(userDTO.getBirthdate());
        user.setCity(userDTO.getCity());
        user.setSex(userDTO.getSex());
        user.setRole(userDTO.getRole());

        user = userRepository.save(user);
        userDTO.setId(user.getId());
        userDTO.setPassword(null);

        return userDTO;
    }

    @RequestMapping(value = "/caregiver",method=RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO postCareiver(@RequestBody UserDTO userDTO) {

        User user = new User();
        userDTO.setRole(User.CAREGIVER);

        user.setNome(userDTO.getNome());
        user.setCognome(userDTO.getCognome());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder().encode(userDTO.getPassword()));
        user.setAddress(userDTO.getAddress());
        user.setTelephoneNumber(userDTO.getTelephoneNumber());
        user.setBirthdate(userDTO.getBirthdate());
        user.setCity(userDTO.getCity());
        user.setSex(userDTO.getSex());
        user.setRole(userDTO.getRole());

        user = userRepository.save(user);
        userDTO.setId(user.getId());
        userDTO.setPassword(null);

        return userDTO;
    }

}
