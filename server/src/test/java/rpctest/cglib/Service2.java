package rpctest.cglib;

/**
 * @author Liubsyy
 * @date 2024/8/17
 **/
public class Service2{
    private int o;

    public Service2(int o) {
        this.o = o;
    }

    public String action(int i){
        return "ACTION2:"+(i|10-100+o);
    }
}