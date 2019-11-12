package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.PersonClassification;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

public class AdminPersonClassificationController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtName;
    private UtilsEJB utilsEJB = null;
    private PersonClassification  personclassificationParam;
    private Button btnSave;
    private Integer evenType2 = -1;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        personclassificationParam = (Sessions.getCurrent().getAttribute("object") != null) ? (PersonClassification) Sessions.getCurrent().getAttribute("object") : null;
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
    
        txtName.setRawValue(null);
    }

    private void loadFields(PersonClassification personclassification) {
        try {txtName.setText(personclassification.getDescription());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
       
        txtName.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
            return  false;
        }
         
        return true;

    }


    private void savePersonClassification(PersonClassification personclassification_) {
        try {
            PersonClassification personclassification = null;

            if (personclassification_ != null) {
                personclassification = personclassification_;
            } else {//New requestType
                personclassification = new PersonClassification();
            }
            personclassification.setDescription(txtName.getText());
            personclassification = utilsEJB.savePersonClassification(personclassification);
            personclassificationParam = personclassification;
            this.showMessage("sp.common.save.success", false, null);
        } catch ( Exception ex) {
            showError(ex);
        }

    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (evenType2) {
                case WebConstants.EVENT_ADD:
                    savePersonClassification(null);
                break;
                case WebConstants.EVENT_EDIT:
                   savePersonClassification(personclassificationParam);
                break;
            }
        }
    }

    public void loadData() {
        switch (evenType2) {
            case WebConstants.EVENT_EDIT:
                loadFields(personclassificationParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(personclassificationParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }

 
    

}
