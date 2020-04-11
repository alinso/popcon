package com.alinso.popcon.validator;

import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.enums.Gender;
import com.alinso.popcon.repository.UserRepository;
import com.alinso.popcon.service.UserService;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@Component
public class UserValidator implements Validator {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository  userRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {

        User user = (User) object;

        if (user.getPassword().length() < 6) {
            errors.rejectValue("password", "Length", "Şifre en az 6 karakter olmalıdır");
        }


        User oldUserUsesThisUsername= userRepository.findByUsername(user.getUsername());
        if(oldUserUsesThisUsername!=null){
            errors.rejectValue("username", "Match", "Bu kullanıcı adı alınmış");
        }

        if (user.getUsername().length() < 3) {
            errors.rejectValue("username", "Match", "Kullanıcı adı en az 3 karakter olmalıdır");
        }

        if( !user.getUsername().matches("\\w+")){
            errors.rejectValue("username", "Match", "Kullanıcı adı Türkçe karakter barındıramaz");
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "Match", "Şifreler eşleşmiyor");

        }


        if (user.getGender() == Gender.UNSELECTED) {
            errors.rejectValue("gender", "Match", "Cinsiyet Seçmelisin");
        }

    }
}
