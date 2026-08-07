import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean match = encoder.matches("password123", "$2a$10$Ew.HlZ5uT13JzB5O92TDbOsP5p6/wT/fGqYwV.T3C8Wn23Y3r2bRy");
        System.out.println("Match: " + match);
    }
}
