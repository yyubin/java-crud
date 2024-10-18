package service;

import com.sun.tools.javac.Main;
import config.Container;
import domain.Member;
import dto.MemberLoginDTO;
import dto.MemberRegisterDTO;
import exception.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import repository.InMemoryBoardRepository;
import repository.InMemoryMemberRepository;
import util.RedisSessionManager;
import util.Session;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MemberAuthServiceTests {

    private MemberAuthService memberAuthService;
    private MemberService memberService;

    @BeforeEach
    public void setup() throws Exception {
        Container container = Container.getInstance();
        container.scan("repository");
        container.scan("util");
        container.scan("service");
        container.scan("controller");

        InMemoryMemberRepository memberRepository = container.get(InMemoryMemberRepository.class);
        InMemoryBoardRepository boardRepository = container.get(InMemoryBoardRepository.class);
        RedisSessionManager redisSessionManager = container.get(RedisSessionManager.class);

        memberService = new MemberService(memberRepository);
        memberAuthService = new MemberAuthService(memberService, redisSessionManager);
    }

    @Test
    public void testSuccessfulLogin() {
        MemberRegisterDTO memberDTO = new MemberRegisterDTO("yubin111", "yubin", "yubin@gmail.com", "qwer", LocalDate.now());
        memberService.registerMember(memberDTO);

        MemberLoginDTO memberLoginDTO = new MemberLoginDTO("yubin111", "qwer");
        String sessionId = memberAuthService.login(memberLoginDTO);

        assertNotNull(sessionId);
    }

    @Test
    public void testFailedLogin() {
        MemberRegisterDTO memberDTO = new MemberRegisterDTO("yubin111", "yubin", "yubin@gmail.com", "qwer", LocalDate.now());
        memberService.registerMember(memberDTO);

        assertThrows(InvalidCredentialsException.class, () -> {
            memberAuthService.login(new MemberLoginDTO("yubin111", "wrongPassword"));
        });
    }

    @Test
    public void testSuccessfulLogout() {
        MemberRegisterDTO memberDTO = new MemberRegisterDTO("yubin111", "yubin", "yubin@gmail.com", "qwer", LocalDate.now());
        memberService.registerMember(memberDTO);

        MemberLoginDTO memberLoginDTO = new MemberLoginDTO("yubin111", "qwer");
        String sessionId = memberAuthService.login(memberLoginDTO);
        memberAuthService.logout();
        assertNull(Session.getSessionId());
    }

    @Test
    public void testSessionManagementAfterLogin() {
        MemberRegisterDTO memberDTO = new MemberRegisterDTO("yubin13", "yubin", "yubin@gmail.com", "qwer", LocalDate.now());
        memberService.registerMember(memberDTO);
        String sessionId = memberAuthService.login(new MemberLoginDTO("yubin13", "qwer"));

        assertNotNull(sessionId);

        memberAuthService.logout();
        assertNull(memberAuthService.getRedisSessionManager().getSession(sessionId));
    }

}

