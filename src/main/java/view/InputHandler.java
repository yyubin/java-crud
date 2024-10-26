package view;

import config.annotations.Component;
import config.annotations.Priority;
import config.annotations.Requires;
import http.request.RequestHandler;
import message.ExceptionMessage;
import message.InputCautionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.interfaces.InputProcessor;
import view.interfaces.PrintProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

@Component
@Priority(value = 1)
@Requires(dependsOn = {RequestHandler.class, PrintProcessor.class})
public class InputHandler implements InputProcessor {

    private final RequestHandler requestHandler;
    private final PrintProcessor printHandler;

    public InputHandler(RequestHandler requestHandler, PrintProcessor printHandler) {
        this.requestHandler = requestHandler;
        this.printHandler = printHandler;
    }

    public void startInputLoop() {
        printHandler.printCustomMessage(InputCautionMessage.EXIT_GUIDE_MESSAGE);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder rawRequest = new StringBuilder();
        String line;

        try {
            while (true) {
                printHandler.printCustomMessage(InputCautionMessage.CAUTION_COMMAND_FOR_HTTP);
                rawRequest.setLength(0);

                while (!(line = reader.readLine()).isEmpty()){
                    rawRequest.append(line).append("\n");
                }

                if (rawRequest.toString().trim().isEmpty() || rawRequest.toString().trim().equals(InputKeyword.EXIT.getKeyword()) || rawRequest.toString().trim().equals(InputKeyword.EXIT.getKeywordEn())){
                    printHandler.printCustomMessage(InputCautionMessage.EXIT_ACCEPT_MESSAGE);
                    break;
                }

                try {
                    requestHandler.handleRequest(rawRequest.toString());
                }catch (Exception e) {
                    printHandler.printCustomMessage(e.getMessage());
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
