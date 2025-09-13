package com.molkovor.manager.config.security;

//import com.molkovor.manager.entity.Authority;
//import com.molkovor.manager.repository.ShopUserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

//@RequiredArgsConstructor
//public class ShopUserDetailService implements UserDetailsService {
//
//    private final ShopUserRepository repository;
//
//    @Override
//    @Transactional(readOnly = true)
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return repository.findByUsername(username)
//                .map(user -> User.builder()
//                        .username(user.getUsername())
//                        .password(user.getPassword())
//                        .authorities(user.getAuthorities().stream()
//                                .map(Authority::getAuthority)
//                                .map(SimpleGrantedAuthority::new)
//                                .toList())
//                        .build())
//                .orElseThrow(() -> new UsernameNotFoundException("User %s not found".formatted(username)));
//    }
//}
