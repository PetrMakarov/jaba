package com.pmakarov.jabaui.console;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Stream data to JTextArea
 */
public class TextAreaOutputStream extends OutputStream {

    private byte[] oneByte;// array for write(int val);
    private Appender appender; // most recent action

    public TextAreaOutputStream(JTextArea textArea) {
        this(textArea, 1000);
    }

    public TextAreaOutputStream(JTextArea textArea, int maxLength) {
        if (maxLength < 1) {
            throw new IllegalArgumentException("TextAreaOutputStream maximum lines must be positive (value=" + maxLength + ")");
        }
        oneByte = new byte[1];
        appender = new Appender(textArea, maxLength);
    }

    /**
     * Clear the current console text area.
     */
    public synchronized void clear() {
        if (appender != null) {
            appender.clear();
        }
    }

    public synchronized void close() {
        appender = null;
    }

    public synchronized void flush() {
    }

    public synchronized void write(int val) {
        oneByte[0] = (byte) val;
        write(oneByte, 0, 1);
    }

    public synchronized void write(byte[] ba) {
        write(ba, 0, ba.length);
    }

    public synchronized void write(byte[] ba, int str, int len) {
        if (appender != null) {
            appender.append(bytesToString(ba, str, len));
        }
//        textArea.append(bytesToString(ba, str, len));
//        textArea.update(textArea.getGraphics());
//        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    static private String bytesToString(byte[] ba, int str, int len) {
        return new String(ba, str, len, StandardCharsets.UTF_8);
    }


    static class Appender implements Runnable {

        private static final String EOL1 = "\n";
        private static final String EOL2 = System.getProperty("line.separator", EOL1);

        private final JTextArea textArea;
        private final int maxLines; // maximum lines allowed in text area
        private final LinkedList<Integer> lengths; // length of lines within text area
        private final List<String> values; // values waiting to be appended

        private int curLength; // length of current line
        private boolean clear;
        private boolean queue;

        Appender(JTextArea txtara, int maxlin) {
            textArea = txtara;
            maxLines = maxlin;
            lengths = new LinkedList<>();
            values = new ArrayList<>();

            curLength = 0;
            clear = false;
            queue = true;
        }

        synchronized void append(String val) {
            values.add(val);
            if (queue) {
                queue = false;
                EventQueue.invokeLater(this);
            }
        }

        synchronized void clear() {
            clear = true;
            curLength = 0;
            lengths.clear();
            values.clear();
            if (queue) {
                queue = false;
                EventQueue.invokeLater(this);
            }
        }

        // the only method to interact with JTextArea
        public synchronized void run() {
            if (clear) {
                textArea.setText("");
            }
            for (String val : values) {
                curLength += val.length();
                if (val.endsWith(EOL1) || val.endsWith(EOL2)) {
                    if (lengths.size() >= maxLines) {
                        textArea.replaceRange("", 0, lengths.removeFirst());
                    }
                    lengths.addLast(curLength);
                    curLength = 0;
                }
                textArea.append(val);
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
            values.clear();
            clear = false;
            queue = true;
        }

    }

}