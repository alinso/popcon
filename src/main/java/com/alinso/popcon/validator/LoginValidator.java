package com.alinso.popcon.validator;

import com.alinso.popcon.repository.UserRepository;
import com.alinso.popcon.security.payload.LoginRequest;
import com.alinso.popcon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;



@Component
public class LoginValidator implements Validator {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository  userRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return LoginRequest.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {

        LoginRequest user = (LoginRequest) object;

        if (user.getPassword().equals("")){
            errors.rejectValue("password", "Length", "Şifre boş olamaz");
        }


        if (user.getUsername().equals("")) {
            errors.rejectValue("username", "Match", "Kullanıcı adı boş olamaz");
        }

    }
}
