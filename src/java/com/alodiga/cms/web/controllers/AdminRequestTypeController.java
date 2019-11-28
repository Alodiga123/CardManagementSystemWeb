package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.RequestType;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

/**
 *
 * @author jose
 */
public class AdminRequestTypeController extends GenericAbstractAdminController {
    
    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtCode;
    private Textbox txtDescription;
    private UtilsEJB utilsEJB = null;
    private RequestType requestTypeParam;
    private Button btnSave;
    private Integer event;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        event = (Integer) Sessions.getCurrent().getAttribute("eventType");
        requestTypeParam = (Sessions.getCurrent().getAttribute("object") != null) ? (RequestType) Sessions.getCurrent().getAttribute("object") : null;
        initialize();
    }

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

    private void loadFields(RequestType requestType) {
        try {
            txtCode.setText(requestType.getCode());
            txtDescription.setText(requestType.getDescription());
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

    private void saveRequestType(RequestType _requestType) {
        try {
            RequestType requestType = null;

            if (_requestType != null) {
                requestType = _requestType;
            } else {//New requestType
                requestType = new RequestType();
            }
            requestType.setCode(txtCode.getText());
            requestType.setDescription(txtDescription.getText());
            requestType = utilsEJB.saveRequestType(requestType);
            requestTypeParam = requestType;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (event) {
                case WebConstants.EVENT_ADD:
                    saveRequestType(null);
                break;
                case WebConstants.EVENT_EDIT:
                   saveRequestType(requestTypeParam);
                break;
            }
        }
    }

    public void loadData() {
        switch (event) {
            case WebConstants.EVENT_EDIT:
                loadFields(requestTypeParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(requestTypeParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }


}

