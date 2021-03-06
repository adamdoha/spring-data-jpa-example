package com.example.datajpa.controller;

import javax.annotation.PostConstruct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.datajpa.dto.MemberDto;
import com.example.datajpa.entity.Member;
import com.example.datajpa.repository.MemberRepository;
import com.example.datajpa.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}") // 트랜잭션이 없는 상황에서 조회가 되기 때문에 조회용으로만 사용해야 한다.
    public String findMember2(@PathVariable("id") Member member) { // PK를 data-jpa가 바로 조회해준다.
        return member.getUsername(); // 간단 간단할 때만 쓸 수 있다. 복잡하면 못쓴다.
    }

    /**
     * @implNote : 컨트롤러에서 파라미터가 바인딩 될 때, Pageable이 있으면 PageRequest를 생성해서 값을 채워줍니다. 스프링 자체가

    근데 디폴트 size가 20개야. 이런걸 좀 바꾸고 싶어
    1. 글로벌 설정(application.yml) -> spring.data.web.pageable.default-page-size : 10, max-page-size : 2000
    2. 어노테이션 직접 설정(글로벌 설정보다 우선함) -> @PageDefault(size = 5, sort = "username" ...)
     */
    // 사용예 : http://localhost:8080/members?page=4&size=3&sort=id,desc
    @GetMapping("/members")
    public Page<Member> list(@PageableDefault(size = 5) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page;
    }

    // 항상 API를 반환할 때는 DTO로 반환하라.
    @GetMapping("/members2")
    public Page<MemberDto> list2(@PageableDefault(size = 5) Pageable pageable) {
        return memberRepository.findAll(pageable).map(MemberDto::new);
    }
    // 페이지 번호가 왜 0번부터냐? 1번부터 하고 싶다? -> 니가 커스텀하셈, 직접 만들어야함
    // 또는 One-indexed-parameter 에 true를 준다. 이러면 0이나 1이나 동일한 결과를 내놓는데 한계가 있다.
    // 어떤 한계냐면 페이지 객체 안의 숫자랑 안맞는다(Pageable안의 pageNumber와 커스텀한 페이지 번호와 같지 않다.)

//    @PostConstruct
    public void init() {
        for (int i = 0; i < 1000; i++) {
            memberRepository.save(new Member("user" + i, i));
        }
    }
}
