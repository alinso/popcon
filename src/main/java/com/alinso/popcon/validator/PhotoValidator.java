package com.alinso.popcon.validator;


import com.alinso.popcon.entity.dto.photo.PhotoFormDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PhotoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PhotoFormDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PhotoFormDto photoFormDto = (PhotoFormDto) target;


        MultipartFile file = photoFormDto.getFile();

        if(photoFormDto.getCategoryIds()==null || photoFormDto.getCategoryIds().size()<1){
            errors.rejectValue("categoryIds", "","En az bir kategori seçmelisin");
        }

        if (file != null && file.isEmpty()){
            errors.rejectValue("file", "Match","Dosya Seçmelisin");
        }

        if(!(file.getContentType().toLowerCase().equals("image/jpg")
                || file.getContentType().toLowerCase().equals("image/jpeg")
                || file.getContentType().toLowerCase().equals("image/png"))){
            errors.rejectValue("file", "","Yalnızca jpeg/jpg/png türündeki dosyaları yükleyebilirsin");
        }

        if(file.getSize()>9937152){ //6 MB
            errors.rejectValue("file","","Max dosya boyutu 9 MB olabilir");
        }

    }
}