import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author LEO D PEN
 * @date 2021/2/7
 * @desc
 */
@DisplayName("test for fluentSet")
class FluentSetTest {

    private static FluentSet fst;

    @BeforeAll
    public static void init() {
        fst = new FluentSet(0L, null, 10);
    }

    // Long test
    @Test
    public void test_id() {
        fst.id(1L);
        assertEquals(1L, fst.id);
    }

    // String test
    @Test
    public void test_name() {
        fst.name("德玛西亚");
        System.out.println(fst.name);
        assertTrue(fst.name != null);
    }

}
