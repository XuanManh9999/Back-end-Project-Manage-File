package com.back_end_TN.project_tn.repositorys;

import com.back_end_TN.project_tn.entitys.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {

}
