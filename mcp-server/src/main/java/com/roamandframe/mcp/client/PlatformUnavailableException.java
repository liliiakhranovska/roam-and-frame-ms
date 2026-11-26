package com.roamandframe.mcp.client;

public class PlatformUnavailableException extends ToolException {

    PlatformUnavailableException(Backend backend) {
        super("The " + backend.label() + " service is temporarily unavailable. Try again later.");
    }

    PlatformUnavailableException(Backend backend, Throwable cause) {
        super("The " + backend.label() + " service is temporarily unavailable. Try again later.", cause);
    }
}
