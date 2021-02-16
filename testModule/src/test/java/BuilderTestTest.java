import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author LEO D PEN
 * @date 2021/2/11
 * @desc
 */
@DisplayName("test for builder")
class BuilderTestTest {

    private static BuilderTest bt1;

    private static BuilderTest bt2;

    @BeforeAll
    public static void init() {
        bt1 = new BuilderTest("LEO D PEN", 0);
    }

    @Test
    public void testBuilder() throws Exception {
        String name = "LEO D PEN";
        Integer ID = 0;
        bt2 = BuilderTest.builder()
                .name(name)
                .ID(ID)
                .build();
        assertEquals(bt1.getID(), bt2.getID());
        assertEquals(bt1.getName(), bt2.getName());
        System.out.println("bt1 : " + bt1.toString());
        System.out.println("bt2 : " + bt2.toString());
    }

}
