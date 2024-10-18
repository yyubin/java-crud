package controller;

import config.annotations.Controller;
import config.annotations.Requires;
import controller.annotations.GetMapping;
import controller.annotations.PostMapping;
import dto.MemberLoginDTO;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.response.ResponseCode;
import message.PrintResultMessage;
import service.MemberAuthService;
import view.PrintHandler;

import java.util.Map;

@Controller
@Requires(dependsOn = {MemberAuthService.class, PrintHandler.class})
public class MemberAuthController implements ControllerInterface{

    private final MemberAuthService memberAuthService;
    private final PrintHandler printHandler;

    public MemberAuthController(MemberAuthService memberAuthService, PrintHandler printHandler) {
        this.memberAuthService = memberAuthService;
        this.printHandler = printHandler;
    }

    @PostMapping(path = "/accounts/signin")
    public void signin(HttpRequest<Map<String, Object>> request) throws RuntimeException {
        Map<String, Object> body = request.getBody();
        MemberLoginDTO memberLoginDTO = new MemberLoginDTO(
                (String) body.get("id"),
                (String) body.get("password")
        );
        String sessionId = memberAuthService.login(memberLoginDTO);
        HttpResponse<?> response = new HttpResponse<>(
                PrintResultMessage.ACCOUNTS_LOGIN_SUCCESS + sessionId,
                ResponseCode.SUCCESS,
                null
        );
        printHandler.printSuccessWithResponseCodeAndCustomMessage(response);
    }

    @GetMapping(path = "/accounts/signout")
    public void signout() {
        memberAuthService.logout();
        HttpResponse<?> response = new HttpResponse<>(
                PrintResultMessage.ACCOUNTS_LOGOUT_SUCCESS,
                ResponseCode.SUCCESS,
                null
        );
        printHandler.printSuccessWithResponseCodeAndCustomMessage(response);
    }

}