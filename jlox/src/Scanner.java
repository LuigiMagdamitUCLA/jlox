import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mypackage.TokenType;
import static mypackage.TokenType.*;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    // keeps track of where the scanner is in the source code
    Scanner(String source) {
        this.source = source;
    }
    List<Token> scanTokens() {
        while(!isAtEnd()) {
            // we are at the start of the next lexeme
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        //EOF token
        return tokens;
    }
    private boolean isAtEnd() {
        return current >= source.length();
    }

    // at each loop, scan every token - in this case below, start with single character tokens
    private void scanToken() {
        char c = advance();
        switch(c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if(match('/')) {
                    //comment case
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;
            // STRING LITERALS
            case '"': string(); break;
            // NUMBER LITERALS
            default:
                if (isDigit(c)) {
                    number();
                } else {
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }
    private void number() {
        while (isDigit(peek())) advance(); // while there is a digit in peek, then advance char

        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) advance();
        }
        addToken(NUMBER,
            Double.parseDouble(source.substring(start, current)));
    }
    private void string() {
        while(peek() != '"' && !isAtEnd()) {
            if(peek() == '\n') line++;
            advance();
            // this loop will find the end
            // start and current are equal, but 
            // current will diverge to the end
        }
        if(isAtEnd()) {
            Lox.error(line, "unterminated string.");
            return;
        }
        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }
    //using this fsunction, we recognize the lexeme in two stages
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        // if there is no expected character, don't advance
        current++;
        return true;
    }
    private char advance() {
        current++;
        return source.charAt(current-1);
    }
    private void addToken(TokenType type) {
        addToken(type, null);
    }
    private void addToken(TokenType type, Object literal){
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
   } 
   private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
        // peeks ahead of the peek var, in cases of decimals
   }
   private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
   }
}

