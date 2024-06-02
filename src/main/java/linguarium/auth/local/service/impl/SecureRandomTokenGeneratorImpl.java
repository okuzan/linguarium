package linguarium.auth.local.service.impl;

import java.security.SecureRandom;
import linguarium.auth.local.service.TokenGenerator;
import org.springframework.stereotype.Service;

@Service
public class SecureRandomTokenGeneratorImpl implements TokenGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateOTP(int length) {
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            codeBuilder.append(secureRandom.nextInt(10));
        }
        return codeBuilder.toString();
    }
}
