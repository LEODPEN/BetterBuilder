import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;


import static org.junit.jupiter.api.Assertions.*;
/**
 * @author LEO D PEN
 * @date 2021/2/7
 * @desc
 */

@DisplayName("test for fluentGet")
public class FluentGetTestTest {

    private static FluentGetTest fgt;

    @BeforeAll
    public static void init() {
        fgt =  new FluentGetTest(0, 0,"诺克萨斯", new FluentSetTest());
    }

    @Test
    public void testBoxedNumAndPNum() {
        System.out.println(fgt.boxedNum());
        assertEquals(fgt.boxedNum(), 0);
        assertEquals(fgt.primitiveNum(), 0);
    }

    @Test
    public void testFST() {
        FluentSetTest fst = fgt.fluentSetTest();
        assertTrue(StringUtils.isBlank(fst.name));
        String name = "this is fst";
        fst.name(name);
        assertEquals(name, fgt.fluentSetTest().name);
    }
}
