package com.alinso.popcon.service;

import com.alinso.popcon.entity.City;
import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.dto.ChangePasswordDto;
import com.alinso.popcon.entity.dto.ProfileDto;
import com.alinso.popcon.entity.dto.ProfileInfoForUpdateDto;
import com.alinso.popcon.exception.UserWarningException;
import com.alinso.popcon.repository.CityRepository;
import com.alinso.popcon.repository.UserRepository;
import com.alinso.popcon.util.DateUtil;
import com.alinso.popcon.util.SendSms;
import com.alinso.popcon.util.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class UserService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CityRepository cityRepository;

    public User register(User newUser) {

        newUser.setConfirmPassword("");
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        newUser.setGain(0);
        newUser.setSpent(0);

        newUser.setEnabled(true);
        User user = userRepository.save(newUser);
        return user;
    }

    public ProfileDto toProfileDto(User user) {
        ProfileDto profileDto = modelMapper.map(user, ProfileDto.class);
        profileDto.setAge(UserUtil.calculateAge(user));

        return profileDto;
    }


    public ProfileDto findByUserName(String username) {
        User user = userRepository.findByUsername(username);
        return toProfileDto(user);
    }

    public ProfileDto findById(Long id) {
        User user = userRepository.findById(id).get();
        return toProfileDto(user);
    }

    public Boolean changePassword(ChangePasswordDto changePasswordDto) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        loggedUser.setPassword(bCryptPasswordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(loggedUser);

        return true;
    }

    public ProfileInfoForUpdateDto myProfileInfoForUpdate() {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProfileInfoForUpdateDto profileInfoForUpdateDto = modelMapper.map(u, ProfileInfoForUpdateDto.class);
        profileInfoForUpdateDto.setbDateString(DateUtil.dateToString(u.getBirthDate(), "dd/MM/yyyy"));
        return profileInfoForUpdateDto;
    }

    public void update(ProfileInfoForUpdateDto profileInfoForUpdateDto) {
        User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        City c = null;
        if (profileInfoForUpdateDto.getCityId() != null)
            c = cityRepository.findById(profileInfoForUpdateDto.getCityId()).get();

        u.setBio(profileInfoForUpdateDto.getBio());
        u.setBirthDate(DateUtil.stringToDate(profileInfoForUpdateDto.getbDateString(), "dd/MM/yyyy"));
        u.setCity(c);
        u.setGender(profileInfoForUpdateDto.getGender());
        u.setName(profileInfoForUpdateDto.getName());
        u.setSurname(profileInfoForUpdateDto.getSurname());
        u.setUsername(profileInfoForUpdateDto.getUsername());

        userRepository.save(u);

    }

    public void sendPhoneVerifyCode(String phone) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User oldUserUsesThisPhone = userRepository.findByPhone(phone);

        if(oldUserUsesThisPhone!=null && oldUserUsesThisPhone.getId()!=loggedUser.getId())
            throw   new UserWarningException("Bu telefon numarası kullanımda");
        if(oldUserUsesThisPhone!=null && oldUserUsesThisPhone.getId()==loggedUser.getId() && loggedUser.getPhoneVerified())
            throw new UserWarningException("Bu telefon numarasını zaten doğruladın");

        SendSms sendSms = new SendSms();
        Random random = new Random();
        Integer code = random.nextInt(999999);


        String message = "PopCon Doğrulama Kodu:" + code.toString();
        sendSms.send(message, phone);

        loggedUser.setPhoneVerifyCode(code.toString());
        loggedUser.setPhone(phone);
        userRepository.save(loggedUser);
    }

    public void forgottePasswordSendPass(String phone) {
        User user;
        try {
            user = userRepository.findByPhone(phone);
        } catch (NoSuchElementException e) {
            throw new UserWarningException("Bu numara ile kayıtlı kullanıcı bulunamadı");
        }


        Random rnd = new Random();
        Integer pureRand = rnd.nextInt(999999);
        Integer pass = pureRand + 100000;
        user.setPassword(bCryptPasswordEncoder.encode(pass.toString()));
        userRepository.save(user);

        SendSms.send("PopCon yeni şifren : " + pass.toString(), phone);
    }


    public void verifyPhone(String code) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(loggedUser.getPhoneVerifyCode().equals(code) && !code.equals("")){
            loggedUser.setPhoneVerified(true);
            loggedUser.setPhoneVerifyCode("");
            userRepository.save(loggedUser);
        }else{
            throw new UserWarningException("Kod yanlış, tekrar dene!");
        }

    }

    public User findEntityById(Long id) {
    User user = userRepository.findById(id).get();
    return user;
    }
}
