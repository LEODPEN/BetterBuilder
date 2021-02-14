import cn.mpy634.annotation.BetterBuilder;
import cn.mpy634.enums.BuilderType;

/**
 * @author LEO D PEN
 * @date 2021/2/11
 * @desc 综合测试
 */
@BetterBuilder(BUILDER_TYPE = BuilderType.CLASSIC)
public class Student {
    private String name;
    private Integer ID;
}
