package fr.pingtimeout.tyrion.util;

import org.junit.Test;

import java.io.PrintStream;

import static org.mockito.Mockito.*;

public class SimpleLoggerTest {


    @Test
    public void no_messages_should_be_printed_with_off_level() {
        // Given
        PrintStream printStream = mock(PrintStream.class);
        SimpleLogger simpleLogger = new SimpleLogger(SimpleLogger.Level.OFF, printStream);
        Object[] args = {"Foo", "bar"};
        String message = "Test String";

        // When
        printMessageWithEveryLevel(simpleLogger, message, args, new Exception());

        // Then
        verifyZeroInteractions(printStream);
        verifyNoMoreInteractions(printStream);
    }


    @Test
    public void only_warn_messages_should_be_printed_with_warn_level() {
        // Given
        PrintStream printStream = mock(PrintStream.class);
        SimpleLogger simpleLogger = new SimpleLogger(SimpleLogger.Level.WARN, printStream);
        Object[] args = {"Foo", "bar"};
        String message = "Test String";

        // When
        printMessageWithEveryLevel(simpleLogger, message, args, new Exception());

        // Then
        verify(printStream).printf("WARN  - " + message + "\n", args);
        verifyNoMoreInteractions(printStream);
    }


    @Test
    public void only_warn_and_info_messages_should_be_printed_with_info_level() {
        // Given
        PrintStream printStream = mock(PrintStream.class);
        SimpleLogger simpleLogger = new SimpleLogger(SimpleLogger.Level.INFO, printStream);
        Object[] args = {"Foo", "bar"};
        String message = "Test String";

        // When
        printMessageWithEveryLevel(simpleLogger, message, args, new Exception());

        // Then
        verify(printStream).printf("WARN  - " + message + "\n", args);
        verify(printStream).printf("INFO  - " + message + "\n", args);
        verifyNoMoreInteractions(printStream);
    }

    @Test
    public void only_warn_info_and_debug_messages_should_be_printed_with_debug_level() {
        // Given
        PrintStream printStream = mock(PrintStream.class);
        SimpleLogger simpleLogger = new SimpleLogger(SimpleLogger.Level.DEBUG, printStream);
        Object[] args = {"Foo", "bar"};
        String message = "Test String";
        Exception exception = new Exception();

        // When
        printMessageWithEveryLevel(simpleLogger, message, args, exception);

        // Then
        verify(printStream).printf("WARN  - " + message + "\n", args);
        verify(printStream).printf("INFO  - " + message + "\n", args);
        verify(printStream).printf("DEBUG - " + message + "\n", args);
        verify(printStream).println(exception);
        // Do not check the "at ..." lines
    }

    @Test
    public void all_messages_should_be_printed_with_trace_level() {
        // Given
        PrintStream printStream = mock(PrintStream.class);
        SimpleLogger simpleLogger = new SimpleLogger(SimpleLogger.Level.TRACE, printStream);
        Object[] args = {"Foo", "bar"};
        String message = "Test String";
        Exception exception = new Exception();

        // When
        printMessageWithEveryLevel(simpleLogger, message, args, exception);

        // Then
        verify(printStream).printf("WARN  - " + message + "\n", args);
        verify(printStream).printf("INFO  - " + message + "\n", args);
        verify(printStream).printf("DEBUG - " + message + "\n", args);
        verify(printStream).printf("TRACE - " + message + "\n", args);
        verify(printStream).println(exception);
        // Do not check the "at ..." lines
    }


    @Test
    public void static_methods_should_delegate_to_logger() {
        // Given
        SimpleLogger simpleLogger = mock(SimpleLogger.class);
        SimpleLogger.LOG = simpleLogger;
        Object[] args = {"Foo", "bar"};
        String message = "Test String";
        Exception exception = new Exception();

        // When
        SimpleLogger.warn(message, args);
        SimpleLogger.info(message, args);
        SimpleLogger.debug(message, args);
        SimpleLogger.debug(exception);
        SimpleLogger.trace(message, args);

        // Then
        verify(simpleLogger).warnMessage(message, args);
        verify(simpleLogger).infoMessage(message, args);
        verify(simpleLogger).debugMessage(message, args);
        verify(simpleLogger).traceMessage(message, args);
        verify(simpleLogger).debugMessage(exception);
    }

    private void printMessageWithEveryLevel(SimpleLogger simpleLogger, String message, Object[] args, Exception exception) {
        simpleLogger.warnMessage(message, args);
        simpleLogger.infoMessage(message, args);
        simpleLogger.debugMessage(message, args);
        simpleLogger.debugMessage(exception);
        simpleLogger.traceMessage(message, args);
    }
}
