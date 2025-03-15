package com.back_end_TN.project_tn.entitys;

import com.back_end_TN.project_tn.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class UserEntity  extends BaseEntity<Long> implements UserDetails, Serializable {

     @Column(name = "user_name")
     private String username;
     @Column(name = "password")
//     @Min(value = 6, message = "Mật khẩu phải có tối thiểu 6 kí tự")
     private String password;

     @Column(name = "email", unique = true)
     private String email;

     private Gender gender;
     @Temporal(TemporalType.DATE)
     private Date birthday;
     private Long point;
     @Column(name = "avatar", columnDefinition = "TEXT")
     private String avatar;
     @Column(unique = true)
     @Min(value = 10)
     @Max(value = 10)
     private String phoneNumber;


     @OneToMany(mappedBy = "userId", orphanRemoval = true)
     private List<UserRoleEntity> userRoles;

     @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
     private List<AuthProvider> authProviders;

     @OneToMany(mappedBy = "userId")
     private List<Address> addresses;


     @Override
     public Collection<? extends GrantedAuthority> getAuthorities() {
          return List.of();
     }

     @Override
     public boolean isAccountNonExpired() {
          return UserDetails.super.isAccountNonExpired();
     }

     @Override
     public boolean isAccountNonLocked() {
//          return UserDetails.super.isAccountNonLocked();
          return true;
     }

     @Override
     public boolean isCredentialsNonExpired() {
          return UserDetails.super.isCredentialsNonExpired();
     }

     @Override
     public boolean isEnabled() {
          return UserDetails.super.isEnabled();
     }
}
