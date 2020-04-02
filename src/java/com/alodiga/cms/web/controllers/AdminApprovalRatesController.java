package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.ApprovalGeneralRate;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.sql.Timestamp;
import java.util.Date;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Window;

public class AdminApprovalRatesController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;

    private Label txtCity;
    private Label txtAgency;
    private Label txtCommercialAssessorUserCode;
    private Label txtAssessorName;
    private Label txtIdentification;
    private Datebox txtApprovalDate;
    private Radio rApprovedYes;
    private Radio rApprovedNo;
    private ProductEJB productEJB = null;
    private User user = null;
    private ApprovalGeneralRate approvalGeneralRateParam;
    private Button btnSave;
    public Window winAdminApprovalGeneralRates;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            approvalGeneralRateParam = null;                    
        } else {
            approvalGeneralRateParam = (ApprovalGeneralRate) Sessions.getCurrent().getAttribute("object");            
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            user = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            loadData();
            this.clearMessage();
        } catch (Exception ex) {
            showError(ex);
        } finally {
            loadData();
        }
    }

    public void clearFields() {
        txtApprovalDate.setRawValue(null);
    }

    private void loadFields(ApprovalGeneralRate approvalGeneralRate) throws EmptyListException, GeneralException, NullParameterException {
        try {
            txtCity.setValue(user.getComercialAgencyId().getCityId().getName());
            txtAgency.setValue(user.getComercialAgencyId().getName());
            txtCommercialAssessorUserCode.setValue(user.getCode());
            txtAssessorName.setValue(user.getFirstNames() + " " + user.getLastNames());
            txtIdentification.setValue(user.getIdentificationNumber());
            txtApprovalDate.setValue(approvalGeneralRate.getApprovalDate());
            if (approvalGeneralRate.getIndApproved() != null) {
                if (approvalGeneralRate.getIndApproved() == true) {
                    rApprovedYes.setChecked(true);    
                } else {
                    rApprovedNo.setChecked(true);
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtApprovalDate.setDisabled(true);
        rApprovedYes.setDisabled(true);
        rApprovedNo.setDisabled(true);        
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtApprovalDate.getText().isEmpty()) {
            txtApprovalDate.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveApprovalRates(ApprovalGeneralRate _approvalGeneralRate) {
        ApprovalGeneralRate approvalGeneralRate = null;
        boolean indApproved;
        try {
            if (_approvalGeneralRate != null) {
                approvalGeneralRate = _approvalGeneralRate;
            } else {
                approvalGeneralRate = new ApprovalGeneralRate();
            }
            
            if (rApprovedYes.isChecked()) {
                indApproved = true;
            } else {
                indApproved = false;
            }
            
            //Guarda la aprobación de las tarifas generales
            approvalGeneralRate.setApprovalDate(txtApprovalDate.getValue());
            approvalGeneralRate.setIndApproved(indApproved);
            approvalGeneralRate.setUserId(user);
            approvalGeneralRate.setCreateDate(new Timestamp(new Date().getTime()));
            approvalGeneralRate = productEJB.saveApprovalGeneralRate(approvalGeneralRate);
            this.showMessage("sp.common.save.success", false, null);
            EventQueues.lookup("updateApprovalGeneralRate", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveApprovalRates(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveApprovalRates(approvalGeneralRateParam);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void onClick$btnBack() {
        winAdminApprovalGeneralRates.detach();
    }

    public void loadData() {
        try {
            switch (eventType) {
                case WebConstants.EVENT_EDIT:
                    loadFields(approvalGeneralRateParam);
                break;
                case WebConstants.EVENT_VIEW:
                    loadFields(approvalGeneralRateParam);
                    blockFields();
                break;
                case WebConstants.EVENT_ADD:
                    txtCity.setValue(user.getComercialAgencyId().getCityId().getName());
                    txtAgency.setValue(user.getComercialAgencyId().getName());
                    txtCommercialAssessorUserCode.setValue(user.getCode());
                    txtAssessorName.setValue(user.getFirstNames() + " " + user.getLastNames());
                    txtIdentification.setValue(user.getIdentificationNumber());
                break;
            }
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }
    
}