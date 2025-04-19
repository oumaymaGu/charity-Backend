package tn.example.charity.Service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
@Service
public class BadWordsService {
    private final List<String> forbiddenWords = Arrays.asList("insulte1", "motinterdit", "spam", "israel");

    public boolean containsForbiddenWords(String text) {
        if (text == null) return false;
        String lowercase = text.toLowerCase();
        return forbiddenWords.stream().anyMatch(lowercase::contains);
    }
}
