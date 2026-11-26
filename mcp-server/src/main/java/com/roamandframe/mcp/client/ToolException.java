package com.roamandframe.mcp.client;

/** A failure whose message is safe and useful to show to the model. */
public class ToolException extends RuntimeException {
    public ToolException(String message) {
        super(message);
    }

    public ToolException(String message, Throwable cause) {
        super(message, cause);
    }
}
