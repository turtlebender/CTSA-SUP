package org.globus.cs.render;

/**
 * Thrown when an error occurs during a prerendering operation.
 *
 * @author Tom Howe
 */
public class RenderingException extends Exception {

    public RenderingException() {
    }

    public RenderingException(String message) {
        super(message);
    }

    public RenderingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RenderingException(Throwable cause) {
        super(cause);
    }
}
