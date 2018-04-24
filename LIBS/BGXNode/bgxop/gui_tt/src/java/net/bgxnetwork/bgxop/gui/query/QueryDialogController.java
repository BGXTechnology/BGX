package net.bgx.bgxnetwork.bgxop.gui.query;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import net.bgx.bgxnetwork.bgxop.gui.ProfilePanelController;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.QueryStatus;

/**
 * Class QueryDialogController
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class QueryDialogController{
    private QueryDialog view;
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui_query");
    protected ProfilePanelController profileController;
    public QueryDialogController(ProfilePanelController profileController){
        this.profileController = profileController;
    }
    protected void setView(QueryDialog view){
        this.view = view;
    }
    public QueryDialog getView(){
        return view;
    }

    public void create(Query query){
        try {
            if (profileController.create(query))
              view.close();
        } catch (Exception e) {
            showClientErrorMessage(e);
        }
    }

    public void createAndExecute(Query query){
        try {
            profileController.checkQueryName(query);
            profileController.createAndExecute(query);
            view.close();
        } catch (Exception e) {
            showClientErrorMessage(e);
        }
    }

    private void showClientErrorMessage(Exception e){
        MessageDialogs.generalError(view, e, rb.getString("QueryDialogController.internalErrorMsg"), rb
                .getString("QueryDialogController.internalErrorTitle"));
    }
}
