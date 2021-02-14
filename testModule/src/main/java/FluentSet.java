import cn.mpy634.annotation.BetterBuilder;
import cn.mpy634.enums.BuilderType;

/**
 * @author LEO D PEN
 * @date 2021/2/6
 * @desc first test: to test the fluentSet.
 */
@BetterBuilder(fluentSet = true, fluentGet = false, BUILDER_TYPE = BuilderType.NO_BUILDER)
public class FluentSet {

    // 改为public是为测试需要
    public Long id;

    public String name;

    public Integer num;

    public FluentSet() {}

    public FluentSet(Long id, String name, Integer num) {
        this.id = id;
        this.name = name;
        this.num = num;
    }

}
