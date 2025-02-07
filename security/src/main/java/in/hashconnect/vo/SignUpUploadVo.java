package in.hashconnect.vo;

import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;

public class SignUpUploadVo {
    private MultipartFile inFile;
    private String type;

    private String uploadLocation;

    private String error;

    public SignUpUploadVo(String type, MultipartFile inFile) {
        this.inFile = inFile;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public MultipartFile getInFile() {
        return inFile;
    }

    public void setInFile(MultipartFile inFile) {
        this.inFile = inFile;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUploadLocation() {
        return uploadLocation;
    }

    public void setUploadLocation(String uploadLocation) {
        this.uploadLocation = uploadLocation;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
