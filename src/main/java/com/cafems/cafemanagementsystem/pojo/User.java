package com.cafems.cafemanagementsystem.pojo;

import com.cafems.cafemanagementsystem.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@org.hibernate.annotations.NamedQuery(name="User.findByEmailId",
        query = "select u from User u where u.email =:email")
@org.hibernate.annotations.NamedQuery(name="User.getAllUser",
        query = " select new com.cafems.cafemanagementsystem.wrapper.UserWrapper(u.id, u.name,u.email,u.contactNumber,u.status) from User u where u.role='user'")
@org.hibernate.annotations.NamedQuery(name="User.updateStatus",
        query = "update User u set u.status=:status where u.id=:id")
@org.hibernate.annotations.NamedQuery(name="User.getAllAdmin",
        query = " select u.email from User u where u.role='admin'")
@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "user")
public class User implements UserDetails {
    //private static final long serialVersionUID =1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "contactNumber")
    private String contactNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name="status")
    private String status;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
