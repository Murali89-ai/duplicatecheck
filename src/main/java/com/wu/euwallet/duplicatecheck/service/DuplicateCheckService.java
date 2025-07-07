package com.wu.euwallet.duplicatecheck.service;

import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;
import com.wu.euwallet.duplicatecheck.model.response.ProfileUpdateResponse;

public interface DuplicateCheckService {
    ProfileUpdateResponse updateProfile(ProfileUpdateRequest request);
}
