package controller;

import dto.MemberLoginDTO;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.response.ResponseCode;
import message.PrintResultMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import service.MemberAuthService;
import view.PrintHandler;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MemberAuthControllerTests {

    private MemberAuthService mockMemberAuthService;
    private PrintHandler mockPrintHandler;
    private MemberAuthController memberAuthController;

    @BeforeEach
    void setUp() {
        mockMemberAuthService = mock(MemberAuthService.class);
        mockPrintHandler = mock(PrintHandler.class);
        memberAuthController = new MemberAuthController(mockMemberAuthService, mockPrintHandler);
    }

    @Test
    void testSignin() throws Throwable {
        // Prepare request
        String sessionId = "mockSessionId";
        Map<String, Object> body = new HashMap<>();
        body.put("id", "testUser");
        body.put("password", "password123");

        @SuppressWarnings("unchecked")
        HttpRequest<Map<String, Object>> request = mock(HttpRequest.class);
        when(request.getBody()).thenReturn(body);
        when(mockMemberAuthService.login(any(MemberLoginDTO.class))).thenReturn(sessionId);

        memberAuthController.signin(request);

        verify(mockMemberAuthService, times(1)).login(any(MemberLoginDTO.class));
        verify(mockPrintHandler, times(1)).printSuccessWithResponseCodeAndCustomMessage(any(HttpResponse.class));

        ArgumentCaptor<HttpResponse<?>> responseCaptor = ArgumentCaptor.forClass(HttpResponse.class);
        verify(mockPrintHandler).printSuccessWithResponseCodeAndCustomMessage(responseCaptor.capture());

        HttpResponse<?> response = responseCaptor.getValue();
        assertEquals(PrintResultMessage.ACCOUNTS_LOGIN_SUCCESS + sessionId, response.getDescription());
        assertEquals(ResponseCode.SUCCESS, response.getResponseCode());
    }

    @Test
    void testSignout() throws Throwable {
        memberAuthController.signout();

        verify(mockMemberAuthService, times(1)).logout();
        verify(mockPrintHandler, times(1)).printSuccessWithResponseCodeAndCustomMessage(any(HttpResponse.class));

        ArgumentCaptor<HttpResponse<?>> responseCaptor = ArgumentCaptor.forClass(HttpResponse.class);
        verify(mockPrintHandler).printSuccessWithResponseCodeAndCustomMessage(responseCaptor.capture());

        HttpResponse<?> response = responseCaptor.getValue();
        assertEquals(PrintResultMessage.ACCOUNTS_LOGOUT_SUCCESS, response.getDescription());
        assertEquals(ResponseCode.SUCCESS, response.getResponseCode());
    }

}