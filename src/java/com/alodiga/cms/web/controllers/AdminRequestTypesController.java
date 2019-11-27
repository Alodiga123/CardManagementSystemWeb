package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.CardRequestType;
import com.cms.commons.models.Country;
import com.cms.commons.models.Currency;
import com.cms.commons.models.PersonType;
import com.cms.commons.models.ProductType;
import com.cms.commons.models.Program;
import com.cms.commons.models.RequestType;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;

public class AdminRequestTypesController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Combobox cmbProgram;
    private Combobox cmbProductType;
    private Combobox cmbCountry;
    private Combobox cmbPersonType;
    private Combobox cmbCardRequestType;
    private UtilsEJB utilsEJB = null;
    private ProgramEJB programEJB = null;
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
          programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
          loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        
    }

    private void loadFields(RequestType requestType) {
        try {
            //txtDescription.setText(requestType.getDescription());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        return null;
    }

    private void saveRequestType(RequestType _requestType) {
        try {
            RequestType requestType = null;
            if (_requestType != null) {
                requestType = _requestType;
            } else {//New requestType
                requestType = new RequestType();
            }
            requestType.setProgramId((Program) cmbProgram.getSelectedItem().getValue());
            requestType.setProductTypeId((ProductType) cmbProductType.getSelectedItem().getValue());
            requestType.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            requestType.setPersonTypeId((PersonType) cmbPersonType.getSelectedItem().getValue());
            requestType.setCardRequestTypeId((CardRequestType) cmbCardRequestType.getSelectedItem().getValue());
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
                loadCmbProgram(event);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(requestTypeParam);
                loadCmbProgram(event);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbProgram(event);
                break;
            default:
                break;
        }
    }
    
    private void loadCmbProgram(Integer event) {
        EJBRequest request1 = new EJBRequest();
        List<Program> programs;

        try {
            programs = programEJB.getProgram(request1);
            cmbProgram.getItems().clear();
            for (Program p: programs) {
                Comboitem item = new Comboitem();
                item.setValue(p);
                item.setLabel(p.getName());
                item.setParent(cmbProgram);
                if (requestTypeParam != null && p.getId().equals(requestTypeParam.getProgramId().getId())) {
                    cmbProgram.setSelectedItem(item);
                }
            }
            if (event.equals(WebConstants.EVENT_ADD)) {
                cmbProgram.setSelectedIndex(1);
            } if (event.equals(WebConstants.EVENT_VIEW)) {
                cmbProgram.setDisabled(true);
            } 
            //prueba luly
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
            ex.printStackTrace();
        }
    }

}
