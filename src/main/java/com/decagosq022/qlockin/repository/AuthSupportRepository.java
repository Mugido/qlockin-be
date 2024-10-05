package com.decagosq022.qlockin.repository;

import com.decagosq022.qlockin.entity.AuthnSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthSupportRepository extends JpaRepository<AuthnSupport, String> {
    AuthnSupport findByCredId(String base64Url);

    // Custom query to return only the list of IDs
    @Query("SELECT a.id FROM AuthnSupport a")
    List<String> findAllIds();

    AuthnSupport findByUserName(String username);
}
