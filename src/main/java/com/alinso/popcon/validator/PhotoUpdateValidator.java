
package com.alinso.popcon.validator;


import com.alinso.popcon.entity.dto.photo.PhotoUpdateDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PhotoUpdateValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PhotoUpdateDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PhotoUpdateDto photoUpdateDto = (PhotoUpdateDto) target;


        if(photoUpdateDto.getCategoryIds()==null || photoUpdateDto.getCategoryIds().size()<1){
            errors.rejectValue("categoryIds", "","En az bir kategori seçmelisin");
        }
    }
}