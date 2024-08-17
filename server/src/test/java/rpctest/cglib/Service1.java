package rpctest.cglib;

/**
 * @author Liubsyy
 * @date 2024/8/17
 **/
public class Service1{
    private int o;

    public Service1(int o) {
        this.o = o;
    }

    public String action(int i){
        return "ACTION1:"+(i|10-100+o);
    }

}