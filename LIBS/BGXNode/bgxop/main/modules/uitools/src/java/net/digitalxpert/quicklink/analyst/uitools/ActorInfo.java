package net.bgx.bgxnetwork.bgxop.uitools;
import java.util.ArrayList;

public class ActorInfo{
    private static ActorInfo single;
    private ActorInfo(){
    }
    public static ActorInfo getInstance(){
        if(single == null){
            try{
                throw new Exception("You dont login");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return single;
    }

    public boolean permitRulles(ArrayList<String> roles){
        boolean permit = false;
        return permit;
    }
}
