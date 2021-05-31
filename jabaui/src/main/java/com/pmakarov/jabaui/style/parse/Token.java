package com.pmakarov.jabaui.style.parse;

import lombok.Getter;
import lombok.Setter;

/**
 * @author pmakarov
 */
@Getter
@Setter
public class Token {
    private TokenType type;
    private String value;
    private Position position;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
        this.position = new Position();
    }

    @Override
    public String toString() {
        return "{ " + type.name() + " | " + value + " | [" + position.toString() + "] }";
    }

    @Getter
    @Setter
    public static class Position {

        private long line;
        private long column;

        public Position(long line, long column) {
            this.line = line;
            this.column = column;
        }

        public Position() {
            this.line = -1;
            this.column = -1;
        }

        @Override
        public String toString() {
            return "line:" + this.getLine() + ", column:" + this.getColumn();
        }
    }
}
