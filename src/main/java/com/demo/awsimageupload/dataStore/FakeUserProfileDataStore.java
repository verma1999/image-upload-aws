package com.demo.awsimageupload.dataStore;

import com.demo.awsimageupload.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {

    private static final List<UserProfile> USER_PROFILE = new ArrayList<>();

    static {
        USER_PROFILE.add(new UserProfile(UUID.fromString("2aeb885d-8631-4880-b4a3-e83aaa8f6a81"), "adi1999", null));
        USER_PROFILE.add(new UserProfile(UUID.fromString("7a0ac875-d279-42eb-863d-cff41e8a268d"), "nitish995", null));
    }

    public List<UserProfile> getUserProfiles() {
        return USER_PROFILE;
    }
}
