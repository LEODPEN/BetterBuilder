import cn.mpy634.annotation.BetterBuilder;
import lombok.Builder;
import lombok.Data;

/**
 * @author LEO D PEN
 * @date 2021/2/8
 * @desc
 */

//@Builder
//@Setter
//@Builder
@BetterBuilder(fluentGet = false, fluentSet = false)
@Data
public class BuilderTest {

    private String name;

    private Integer ID;

//    public BuilderTest(String name, Integer ID) {
//        this.name = name;
//        this.ID = ID;
//    }

    //    private static Integer ss;
//
//    private static final String pd = "11";

}
