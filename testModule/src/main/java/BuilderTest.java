import cn.mpy634.annotation.BetterBuilder;
import cn.mpy634.enums.BuilderType;
import lombok.Data;

/**
 * @author LEO D PEN
 * @date 2021/2/8
 * @desc
 */

@BetterBuilder(fluentGet = false, fluentSet = false, BUILDER_TYPE = BuilderType.CLASSIC)
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
