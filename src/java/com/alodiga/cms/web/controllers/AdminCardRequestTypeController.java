package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.CardRequestType;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

public class AdminCardRequestTypeController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtCode;
    private Textbox txtDescription;
    private UtilsEJB utilsEJB = null;
    private CardRequestType cardRequestTypeParam;
    private Button btnSave;
    private Integer event;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        event = (Integer) Sessions.getCurrent().getAttribute("eventType");
        cardRequestTypeParam = (Sessions.getCurrent().getAttribute("object") != null) ? (CardRequestType) Sessions.getCurrent().getAttribute("object") : null;
        initialize();
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
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtCode.setRawValue(null);
        txtDescription.setRawValue(null);
    }

    private void loadFields(CardRequestType cardRequestType) {
        try {
            txtCode.setText(cardRequestType.getCode());
            txtDescription.setText(cardRequestType.getDescription());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtCode.setReadonly(true);
        txtDescription.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtCode.getText().isEmpty()) {
            txtCode.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtDescription.getText().isEmpty()) {
            txtDescription.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);    
        } else {
            return true;
        }
        return false;
    }

    public void onClick$btnCodes() {
        Executions.getCurrent().sendRedirect("/docs/T-SP-E.164D-2009-PDF-S.pdf", "_blank");
    }

    public void onClick$btnShortNames() {
        Executions.getCurrent().sendRedirect("/docs/countries-abbreviation.pdf", "_blank");
    }

    private void saveCardRequestType(CardRequestType _cardRequestType) {
        try {
            CardRequestType cardRequestType = null;

            if (_cardRequestType != null) {
                cardRequestType = _cardRequestType;
            } else {//New requestType
                cardRequestType = new CardRequestType();
            }
            cardRequestType.setCode(txtCode.getText());
            cardRequestType.setDescription(txtDescription.getText());
            cardRequestType = utilsEJB.saveCardRequestType(cardRequestType);
            cardRequestTypeParam = cardRequestType;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (event) {
                case WebConstants.EVENT_ADD:
                    saveCardRequestType(null);
                break;
                case WebConstants.EVENT_EDIT:
                   saveCardRequestType(cardRequestTypeParam);
                break;
            }
        }
    }

    public void loadData() {
        switch (event) {
            case WebConstants.EVENT_EDIT:
                loadFields(cardRequestTypeParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(cardRequestTypeParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }


}
