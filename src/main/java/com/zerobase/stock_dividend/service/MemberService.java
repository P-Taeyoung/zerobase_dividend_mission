package com.zerobase.stock_dividend.service;

import com.zerobase.stock_dividend.model.Auth;
import com.zerobase.stock_dividend.model.MemberEntity;
import com.zerobase.stock_dividend.persist.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(s)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    public MemberEntity register(Auth.SignUp member) {
       boolean exists = this.memberRepository.existsByUsername(member.getUsername());
       if (exists) {
           throw new RuntimeException("Username already exists");
       }

       member.setPassword(this.passwordEncoder.encode(member.getPassword()));
       return this.memberRepository.save(member.toEntity());
    }

    public MemberEntity authenticate(Auth.SignIn member) {
        var user = this.memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        if(!this.passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        return user;

    }


}
