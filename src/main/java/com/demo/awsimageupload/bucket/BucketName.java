package com.demo.awsimageupload.bucket;

import lombok.Getter;

public enum BucketName {

    PROFILE_IMAGE("practice-image-upload-123");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
