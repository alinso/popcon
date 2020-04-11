package com.alinso.popcon.validator;

import com.alinso.popcon.entity.User;
import com.alinso.popcon.entity.dto.ProfileInfoForUpdateDto;
import com.alinso.popcon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserUpdateValidator implements Validator {

    @Autowired
    UserRepository userRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return ProfileInfoForUpdateDto.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {

        ProfileInfoForUpdateDto user = (ProfileInfoForUpdateDto) object;
        User loggedUser  =(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User oldUserUsesThisUsername= userRepository.findByUsername(user.getUsername());
        if(oldUserUsesThisUsername!=null && oldUserUsesThisUsername.getId()!=loggedUser.getId()){
            errors.rejectValue("username", "Match", "Bu kullanıcı adı alınmış");
        }

    }
}
