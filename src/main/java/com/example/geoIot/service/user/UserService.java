package com.example.geoIot.service.user;

import com.example.geoIot.entity.User;
import com.example.geoIot.entity.dto.UserDto;
import com.example.geoIot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
                return userRepository.findByUserEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));
            }
        };
    }

    public UserDto getUserById(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NoSuchElementException();
        }
        return convertUserToDto(user.get());
    }

    public UserDto getUserByEmail(String email) {
        Optional<User> user = userRepository.findByUserEmail(email);
        if (user.isEmpty()) {
            throw new NoSuchElementException(email);
        }
        return convertUserToDto(user.get());
    }

    public List<UserDto> getAllUsers() {
        List<User> userList = userRepository.findAll();
        if (userList.isEmpty()) {
            throw new NoSuchElementException("No users found");
        }
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList) {
            userDtoList.add(convertUserToDto(user));
        }
        return userDtoList;
    }

    public UserDto updateUser(UserDto userDto) {
        Optional<User> user = userRepository.findById(userDto.getId());
        if (user.isEmpty()) {
            throw new NoSuchElementException("User not found");
        }
        User userToUpdate = user.get();
        userToUpdate.setUserEmail(userDto.getEmail());
        userToUpdate.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userToUpdate.setRole(userDto.getRole());
        return convertUserToDto(userRepository.save(userToUpdate));
    }

    public UserDto createUser(UserDto userDto) {
        User user = User.builder()
                .userEmail(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(userDto.getRole())
                .build();
        user = userRepository.save(user);
        return convertUserToDto(user);
    }

    public UserDto deleteUser(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new NoSuchElementException("User not found");
        }
        userRepository.deleteById(id);
        return convertUserToDto(user.get());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUserEmail(email);
        if (user.isEmpty()) {
            throw new NoSuchElementException();
        }
        return user.get();
    }

    private UserDto convertUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getUserEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }
}
