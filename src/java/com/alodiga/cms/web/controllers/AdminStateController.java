package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.State;
import com.cms.commons.models.Country;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

public class AdminStateController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtSimbol,txtName;
    private UtilsEJB utilsEJB = null;
    private State stateParam;
    private Button btnSave;
    private Integer evenType2 = -1;
            
            


    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        stateParam = (Sessions.getCurrent().getAttribute("object") != null) ? (State) Sessions.getCurrent().getAttribute("object") : null;
        evenType2 = (Integer) (Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE));
        initialize();
        loadData();
//      initView(eventType, "sp.crud.requestType");
    }

//    @Override
//    public void initView(int eventType, String adminView) {
//        super.initView(eventType, "sp.crud.requestType");
//    }
    @Override
    public void initialize() {
        super.initialize();
        try {

            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtSimbol.setRawValue(null);
        txtName.setRawValue(null);
    }

    private void loadFields(State state) {
        try {
         //  txtSimbol.setText(state.getCountryId());
            txtName.setText(state.getName());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtSimbol.setReadonly(true);
        txtName.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        }
        if (txtSimbol.getText().isEmpty()) {
            txtSimbol.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;

    }


    private void saveState(State state_) {
        try {
            State state = null;

            if (state_ != null) {
                state = state_;
            } else {//New requestType
                state = new State();
            }
            state.setName(txtName.getText());
           // state.setCountryId(countryId);
         //   state.setSymbol(txtSimbol.getText());
            state = utilsEJB.saveState(state);
            stateParam = state;
            this.showMessage("sp.common.save.success", false, null);
        } catch ( Exception ex) {
            showError(ex);
        }

    }

    

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (evenType2) {
                case WebConstants.EVENT_ADD:
                    saveState(null);
                break;
                case WebConstants.EVENT_EDIT:
                   saveState(stateParam);
                break;
            }
        }
    }

    public void loadData() {
        switch (evenType2) {
            case WebConstants.EVENT_EDIT:
                loadFields(stateParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(stateParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }

}
