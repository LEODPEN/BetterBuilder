import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author LEO D PEN
 * @date 2021/2/16
 * @desc
 */
@DisplayName("TypeSafeTest")
class TypeSafeTest {

    private TypeSafe ts1, ts2;

    @Test
    public void test_type_safe() throws IllegalArgumentException {
        ts1 = TypeSafe.builder()
                .ID(1)
                .name("PF")
                .PID(true)
                .build();
        assertEquals(1, ts1.ID());
        assertThrows(IllegalArgumentException.class,
                () -> {
                    ts2 = TypeSafe.builder()
                            .ID(1)
//                        .name("PF")
                            .PID(true)
                            .build();
                } );
    }

}
