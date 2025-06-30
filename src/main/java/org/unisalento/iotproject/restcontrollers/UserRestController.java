package org.unisalento.iotproject.restcontrollers;


import org.unisalento.iotproject.domain.User;
import org.unisalento.iotproject.dto.AuthenticationResponseDTO;
import org.unisalento.iotproject.dto.LoginDTO;
import org.unisalento.iotproject.dto.UserDTO;
import org.unisalento.iotproject.dto.UserListDTO;
import org.unisalento.iotproject.exceptions.UserNotFoundException;
import org.unisalento.iotproject.repositories.UserRepository;
import org.unisalento.iotproject.security.JwtUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserRestController {

    @Autowired
    private MqttConfig mqttConfig;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtilities jwtUtilities;


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public UserDTO get(@PathVariable String id, @RequestHeader("Authorization") String token) throws UserNotFoundException {

        token = token.substring(7);


        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.get().getId());
        userDTO.setNome(user.get().getNome());
        userDTO.setCognome(user.get().getCognome());
        userDTO.setEmail(user.get().getEmail());
        userDTO.setAddress(user.get().getAddress());
        userDTO.setBirthdate(user.get().getBirthdate());
        userDTO.setPassword(user.get().getPassword());
        userDTO.setTelephoneNumber(user.get().getTelephoneNumber());
        userDTO.setCity(user.get().getCity());
        userDTO.setSex(user.get().getSex());
        userDTO.setRole(user.get().getRole());
        userDTO.setCaloriesThreshold(user.get().getCaloriesThreshold());
        userDTO.setLinkedUserId(user.get().getLinkedUserId());

        return userDTO;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public UserListDTO getAll() {

        UserListDTO usersList = new UserListDTO();
        ArrayList<UserDTO> list = new ArrayList<>();
        usersList.setList(list);

        List<User> users = userRepository.findAll();

        for (User user : users) {
            UserDTO userDTO = new UserDTO();

            userDTO.setId(user.getId());
            userDTO.setNome(user.getNome());
            userDTO.setCognome(user.getCognome());
            userDTO.setEmail(user.getEmail());
            userDTO.setAddress(user.getAddress());
            userDTO.setBirthdate(user.getBirthdate());
            userDTO.setPassword(null);
            userDTO.setTelephoneNumber(user.getTelephoneNumber());
            userDTO.setCity(user.getCity());
            userDTO.setSex(user.getSex());
            userDTO.setRole(user.getRole());
            userDTO.setCaloriesThreshold(user.getCaloriesThreshold());
            userDTO.setLinkedUserId(user.getLinkedUserId());

            list.add(userDTO);
        }

        return usersList;

    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody UserDTO userDTO) {

        Optional<User> existingUserOptional = userRepository.findById(id);

        if (!existingUserOptional.isPresent()) {

            return ResponseEntity.notFound().build();
        }

        User existingUser = existingUserOptional.get();

        existingUser.setNome(userDTO.getNome());
        existingUser.setCognome(userDTO.getCognome());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setCity(userDTO.getCity());
        existingUser.setTelephoneNumber(userDTO.getTelephoneNumber());
        existingUser.setBirthdate(userDTO.getBirthdate());
        existingUser.setSex(userDTO.getSex());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setCaloriesThreshold(userDTO.getCaloriesThreshold());
        existingUser.setLinkedUserId(userDTO.getLinkedUserId());


        User updatedUser = userRepository.save(existingUser);

        UserDTO updatedUserDTO = new UserDTO();
        updatedUserDTO.setId(updatedUser.getId());
        updatedUserDTO.setNome(updatedUser.getNome());
        updatedUserDTO.setCognome(updatedUser.getCognome());
        updatedUserDTO.setEmail(updatedUser.getEmail());
        updatedUserDTO.setRole(updatedUser.getRole());
        updatedUserDTO.setCity(updatedUser.getCity());
        updatedUserDTO.setTelephoneNumber(updatedUser.getTelephoneNumber());
        updatedUserDTO.setBirthdate(updatedUser.getBirthdate());
        updatedUserDTO.setSex(updatedUser.getSex());
        updatedUserDTO.setAddress(updatedUser.getAddress());
        updatedUserDTO.setCaloriesThreshold(updatedUser.getCaloriesThreshold());

        return ResponseEntity.ok(updatedUserDTO);
    }

    @RequestMapping(value = "/linkedUser/{idDoctor}/{idCaregiver}", method = RequestMethod.PUT)
    public ResponseEntity<UserDTO> linkedUser(@PathVariable String idCaregiver, @PathVariable String idDoctor) {

        Optional<User> caregiverOptional = userRepository.findById(idCaregiver);
        if (!caregiverOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Optional<User> doctorOptional = userRepository.findById(idDoctor);
        if (!doctorOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User caregiver = caregiverOptional.get();
        User doctor = doctorOptional.get();

        caregiver.setLinkedUserId(doctor.getId());
        doctor.setLinkedUserId(caregiver.getId());

        userRepository.save(caregiver);
        userRepository.save(doctor);

        UserDTO updatedCaregiverDTO = new UserDTO();
        updatedCaregiverDTO.setId(caregiver.getId());
        updatedCaregiverDTO.setNome(caregiver.getNome());
        updatedCaregiverDTO.setCognome(caregiver.getCognome());
        updatedCaregiverDTO.setEmail(caregiver.getEmail());
        updatedCaregiverDTO.setAddress(caregiver.getAddress());
        updatedCaregiverDTO.setBirthdate(caregiver.getBirthdate());
        updatedCaregiverDTO.setPassword(null);
        updatedCaregiverDTO.setTelephoneNumber(caregiver.getTelephoneNumber());
        updatedCaregiverDTO.setCity(caregiver.getCity());
        updatedCaregiverDTO.setSex(caregiver.getSex());
        updatedCaregiverDTO.setRole(caregiver.getRole());
        updatedCaregiverDTO.setCaloriesThreshold(caregiver.getCaloriesThreshold());
        updatedCaregiverDTO.setLinkedUserId(caregiver.getLinkedUserId());

        return ResponseEntity.ok(updatedCaregiverDTO);
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String id) throws UserNotFoundException {
        Optional<User> existingUserOptional = userRepository.findById(id);
        if (!existingUserOptional.isPresent()) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(id);
    }


    @RequestMapping(value = "/searchByEmail", method = RequestMethod.GET)
    public ResponseEntity<UserDTO> searchByEmail(@RequestParam String email) {

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (!userOptional.isPresent()) {
            return ResponseEntity.status(403).body(null);
        }

        User user = userOptional.get();
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setNome(user.getNome());
        userDTO.setCognome(user.getCognome());
        userDTO.setEmail(user.getEmail());
        userDTO.setAddress(user.getAddress());
        userDTO.setBirthdate(user.getBirthdate());
        userDTO.setTelephoneNumber(user.getTelephoneNumber());
        userDTO.setCity(user.getCity());
        userDTO.setSex(user.getSex());
        userDTO.setRole(user.getRole());
        userDTO.setCaloriesThreshold(user.getCaloriesThreshold());
        userDTO.setLinkedUserId(user.getLinkedUserId());

        return ResponseEntity.ok(userDTO);
    }


    @RequestMapping(value = "/authenticate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginDTO loginDTO) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getEmail(),
                        loginDTO.getPassword()
                )
        );
        Optional<User> userOptional = userRepository.findByEmail(authentication.getName());

        if(userOptional.isEmpty()) {
            throw new UsernameNotFoundException(loginDTO.getEmail());
        }

        User user = userOptional.get();

        if (user.getRole().equals(User.DOCTOR)) {
            mqttConfig.subscribeToTopic("food/deficit");
            mqttConfig.subscribeToTopic("activity/step");
            mqttConfig.subscribeToTopic("heartRate/check");
            mqttConfig.subscribeToTopic("weight/check");
            mqttConfig.subscribeToTopic("sleep/check");
            mqttConfig.subscribeToTopic("spo2/check");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String jwt = jwtUtilities.generateToken(user.getEmail(), user.getId(), user.getRole());
        return ResponseEntity.ok(new AuthenticationResponseDTO(jwt));

    }


}