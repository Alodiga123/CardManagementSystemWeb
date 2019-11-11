package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.CardStatus;
import com.cms.commons.models.Currency;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

public class AdminCardStatusControllers extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtName;
    private UtilsEJB utilsEJB = null;
    private CardStatus  cardStatusParam;
    private Button btnSave;
    private Integer evenType2 = -1;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        
        cardStatusParam = (Sessions.getCurrent().getAttribute("object") != null) ? (CardStatus) Sessions.getCurrent().getAttribute("object") : null;
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

    private void loadFields(CardStatus cardStatus) {
        try {txtName.setText(cardStatus.getDescription());
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


    private void saveCardStatus(CardStatus cardStatus_) {
        try {
            CardStatus cardStatus = null;

            if (cardStatus_ != null) {
                cardStatus = cardStatus_;
            } else {//New requestType
                cardStatus = new CardStatus();
            }
            cardStatus.setDescription(txtName.getText());
            cardStatus = utilsEJB.saveCardStatus(cardStatus);
            cardStatusParam = cardStatus;
            this.showMessage("sp.common.save.success", false, null);
        } catch ( Exception ex) {
            showError(ex);
        }

    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (evenType2) {
                case WebConstants.EVENT_ADD:
                    saveCardStatus(null);
                break;
                case WebConstants.EVENT_EDIT:
                   saveCardStatus(cardStatusParam);
                break;
            }
        }
    }

    public void loadData() {
        switch (evenType2) {
            case WebConstants.EVENT_EDIT:
                loadFields(cardStatusParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(cardStatusParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }

 
    

}
