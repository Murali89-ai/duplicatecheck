
package com.wu.euwallet.duplicatecheck.service;

import com.wu.euwallet.duplicatecheck.dto.DuplicateCheckResponse;
import com.wu.euwallet.duplicatecheck.model.common.kafka.TransactionData;
import com.wu.euwallet.duplicatecheck.model.request.ProfileUpdateRequest;

public interface DuplicateCheckService {

    DuplicateCheckResponse processProfileUpdate(ProfileUpdateRequest profileUpdateRequest,
                                                TransactionData transactionData);
}
