package com.alinso.popcon.validator;


import com.alinso.popcon.entity.dto.contest.CustomContestFormDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CustomContestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CustomContestFormDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CustomContestFormDto customContestFormDto = (CustomContestFormDto) target;


        if(customContestFormDto.getPhoto1Id() ==0 ){
            errors.rejectValue("photo1Id", "","İki fotoğraf seçmelisin");
        }
        if(customContestFormDto.getPhoto2Id() ==0){
            errors.rejectValue("photo2Id", "","İki fotoğraf seçmelisin");
        }
        if(customContestFormDto.getTitle().equals("")){
            errors.rejectValue("title", "","Bir başllık yazmalısın");
        }
        if(customContestFormDto.getMaxVote()==0){
            errors.rejectValue("maxVote", "","Sıfırdan büyük bir oy sayısı seçmelisin");
        }
    }
}