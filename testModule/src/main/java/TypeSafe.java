import cn.mpy634.annotation.*;
import cn.mpy634.enums.BuilderType;

import java.lang.reflect.Type;

/**
 * @author LEO D PEN
 * @date 2021/2/15
 * @desc
 */
@BetterBuilder(BUILDER_TYPE = BuilderType.TYPE_SAFE, fluentSet = false, fluentGet = true)
public class TypeSafe {

    @BetterBuilder.Required
    private Integer ID;

    @BetterBuilder.Required
    private String name;

    private Boolean PID;

    private Long PID79211;

    public TypeSafe(Integer ID, String name) {
        this.ID = ID;
        this.name = name;
    }
}
