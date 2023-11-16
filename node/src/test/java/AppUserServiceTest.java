import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.antisessa.NodeApplication;

import static org.junit.jupiter.api.Assertions.assertTrue;

//@ExtendWith(NodeApplication.class)
@SpringBootTest(classes = NodeApplication.class)
class AppUserServiceTest {

    @Test
    void SomeTest() {
        assertTrue(true);
    }
}