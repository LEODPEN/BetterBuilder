import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author LEO D PEN
 * @date 2021/2/12
 * @desc
 */

@DisplayName("IgnoreGetSetTest")
class IgnoreGetSetTest {

    private static IgnoreGetSet igs;

    private static Set<Method> methods;

    @BeforeAll
    public static void init() {
        igs = new IgnoreGetSet("牛", 1, new Student(), new ArrayList<Boolean>());
        methods = Arrays.stream(igs.getClass().getMethods())
                .collect(Collectors.toSet());
    }

    @Test
    public void 大_test_ignore_get() {
        int cnt = 0;
        for (Method m : methods) {
            if (m.getName().equals("大")) {
                assertEquals(igs.getClass(), m.getReturnType());
                cnt++;
            }
        }
        assertEquals(1, cnt);
    }

    @Test
    public void 牛_test_ignore_set() {
        int cnt = 0;
        for (Method m : methods) {
            if (m.getName().equals("牛")) {
                assertEquals(String.class, m.getReturnType());
                cnt++;
            }
        }
        assertEquals(1, cnt);
    }

    @Test
    public void 年_test_ignore() {
        int cnt = 0;
        for (Method m : methods) {
            if (m.getName().equals("年")) {
                cnt++;
            }
        }
        assertEquals(0, cnt);
    }


}
