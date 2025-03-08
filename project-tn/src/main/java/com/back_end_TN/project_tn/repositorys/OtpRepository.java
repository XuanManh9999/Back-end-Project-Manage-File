package com.back_end_TN.project_tn.repositorys;

import com.back_end_TN.project_tn.entitys.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
}
