package com.example.spring_jpa.controller;

import com.example.spring_jpa.entity.Member;
import com.example.spring_jpa.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findById(@PathVariable Long id) {
        memberRepository.findById(id);
        return "Member found with ID: " + id;
    }

    @GetMapping("/members2/{id}")
    public String findById2(@PathVariable("id") Member member) {
        return "Member found with ID: " + member.getId();
    }

    @GetMapping("/members")
    public PagedModel<Member> list(@PageableDefault(size = 3, page = 0) Pageable pageable) {
        Page<Member> all = memberRepository.findAll(pageable);
        return new PagedModel<>(all);
    }

    @PostConstruct
    public void init() {
        memberRepository.save(new Member("John Doe1", 10));
        memberRepository.save(new Member("John Doe2", 10));
        memberRepository.save(new Member("John Doe3", 10));
        memberRepository.save(new Member("John Doe4", 10));
        memberRepository.save(new Member("John Doe5", 10));
        memberRepository.save(new Member("John Doe6", 10));
        memberRepository.save(new Member("John Doe7", 10));
        memberRepository.save(new Member("John Doe8", 10));
        memberRepository.save(new Member("John Doe9", 10));
        memberRepository.save(new Member("John Doe10", 10));
    }
}
