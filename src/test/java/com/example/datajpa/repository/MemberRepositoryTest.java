package com.example.datajpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.example.datajpa.dto.MemberDto;
import com.example.datajpa.entity.Member;
import com.example.datajpa.entity.Team;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    @DisplayName("Member 테스트")
    @Test
    public void testMember() throws Exception {
        Member member = Member.builder().username("memberB").build();
        Member saveMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(saveMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); // 같은 트랜잭션에서는 영속성 컨텍스트의 동일성 보장!
    }

    @DisplayName("Member 기본 CRUD 체크")
    @Test
    public void basicCRUD() throws Exception {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        // 단건 조회 검사
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        findMember1.setUsername("member1!!!!!!!!!!!!!");
//
//        //리스트 조회 검증
//        List<Member> all = memberJpaRepository.findAll();
//        assertThat(all.size()).isEqualTo(2);
//
//        //카운트 검
//        long count = memberJpaRepository.count();
//        assertThat(count).isEqualTo(2);
//
//        //삭제 검증
//        memberJpaRepository.delete(member1);
//        memberJpaRepository.delete(member2);
    }

    @DisplayName("findByUsernameAndAgeGreaterThen 테스트")
    @Test
    public void findByUsernameAndAgeGreaterThen() throws Exception {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("aaa", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("aaa", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("aaa");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @DisplayName("Method Query Test")
    @Test
    public void find_hello_by() throws Exception {
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @DisplayName("NamedQuery 테스트")
    @Test
    public void named_query() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findByUsername("AAA");

        assertEquals(10, aaa.get(0).getAge());
    }

    @DisplayName("레포지토리에 쿼리 정의 테스트")
    @Test
    public void query_test() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertEquals(m1, result.get(0));
    }

    @DisplayName("findUsernameList 테스트")
    @Test
    public void find_username_list_query_test() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @DisplayName("JPQL로 Dto로 받아오는 것 테스트")
    @Test
    public void findMemberDto() throws Exception {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member = new Member("aaa", 10);
        member.setTeam(team);
        memberRepository.save(member);

        List<MemberDto> memberDtos = memberRepository.findMemberDto();
        for (MemberDto dto : memberDtos) {
            System.out.println("dto = " + dto);
        }
    }

    @DisplayName("SQL in절 테스트, 리스트 객체도 파라미터 바인딩이 된다!")
    @Test
    public void findByNames() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @DisplayName("다양한 리턴타입 테스트")
    @Test
    public void retrunType_Test() throws Exception {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("AAA");
        System.out.println("List ==>" + aaa.get(0).getUsername());
        Member aaa1 = memberRepository.findOneMemberByUsername("AAA");
        System.out.println("그냥 Member ==>" + aaa1.getUsername());
        Optional<Member> aaa2 = memberRepository.findOptionalMemberByUsername("AAA");
        System.out.println("Optional Member ==>" + aaa2.get().getUsername());

        //없는 것을 찾으려고 할 때
        List<Member> asdasdasd = memberRepository.findListByUsername("asdasd");
        System.out.println("result = " + asdasdasd.size()); //없으면 empty Collection이 조회가 된다.

        Member asdasdasd1 = memberRepository.findOneMemberByUsername("asdasd");
        System.out.println("asdasdasd1 = " + asdasdasd1); // null이 출력된다.
        // jpa는 없으면 NoResultException이 터지는데, spring-data-jpa는 이걸 try~catch로 감싸가지고 null로 반환합니다. (이게 jpa랑 다른 점)
        // 근데 이건 논쟁이 꽤 있었는데, 이제 자바8이 생기면서 Optional이 생겨나면서 이에 대한 논쟁이 사라짐

        Optional<Member> asdasdasd2 = memberRepository.findOptionalMemberByUsername("asdasdasd");
        System.out.println("asdasdasd2 = " + asdasdasd2); // Optional.empty
        // 데이터 조회했을 때 있을 수도, 없을 수도 있으니까 이럴 땐 Optional 쓰는 것이 맞습니다.

        /*
        근데, 2개가 있는데 하나만 조회할 땐 NonUniqueResultException이 터진다.
         */
        Member m3 = new Member("CCC", 10);
        Member m4 = new Member("CCC", 20);
        memberRepository.save(m3);
        memberRepository.save(m4);

        // NonUniqueResultException이 터지는데, spring-data-jpa가 이걸 spring의 Exception 이름인 IncorrectResultSizeDataAccessException로 변환한다!
        // 스프링 추상화이기 때문에 다른 jpa 구현체를 써도, 이걸 사용하는 클라이언트 코드 쪽에서 예외를 또 고칠 필요가 없습니다.
        // 그래서 이렇게 Exception을 한 번 변환해서 리턴해주고 있습니다.
//        Member ccc = memberRepository.findOneMemberByUsername("CCC"); // NonUniqueResultException -> IncorrectResultSizeDataAccessException

//        메서드 이름을 이상하게 했을 때
        Optional<Member> aaaaaaaaaaaaaaa = memberRepository.findAaaaaaByUsername("AAA");
        aaaaaaaaaaaaaaa.ifPresent(System.out::println);
    }

    @DisplayName("NamedQueryTest")
    @Test
    public void named_query_test() throws Exception {

        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findByUsername("AAA");

        assertEquals(10, aaa.get(0).getAge());

    }

    /**
     * @implNote : 페이징은 딱 잘라서 가져오기에 최적화하기가 쉬운데 totalCount는 크기가 커지면 견적이 너무 커진다.
     * 성능이 너무 저하되는 문제가 있는데(조인을 겁나 해서) 조인을 하지 않아도 되는 케이스가 많다.
     * 그래서 countQuery를 분리하는 작업을 할 수 있습니다.
     */
    @DisplayName("페이징 테스트")
    @Test
    public void paging() throws Exception {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        // spring-data-jpa는 page를 0부터 시작합니다! 1이 아니에요. 주의하세요!!
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));
        // 0페이지에서 3개가져와, 그리고 옵션으로 소팅도 하고 싶으면 소팅도 넣을 수 있다!

        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements(); // 내부적으로 totalCount계산도 해주고 있다.
        // select count(member0_.member_id) as col_0_0_ from member member0_ where member0_.age=10;

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("total = " + totalElements);
        // totalQuery를 날린 적도 없는데, totalQuery가 날라간다.

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @DisplayName("페이징 결과는 항상 DTO로 변환하여 클라이언트에 반환해야 한다.")
    @Test
    public void paging_to_dto() throws Exception {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        Page<MemberDto> mapPage = page.map(member -> new MemberDto(member.getId(), member.getUsername(), "TeamName"));

        mapPage.forEach(m -> System.out.println("=================> "+m));
    }

    @DisplayName("슬라이스 테스트")
    @Test
    public void slice() throws Exception {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        // spring-data-jpa는 page를 0부터 시작합니다! 1이 아니에요. 주의하세요!!
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));
        // 0페이지에서 3개가져와, 그리고 옵션으로 소팅도 하고 싶으면 소팅도 넣을 수 있다!

        Slice<Member> page = memberRepository.findByAgeSlice(age, pageRequest);
        // 쿼리 결과 : select member0_.member_id as member_i1_0_, member0_.age as age2_0_, member0_.team_id as team_id4_0_, member0_.username as username3_0_ from member member0_ left outer join team team1_ on member0_.team_id=team1_.team_id order by member0_.username desc limit 4;
        // 3개를 요청했지만, 1개 더(limit 4) 여유롭게 가져오는 것을 확인할 수 있다. 다음페이지가 있다면 [더보기]등을 지원하기 위함
        // 그리고 TotalCount를 가져오지 않습니다.

        List<Member> content = page.getContent();
        //slice는 토탈 개수를 몰라요. 알 필요가 없으니까 (더보기 버튼 눌러서 페이지 더 불러오고...)
        System.out.println("content.size() = " + content.size());

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @DisplayName("단순하게 앞에거 3개만 가져올래")
    @Test
    public void getTop3() throws Exception {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        // 페이지 쿼리 안쓰고 그냥 Top3를 해줘도 됩니다.
        List<Member> list = memberRepository.findTop3ByAgeOrderByUsernameDesc(age);

        for (Member member : list) {
            System.out.println("member = " + member);
        }
    }

    @DisplayName("Entity는 절대로 api쪽으로 바로 내보내서는 안됩니다.")
    @Test
    public void mapToDto() throws Exception {
        Team team = new Team("teamA");
        teamRepository.save(team);
        Member member = new Member("member1", 10);
        member.setTeam(team);
        memberRepository.save(member);
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // 다음과 같이 하면 page를 유지하면서 Dto로 반환할 수 있습니다.
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), team.getName()));

    }

}
