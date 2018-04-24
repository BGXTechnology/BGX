package net.bgx.bgxnetwork.bgxop.gui.background;
import java.awt.Window;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;
import javax.ejb.EJBAccessException;

import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.bgxop.uitools.WaitDialog;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import net.bgx.bgxnetwork.exception.query.QueryEJBException;
import net.bgx.bgxnetwork.exception.query.QueryDataException;

/**
 * Class ServerThread
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public abstract class ServerThread extends Thread{
    protected WaitDialog dialog;
    protected Exception ex;
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");

    public ServerThread(WaitDialog dialog){
        this.dialog = dialog;
    }

    public Exception getException(){
        return ex;
    }

//    protected void doNetworkOperation(Runnable worker) throws InterruptedException {
    protected void doNetworkOperation(AbstractNetworkWorker worker) throws InterruptedException {
        long workerCheckInterval = 50;
        Thread workerThread = new Thread(worker);
        workerThread.start();
        while ( workerThread.getState() != Thread.State.TERMINATED ) {
            sleep(workerCheckInterval);
            if (isInterrupted()) {
                System.out.println("ServerThread.doNetworkOperation(): SERVER THREAD IS INTERRUPTED");
                //workerThread.stop();
                throw new InterruptedException();
            }
        }
    }
    
    protected void finish(){
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                Window owner = dialog.getOwner();
                dialog.close();
                if(ex != null){
                    if(ex instanceof QueryBusinesException){
                        if(((QueryBusinesException) ex).getErrorCode().equals(ErrorList.BUSINES_ROLE_PERMISSION_EXCEPTION)){
                            MessageDialogs.warning(owner, rb.getString("message.error.info.noPermissions")+"\n"
                              +rb.getString("message.error.info.ascInfo"), rb.getString("message.error.info.title"));
                        }
                        else{
                            MessageDialogs.generalError(owner, ex, rb.getString("error.incorrectQueryData"), rb.getString("error.commonTitle"));
                        }
                    }
                    else if(ex instanceof QueryDataException || ex instanceof QueryEJBException){
                        MessageDialogs.generalError(owner, ex, rb.getString("error.incorrectQueryData"), rb.getString("error.commonTitle"));
                    }
                    else if(ex instanceof EJBAccessException || ex.getCause() instanceof SecurityException){
                      MessageDialogs.warning(owner, rb.getString("message.error.info.noPermissions")+"\n"
                              +rb.getString("message.error.info.ascInfo"), rb.getString("message.error.info.title"));
                    }
                    else{
                        MessageDialogs.generalServerError(owner, ex);
                    }
                }
            }
        });
    }

    public abstract Object getResult();

    public abstract class AbstractNetworkWorker<T> implements Runnable {
        protected T result = null;
        public T getResult() {
            return result;
        }
    }
}