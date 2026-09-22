import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean match = encoder.matches("123456", "$2a$10$qa5NrjWLAm.8YZoqI/pHY.bLL8vkaN0xm119Mkxfvg5fV0ifh53ze");
        System.out.println("Match: " + match);
        System.out.println("New Hash for 123456: " + encoder.encode("123456"));
    }
}
