package com.molkovor.manager.scratch;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ScratchEncoder {
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder()
                .encode("password"));
    }
}
