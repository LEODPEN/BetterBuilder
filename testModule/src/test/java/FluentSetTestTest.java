import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.AllPermission;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author LEO D PEN
 * @date 2021/2/7
 * @desc
 */
@DisplayName("test for fluentSet")
class FluentSetTestTest {

    private static FluentSetTest fst;

    @BeforeAll
    public static void init() {
        fst = new FluentSetTest(0L, null, 10);
    }

    // Long test
    @Test
    public void testId() {
        fst.id(1L);
        assertEquals(1L, fst.id);
    }

    // String test
    @Test
    public void testName() {
        fst.name("德玛西亚");
        System.out.println(fst.name);
        assertTrue(fst.name != null);
    }

}
