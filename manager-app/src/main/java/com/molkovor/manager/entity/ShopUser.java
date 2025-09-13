package com.molkovor.manager.entity;

//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.List;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "t_user", schema = "manager")
//public class ShopUser {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
//
//    @Column(name = "c_username", nullable = false, unique = true)
//    private String username;
//
//    @Column(name = "c_password")
//    private String password;
//
//    @ManyToMany
//    @JoinTable(schema = "manager", name = "t_user_authority",
//            joinColumns = @JoinColumn(name = "c_id_user"),
//            inverseJoinColumns = @JoinColumn(name = "c_id_authority"))
//    private List<Authority> authorities;
//}
