package tn.example.charity.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.stripe.Stripe;
import javax.annotation.PostConstruct;

@Configuration
public class StripeConfig {

    @Value("${stripe.api.secretKey}")
    private String secretKey;

    @Value("${stripe.api.publicKey}")
    private String publicKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }

    public String getPublicKey() {
        return publicKey;
    }
}