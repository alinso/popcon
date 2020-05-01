package com.alinso.popcon.service;

import com.alinso.popcon.entity.City;
import com.alinso.popcon.entity.Photo;
import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.dto.user.ChangePasswordDto;
import com.alinso.popcon.entity.dto.user.ProfileDto;
import com.alinso.popcon.entity.dto.user.ProfileInfoForUpdateDto;
import com.alinso.popcon.entity.dto.photo.PhotoFormDto;
import com.alinso.popcon.entity.enums.Gender;
import com.alinso.popcon.exception.UserWarningException;
import com.alinso.popcon.repository.CityRepository;
import com.alinso.popcon.repository.PhotoRepository;
import com.alinso.popcon.repository.UserRepository;
import com.alinso.popcon.repository.VoteRepository;
import com.alinso.popcon.util.DateUtil;
import com.alinso.popcon.util.FileStorageUtil;
import com.alinso.popcon.util.SendSms;
import com.alinso.popcon.util.UserUtil;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.StoredProcedureQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

@Service
public class UserService {



    @Autowired
    EntityManager entityManager;
    @Autowired
    UserRepository userRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    FileStorageUtil fileStorageUtil;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    MessageService messageService;

    @Autowired
    PhotoService photoService;

    public User register(User newUser) {

        newUser.setConfirmPassword("");
        newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
        newUser.setGain(0);
        newUser.setSpent(0);
        newUser.setName("");
        newUser.setSurname("");
        newUser.setRole("ROLE_USER");
        newUser.setBio("");
        newUser.setPhoneVerified(false);
        newUser.setProfilePicName("user.png");
        newUser.setEnabled(true);
        newUser.setCorrectGuessCount(0);
        newUser.setWrongGuessCount(0);

        if (newUser.getGender() == Gender.FEMALE)
            newUser.setPreferredGender(Gender.MALE);

        if (newUser.getGender() == Gender.MALE)
            newUser.setPreferredGender(Gender.FEMALE);

        User user = userRepository.save(newUser);

        notificationService.newGreetingMessage(user);
        messageService.greetingMessageForNewUser(user);
        return user;
    }

    public ProfileDto toDto(User user) {
        ProfileDto profileDto = modelMapper.map(user, ProfileDto.class);
        profileDto.setAge(UserUtil.calculateAge(user));

        return profileDto;
    }

    public Integer point(Long userId) {
        User user = userRepository.findById(userId).get();
        Integer allLikedOfUser = voteRepository.getLikedCountOfUser(user);


        Integer point = allLikedOfUser + (user.getCorrectGuessCount() / 2) - (user.getWrongGuessCount() / 3);
        return point;

    }


    public List<ProfileDto> toDtoList(List<User> users) {


        List<ProfileDto> profileDtos = new ArrayList<>();
        for (User u : users) {
            profileDtos.add(toDto(u));
        }

        return profileDtos;
    }


    public ProfileDto findByUserName(String username) {
        User user = userRepository.findByUsername(username);
        return toDto(user);
    }

    public ProfileDto findById(Long id) {
        User user = userRepository.findById(id).get();
        return toDto(user);
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

        u.setBirthDate(DateUtil.stringToDate(profileInfoForUpdateDto.getbDateString(), "dd/MM/yyyy"));

        u.setBio(profileInfoForUpdateDto.getBio());
        u.setCity(c);
        u.setGender(profileInfoForUpdateDto.getGender());
        u.setName(profileInfoForUpdateDto.getName());
        u.setSurname(profileInfoForUpdateDto.getSurname());
        u.setUsername(profileInfoForUpdateDto.getUsername());
        u.setPreferredGender(profileInfoForUpdateDto.getPreferredGender());

        userRepository.save(u);

    }

    public void sendPhoneVerifyCode(String phone) {
        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User oldUserUsesThisPhone = userRepository.findByPhone(phone);

        if (oldUserUsesThisPhone != null && oldUserUsesThisPhone.getId() != loggedUser.getId())
            throw new UserWarningException("Bu telefon numarası kullanımda");
        if (oldUserUsesThisPhone != null && oldUserUsesThisPhone.getId() == loggedUser.getId() && loggedUser.getPhoneVerified())
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
        if (loggedUser.getPhoneVerifyCode().equals(code) && !code.equals("")) {
            loggedUser.setPhoneVerified(true);
            loggedUser.setPhoneVerifyCode("");
            userRepository.save(loggedUser);
        } else {
            throw new UserWarningException("Kod yanlış, tekrar dene!");
        }

    }

    public User findEntityById(Long id) {
        User user = userRepository.findById(id).get();
        return user;
    }

    public List<ProfileDto> searchUser(String searchText, Integer pageNum) {

        Pageable pageable = PageRequest.of(pageNum, 20);
        searchText.replaceAll("\\s+", "");
        List<User> users = userRepository.searchUser(searchText, pageable);
        List<ProfileDto> profileDtos = new ArrayList<>();
        for (User user : users) {
            profileDtos.add(toDto(user));
        }
        return profileDtos;
    }

    public String updateProfilePic(PhotoFormDto photoFormDto) {

        String extension = FilenameUtils.getExtension(photoFormDto.getFile().getOriginalFilename());
        String newName = fileStorageUtil.makeFileName() + "." + extension;

        User loggedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //save new file and remove old one
        if (!loggedUser.getProfilePicName().equals("user.png"))
            fileStorageUtil.deleteFile(loggedUser.getProfilePicName());
        fileStorageUtil.storeFile(photoFormDto.getFile(), newName, true);

        //update database
        loggedUser.setProfilePicName(newName);
        userRepository.save(loggedUser);
        return newName;
    }




    public void deleteById(Long id) {

        try {

            User user = userRepository.getOne(id);

            //Delete profile photo
            String profilePhoto = user.getProfilePicName();
            fileStorageUtil.deleteFile(profilePhoto);

            //Delete album photos
            List<Photo> photos = photoService.getAllByUserId(id);
            for (Photo p : photos) {
                photoService.delete(p.getFileName());
            }

            StoredProcedureQuery delete_user_sp = entityManager.createNamedStoredProcedureQuery("delete_user_sp");
            delete_user_sp.setParameter("userId", id);
            delete_user_sp.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
