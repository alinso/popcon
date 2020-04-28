package com.alinso.popcon.controller;

import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.dto.user.ChangePasswordDto;
import com.alinso.popcon.entity.dto.user.ProfileDto;
import com.alinso.popcon.entity.dto.user.ProfileInfoForUpdateDto;
import com.alinso.popcon.entity.dto.photo.PhotoFormDto;
import com.alinso.popcon.security.JwtTokenProvider;
import com.alinso.popcon.security.SecurityConstants;
import com.alinso.popcon.security.payload.JWTLoginSucessReponse;
import com.alinso.popcon.security.payload.LoginRequest;
import com.alinso.popcon.service.UserService;
import com.alinso.popcon.util.MapValidationErrorUtil;
import com.alinso.popcon.validator.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {


    @Autowired
    UserService userService;

    @Autowired
    ChangePasswordValidator changePasswordValidator;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserUpdateValidator userUpdateValidator;

    @Autowired
    UserValidator userValidator;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    MapValidationErrorUtil mapValidationErrorUtil;

    @Autowired
    LoginValidator loginValidator;

    @Autowired
    ProfilePicValidator profilePicValidator;

    @GetMapping("test")
    public ResponseEntity<?> maleCount(){
        return new ResponseEntity<>("test test", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result) {
        // Validate passwords match
        userValidator.validate(user, result);

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        user.setPassword(user.getPassword());
        User newUser = userService.register(user);

        return new ResponseEntity<String>("Created", HttpStatus.CREATED);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> profile(@PathVariable("id") Long id) {

        ProfileDto profileDto  =userService.findById(id);
        return new ResponseEntity<ProfileDto>(profileDto, HttpStatus.CREATED);
    }

    @GetMapping("/getIdOfUserName/{username}")
    public ResponseEntity<?> getIdOfUserName(@PathVariable("username") String username) {

        ProfileDto profileDto  =userService.findByUserName(username);
        return new ResponseEntity<>(profileDto.getId(), HttpStatus.CREATED);
    }


    @GetMapping("/likedCount/{id}")
    public ResponseEntity<?> sendPhoneVerifyCode( @PathVariable("id") Long userId) {

        Integer likeCount  =userService.point(userId);
        return new ResponseEntity<Integer>(likeCount, HttpStatus.CREATED);
    }

    @GetMapping("/sendPhoneVerifyCode/{phone}")
    public ResponseEntity<?> sendPhoneVerifyCode( @PathVariable("phone") String phone) {

        userService.sendPhoneVerifyCode(phone);
        return new ResponseEntity<String>("OK", HttpStatus.CREATED);
    }

    @GetMapping("/verifyPhone/{code}")
    public ResponseEntity<?> verifyPhone(@PathVariable("code") String code) {
        userService.verifyPhone(code);
        return new ResponseEntity<String>("OK", HttpStatus.CREATED);
    }

    @GetMapping("/phoneVerified/{id}")
    public ResponseEntity<?> phoneVerificationState(@PathVariable("id") Long id) {
        User user = userService.findEntityById(id);
        return new ResponseEntity<Boolean>(user.getPhoneVerified(), HttpStatus.CREATED);
    }


    @GetMapping("/getPhone/")
    public ResponseEntity<?> getPhone() {
        User loggedUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new ResponseEntity<String>(loggedUser.getPhone(), HttpStatus.CREATED);
    }
    @GetMapping("forgottenPassword/{phone}")
    public ResponseEntity<?> sendForgottenPasswordMail(@PathVariable("phone") String phone) {
        userService.forgottePasswordSendPass(phone);
        return new ResponseEntity<>("mail sent", HttpStatus.OK);
    }
    @GetMapping("search/{searchText}/{pageNum}")
    public ResponseEntity<?> search(@PathVariable("searchText") String searchText,@PathVariable("pageNum") Integer pageNum) {
        List<ProfileDto> profileDtos = userService.searchUser(searchText,pageNum);
        return new ResponseEntity<>(profileDtos, HttpStatus.OK);
    }

    @GetMapping("/myProfileInfoForUpdate")
    public ResponseEntity<?> myProfileInfoForUpdate() {

        ProfileInfoForUpdateDto user  =userService.myProfileInfoForUpdate();
        return new ResponseEntity<ProfileInfoForUpdateDto>(user, HttpStatus.CREATED);
    }

    @PostMapping("/updateProfilePic")
    public ResponseEntity<?> changeProfilePic(PhotoFormDto photoFormDto, BindingResult result) {

        profilePicValidator.validate(photoFormDto, result);

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        String picName = userService.updateProfilePic(photoFormDto);
        return new ResponseEntity<String>(picName, HttpStatus.ACCEPTED);
    }

    @PostMapping("/updateInfo")
    public ResponseEntity<?> update(@Valid @RequestBody ProfileInfoForUpdateDto profileInfoForUpdateDto, BindingResult result) {


        //set logged usre id because of security issues

        userUpdateValidator.validate(profileInfoForUpdateDto, result);
        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        userService.update(profileInfoForUpdateDto);

        return new ResponseEntity<String>("updated", HttpStatus.ACCEPTED);
    }
    @PostMapping("/updatePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto, BindingResult result) {
        /*/*/
        changePasswordValidator.validate(changePasswordDto, result);

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        userService.changePassword(changePasswordDto);

        return new ResponseEntity<ChangePasswordDto>(changePasswordDto, HttpStatus.ACCEPTED);
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
        loginValidator.validate(loginRequest,result);

        ResponseEntity<?> errorMap = mapValidationErrorUtil.MapValidationService(result);
        if (errorMap != null) return errorMap;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        ProfileDto user = userService.findByUserName(loginRequest.getUsername());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX + tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JWTLoginSucessReponse(true, jwt, user.getProfilePicName()));
    }

}
