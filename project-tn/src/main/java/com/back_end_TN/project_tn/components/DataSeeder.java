package com.back_end_TN.project_tn.components;

import com.back_end_TN.project_tn.entitys.Permission;
import com.back_end_TN.project_tn.repositorys.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner  {

    private final PermissionRepository permissionRepository;


    @Override
    public void run(String... args)  {
        seederPermission();
    }

    public void seederPermission () {
        System.out.println("Check count: " + permissionRepository.count());
        if (permissionRepository.count() == 0) {
            permissionRepository.save(Permission.builder().name("CREATE").build());
            permissionRepository.save(Permission.builder().name("UPDATE").build());
            permissionRepository.save(Permission.builder().name("READ").build());
            permissionRepository.save(Permission.builder().name("DELETE").build());
            permissionRepository.save(Permission.builder().name("FULL_ACCESS").build());
        }
     }


}
