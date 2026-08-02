package com.rabittel.usersservice.services.implementations;

import com.rabittel.usersservice.services.interfaces.PasswordGeneratorService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordGeneratorServiceImpl implements PasswordGeneratorService {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIAL = "@#$%&*!?";

    private static final String CHARACTERS =
            UPPERCASE +
                    LOWERCASE +
                    NUMBERS +
                    SPECIAL;

    private static final int PASSWORD_LENGTH = 10;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateTemporaryPassword() {

        StringBuilder password = new StringBuilder();

        password.append(randomCharacter(UPPERCASE));
        password.append(randomCharacter(LOWERCASE));
        password.append(randomCharacter(NUMBERS));
        password.append(randomCharacter(SPECIAL));

        while (password.length() < PASSWORD_LENGTH) {
            password.append(randomCharacter(CHARACTERS));
        }

        return shuffle(password.toString());
    }

    private char randomCharacter(String characters) {

        return characters.charAt(
                secureRandom.nextInt(characters.length())
        );
    }

    private String shuffle(String value) {

        char[] chars = value.toCharArray();

        for (int i = chars.length - 1; i > 0; i--) {

            int index = secureRandom.nextInt(i + 1);

            char temp = chars[i];
            chars[i] = chars[index];
            chars[index] = temp;
        }

        return new String(chars);
    }
}