package com.hcodesolutions.template.util;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * @author Dewmith Mihisara
 * @date 2025-02-06
 * @since 0.0.1
 */
@Configuration
public class CommonUtils {
    public static Pageable setPagination(Integer offset, Integer limit, String columnName) {
        return PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, columnName));
    }

    public String removeTokenPrefix(String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        return token;
    }

    public static User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return  (User) authentication.getPrincipal();
    }
}
