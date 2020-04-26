package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.ApprovalGeneralRate;
import com.cms.commons.models.ApprovalProgramRate;
import com.cms.commons.models.Product;
import com.cms.commons.models.Program;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class AdminActivationProductController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private ProductEJB productEJB = null;
    private Label lblProduct;
    private Label lblAgency;
    private Label lblUserActivation;
    private Label lblIdentification;
    private Datebox dtbActivationDate;
    private Radio rActivationYes;
    private Radio rActivationNo;
    private Textbox txtObservations;
    private User user = null;
    private Product productParam;
    private Button btnSave;
    public Window winAdminActivationProduct;
    private Program program;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            productParam = null;                    
        } else {
            productParam = (Product) Sessions.getCurrent().getAttribute("object");            
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            user = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
//            loadData();
            this.clearMessage();
        } catch (Exception ex) {
            showError(ex);
        } finally {
//            loadData();
        }
    }

    public void clearFields() {
        dtbActivationDate.setRawValue(null);
    }

    private void loadFields(Product product) throws EmptyListException, GeneralException, NullParameterException {
        try {
            product = (Product) session.getAttribute(WebConstants.PROGRAM);
            lblProduct.setValue(program.getName());
            lblAgency.setValue(user.getComercialAgencyId().getCityId().getName());
            lblUserActivation.setValue(user.getFirstNames() + " " + user.getLastNames());
            lblIdentification.setValue(user.getIdentificationNumber());
//            dtbActivationDate.setValue(oroduct.getApprovalDate());
//            if (oroduct.getIndApproved() != null) {
//                if (oroduct.getIndApproved() == true) {
//                    rActivationYes.setChecked(true);    
//                } else {
//                    rActivationNo.setChecked(true);
//                }
//            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        rActivationYes.setDisabled(true);
        rActivationNo.setDisabled(true);        
        btnSave.setVisible(false);
    }
////
////    public Boolean validateEmpty() {
////        if (txtActivationDate.getText().isEmpty()) {
////            txtActivationDate.setFocus(true);
////            this.showMessage("sp.error.field.cannotNull", true, null);
////        } else {
////            return true;
////        }
////        return false;
////    }
//
//    private void saveApprovalRates(ApprovalProgramRate _approvalProgramRate) {
//        ApprovalProgramRate approvalProgramRate = null;
//        boolean indApproved;
//        try {
//            if (_approvalProgramRate != null) {
//                approvalProgramRate = _approvalProgramRate;
//            } else {
//                approvalProgramRate = new ApprovalProgramRate();
//            }
//            
//            if (rActivationYes.isChecked()) {
////                indActivation = true;
//            } else {
////                indActivation = false;
//            }
//            
//            //Guarda la aprobación de las tarifas por programa
//            approvalProgramRate.setProgramId(program);
//            approvalProgramRate.setApprovalDate(txtApprovalDate.getValue());
//            approvalProgramRate.setIndApproved(indApproved);
//            approvalProgramRate.setUserId(user);
//            approvalProgramRate.setCreateDate(new Timestamp(new Date().getTime()));
//            approvalProgramRate = productEJB.saveApprovalProgramRate(approvalProgramRate);
//            this.showMessage("sp.common.save.success", false, null);
//            EventQueues.lookup("updateApprovalProgramRate", EventQueues.APPLICATION, true).publish(new Event(""));
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
//
//    public void onClick$btnSave() {
//        if (validateEmpty()) {
//            switch (eventType) {
//                case WebConstants.EVENT_ADD:
//                    saveApprovalRates(null);
//                    break;
//                case WebConstants.EVENT_EDIT:
//                    saveApprovalRates(approvalProgramRateParam);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//    
//    public void onClick$btnBack() {
//        winAdminApprovalProgramRate.detach();
//    }
//
    public void loadData() {
//        try {
            switch (eventType) {
//                case WebConstants.EVENT_EDIT:
//                    loadFields(approvalProgramRateParam);
//                break;
//                case WebConstants.EVENT_VIEW:
//                    loadFields(approvalProgramRateParam);
//                    blockFields();
//                break;
//                case WebConstants.EVENT_ADD:
//                    lblProgram.setValue(program.getName());
//                    txtCity.setValue(user.getComercialAgencyId().getCityId().getName());
//                    txtAgency.setValue(user.getComercialAgencyId().getName());
//                    txtCommercialAssessorUserCode.setValue(user.getCode());
//                    txtAssessorName.setValue(user.getFirstNames() + " " + user.getLastNames());
//                    txtIdentification.setValue(user.getIdentificationNumber());
//                break;
            }
//        } catch (EmptyListException ex) {
//            showError(ex);
//        } catch (GeneralException ex) {
//            showError(ex);
//        } catch (NullParameterException ex) {
//            showError(ex);
        }
    }
    
//}