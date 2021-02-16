import java.util.List;

/**
 * @author LEO D PEN
 * @date 2021/2/15
 * @desc
 */
public class Test<H, L>{

    private Integer i;

    private String s;

    public Test(Integer i, String s) {
        this.i = i;
        this.s = s;
    }

    public Test() {
    }

    public Test<PF.REQ, L> setI(Integer i) {
        this.i = i;
        return new Test<>(this.i, this.s);
    }

    public Test<H, PF.REQ> setS(String s) {
        this.s = s;
        return new Test<>(this.i, this.s);
    }

    private static class PF {
        static abstract class REQ extends DEF{
        }

        static abstract class DEF {
        }
    }


    public static void main(String[] args) {
//        Test<Test.DEF, Test.DEF> t = new Test<Test.DEF, Test.DEF>().setS("S").setI(1);
        Test<Test.PF.REQ,? extends Test.PF.DEF> t = new Test<Test.PF.DEF,Test.PF.DEF>().setS("S").setI(1);
        System.out.println(t.getClass());
    }
}
