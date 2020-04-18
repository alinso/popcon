
package com.alinso.popcon.validator;


import com.alinso.popcon.entity.dto.photo.PhotoFormDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PhotoUpdateValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PhotoFormDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PhotoFormDto photoUpdateDto = (PhotoFormDto) target;


        if(photoUpdateDto.getCategoryIds()==null || photoUpdateDto.getCategoryIds().size()<1){
            errors.rejectValue("categoryIds", "","En az bir kategori seçmelisin");
        }
    }
}