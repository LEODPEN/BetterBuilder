import cn.mpy634.constant.StrConstant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author LEO D PEN
 * @date 2021/2/12
 * @desc
 */
@DisplayName("no builder test")
class NoBuilderTest {

    private static NoBuilder nbt;

    @BeforeAll
    public static void init() {
        nbt = new NoBuilder();
    }

    @Test
    public void there_should_be_no_builder_test() {
        Set<String> mn = Arrays.stream(nbt.getClass().getMethods())
                .map(Method::getName)
                .collect(Collectors.toSet());
        assertFalse(mn.contains(StrConstant.BUILDER));
    }

}
