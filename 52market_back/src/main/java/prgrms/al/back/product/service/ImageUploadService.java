package prgrms.al.back.product.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ImageUploadService {

    private final UploadService s3Service;

    public String uploadImage(MultipartFile file){ // productService에서 사용하면될듯
        String fileName = createFileName(file.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType((file.getContentType()));
        try(InputStream inputStream = file.getInputStream()){
            s3Service.uploadFile(inputStream,objectMetadata,fileName);
        } catch (IOException e){
            throw new IllegalArgumentException(String.format("파일 변환 에러 (%s)",file.getOriginalFilename()));
        }
        return s3Service.getFileUrl(fileName);
    }

    private String createFileName(String originalFileName){ //파일이름 유니크하게
        return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }

    private String getFileExtension(String fileName){
        try{
            return fileName.substring(fileName.lastIndexOf("."));
        }catch (StringIndexOutOfBoundsException e){
            throw new IllegalArgumentException(String.format("잘못된 형식의 파일 (%s) 입니다",fileName));
        }
    }
}