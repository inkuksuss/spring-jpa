package com.example.spring_jpa.repository;

import com.example.spring_jpa.entity.Member;

import java.util.List;

public interface MemberQueryRepository {

    List<Member> findByAge(int age);
}
