import cn.mpy634.annotion.BetterBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author LEO D PEN
 * @date 2021/2/8
 * @desc
 */

//@Builder
//@Setter
@BetterBuilder(fluentGet = false, fluentSet = false)
@Data
//@Builder
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
