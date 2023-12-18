package com.demo.awsimageupload.profile;

import com.amazonaws.AmazonServiceException;
import com.demo.awsimageupload.bucket.BucketName;
import com.demo.awsimageupload.fileStore.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UserProfileService {

    private final UserProfileDataAccessService userProfileDataAccessService;
    private final FileStore filestore;

    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore filestore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.filestore = filestore;
    }

    List<UserProfile> getUserProfiles() {
        return userProfileDataAccessService.getUserProfiles();
    }

    public void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
        isFileEmpty(file);
        isImage(file);
        UserProfile user = getUserProfile(userProfileId);
        Map<String, String> metaData = getExtractMetaData(file);
        String path = String.format("%s", BucketName.PROFILE_IMAGE.getBucketName());
        System.out.println(path);
        String fileName = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());
        System.out.println(fileName);
        try {
            filestore.save(path, fileName, Optional.of(metaData), file.getInputStream());
            user.setUserProfileImageLink(fileName);
        } catch (IOException e) {
           throw new IllegalStateException(e);
        }
    }

    byte[] downloadUserProfileImage(UUID userProfileId) {
        UserProfile user = getUserProfile(userProfileId);
        String path = String.format("%s", BucketName.PROFILE_IMAGE.getBucketName());

        return user.getUserProfileImageLink()
                .map(key -> filestore.download(path, key))
                .orElse(new byte[0]);
    }

    private Map<String, String> getExtractMetaData(MultipartFile file) {
        Map<String, String> metaData = new HashMap<>();
        metaData.put("Content-Type", file.getContentType());
        metaData.put("Content-Length", String.valueOf(file.getSize()));
        return metaData;
    }

    private UserProfile getUserProfile(UUID userProfileId) {
        return userProfileDataAccessService
                .getUserProfiles()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("User profile %s not found", userProfileId)));
    }

    private void isImage(MultipartFile file) {
        if(!Arrays.asList(org.apache.http.entity.ContentType.IMAGE_JPEG.getMimeType(),
                org.apache.http.entity.ContentType.IMAGE_PNG.getMimeType(),
                org.apache.http.entity.ContentType.IMAGE_GIF.getMimeType()).contains(file.getContentType())){
            throw new IllegalStateException("File must be an image [" + file.getContentType() + "]");
        }
    }

    private void isFileEmpty(MultipartFile file) {
        if(file.isEmpty()){
            throw new IllegalStateException("Cannot uploaed empty file ["+ file.getSize() + "]");
        }
    }
}
