package tn.example.charity.Service;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PasswordValidatorService {
	private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

	private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

	public boolean validatePassword(String password) {
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}

}
