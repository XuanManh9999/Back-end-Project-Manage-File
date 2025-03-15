package com.back_end_TN.project_tn.repositorys;

import com.back_end_TN.project_tn.entitys.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
}
