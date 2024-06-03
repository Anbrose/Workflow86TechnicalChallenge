package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobustJSONParser {
    private enum TokenType {
        LEFT_BRACE, RIGHT_BRACE, LEFT_BRACKET, RIGHT_BRACKET, COLON, COMMA, STRING, NUMBER, BOOLEAN, NULL, END
    } // END if for handling the final token

    private static class Token {
        TokenType type;
        String value;

        Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    // Lexer is for mapping chars to object
    private static class Lexer {
        private final String input;
        private int pos = 0;
        private final int length;

        Lexer(String input) {
            this.input = input;
            this.length = input.length();
        }

        // Tokenize all parts.
        Token nextToken() {
            while (pos < length) {
                char currentChar = input.charAt(pos);
                switch (currentChar) {
                    case '{':
                        pos++;
                        return new Token(TokenType.LEFT_BRACE, "{");
                    case '}':
                        pos++;
                        return new Token(TokenType.RIGHT_BRACE, "}");
                    case '[':
                        pos++;
                        return new Token(TokenType.LEFT_BRACKET, "[");
                    case ']':
                        pos++;
                        return new Token(TokenType.RIGHT_BRACKET, "]");
                    case ':':
                        pos++;
                        return new Token(TokenType.COLON, ":");
                    case ',':
                        pos++;
                        return new Token(TokenType.COMMA, ",");
                    case '"':
                        return stringToken();
                    case ' ':
                    case '\t':
                    case '\n':
                    case '\r':
                        pos++;
                        break;
                    default:
                        if (Character.isDigit(currentChar) || currentChar == '-') {
                            return numberToken();
                        } else if (Character.isLetter(currentChar)) {
                            return literalToken();
                        } else {
                            throw new RuntimeException("Unexpected character: " + currentChar);
                        }
                }
            }
            return new Token(TokenType.END, "");
        }

        private Token stringToken() {
            StringBuilder sb = new StringBuilder();
            pos++;  // skip opening quote
            while (pos < length && input.charAt(pos) != '"') {
                sb.append(input.charAt(pos));
                pos++;
            }
            pos++;  // skip closing quote
            return new Token(TokenType.STRING, sb.toString());
        }

        private Token numberToken() {
            StringBuilder sb = new StringBuilder();
            while (pos < length && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
                sb.append(input.charAt(pos));
                pos++;
            }
            return new Token(TokenType.NUMBER, sb.toString());
        }

        private Token literalToken() {
            StringBuilder sb = new StringBuilder();
            while (pos < length && Character.isLetter(input.charAt(pos))) {
                sb.append(input.charAt(pos));
                pos++;
            }
            String value = sb.toString();
            switch (value) {
                case "true":
                case "false":
                    return new Token(TokenType.BOOLEAN, value);
                case "null":
                    return new Token(TokenType.NULL, value);
                default:
                    System.out.println(input.substring(0, pos));
                    throw new RuntimeException("Unexpected literal: " + value);
            }
        }
    }

    private final Lexer lexer;
    private Token currentToken;

    public RobustJSONParser(String input) {
        lexer = new Lexer(input);
        // inited pos at 0
        currentToken = lexer.nextToken();
    }

    private void nextToken() {
        currentToken = lexer.nextToken();
    }

    public Map<String, Object> parse() {
        if (currentToken.type == TokenType.LEFT_BRACE) {
            return parseObject();
        } else {
            throw new RuntimeException("JSON format should be started with {");
        }
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> object = new HashMap<>();
        nextToken(); // skip the first '{'
        while (currentToken.type != TokenType.RIGHT_BRACE && currentToken.type != TokenType.END) {
            if (currentToken.type != TokenType.STRING) {
                throw new RuntimeException("Expected string for key but got: " + currentToken.value);
            }
            String key = currentToken.value;
            nextToken(); // skip key
            if (currentToken.type != TokenType.COLON) {
                throw new RuntimeException("Expected ':' but got: " + currentToken.value);
            }
            nextToken(); // skip ':'
            Object value = parseValue();
            object.put(key, value);
            if (currentToken.type == TokenType.COMMA) {
                nextToken(); // skip ','
            }
        }
        nextToken(); // skip '}' or reach END
        return object;
    }

    private List<Object> parseArray() {
        List<Object> array = new ArrayList<>();
        nextToken(); // skip '['
        while (currentToken.type != TokenType.RIGHT_BRACKET && currentToken.type != TokenType.END) {
            array.add(parseValue());
            if (currentToken.type == TokenType.COMMA) {
                nextToken(); // skip ','
            }
        }
        nextToken(); // skip ']' or reach END
        return array;
    }

    private Object parseValue() {
        switch (currentToken.type) {
            case STRING:
                String stringValue = currentToken.value;
                nextToken();
                return stringValue;
            case LEFT_BRACE:
                return parseObject();
            case LEFT_BRACKET:
                return parseArray();
            case NUMBER:
                double numberValue = Double.parseDouble(currentToken.value);
                nextToken();
                return numberValue;
            case BOOLEAN:
                // for handling {"key": true/false}
                boolean booleanValue = Boolean.parseBoolean(currentToken.value);
                nextToken();
                return booleanValue;
            case NULL:
                // for handling {"key":""} should be deprecated since existing format is
                // valid up to the pos of imcompletion
                nextToken();
                return null;
            case END:
                //  for handling {"key": <end>
                return null;
            default:
                throw new RuntimeException("Unexpected token: " + currentToken.value);
        }
    }
}
