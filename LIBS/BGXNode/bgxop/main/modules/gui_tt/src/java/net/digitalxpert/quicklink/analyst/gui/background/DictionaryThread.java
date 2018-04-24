package net.bgx.bgxnetwork.bgxop.gui.background;

import net.bgx.bgxnetwork.bgxop.gui.query.dictionary.Loader;
import net.bgx.bgxnetwork.bgxop.services.QueryServiceDelegator;

import java.util.ArrayList;

/**
 * Date: 06.03.2007
 * Time: 15:00:03
 */
public class DictionaryThread extends Thread {
  private int mode = -1;

  public DictionaryThread(int mode) {
    this.mode = mode;
  }

  public void run(){
    switch(mode){
      case Loader.INN_MASK :
//        QueryServiceDelegator service = new QueryServiceDelegator();
//        try {
//            ArrayList<String> inn = service.getInnForDictionary();
//            Loader.getInstance().set_requestList(inn);
//        }
//        catch (Exception e){
//             //do not trace this exception
//            //System.out.println("Can't load inn dictionary:"+e);
//          ;
//        }

        break;
    }
  }
}
