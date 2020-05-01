package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import static com.alodiga.cms.web.controllers.ListRateByProgramController.program;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.ApprovalGeneralRate;
import com.cms.commons.models.ApprovalProductRate;
import com.cms.commons.models.ApprovalProgramRate;
import com.cms.commons.models.Product;
import com.cms.commons.models.Program;
import com.cms.commons.models.RateByProduct;
import com.cms.commons.models.RateByProgram;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Window;

public class AdminApprovalProductRateController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblProgram;
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
    private ApprovalProductRate approvalProductRateParam;
    private Button btnSave;
    public Window winAdminApprovalProductRate;
    private Program program;
    private List<RateByProduct> rateByProductByProductList = new ArrayList<RateByProduct>();
    private Product product;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            approvalProductRateParam = null;                    
        } else {
            approvalProductRateParam = (ApprovalProductRate) Sessions.getCurrent().getAttribute("object");            
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            user = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            product = (Product) session.getAttribute(WebConstants.PRODUCT);
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

    private void loadFields(ApprovalProductRate approvalProductRate) throws EmptyListException, GeneralException, NullParameterException {
        try {
            program = (Program) session.getAttribute(WebConstants.PROGRAM);
            lblProgram.setValue(program.getName());
            txtCity.setValue(approvalProductRate.getUserId().getComercialAgencyId().getCityId().getName());
            txtAgency.setValue(approvalProductRate.getUserId().getComercialAgencyId().getName());
            txtCommercialAssessorUserCode.setValue(approvalProductRate.getUserId().getCode());
            txtAssessorName.setValue(approvalProductRate.getUserId().getFirstNames() + " " + approvalProductRate.getUserId().getLastNames());
            txtIdentification.setValue(approvalProductRate.getUserId().getIdentificationNumber());
            txtApprovalDate.setValue(approvalProductRate.getApprovalDate());
            if (approvalProductRate.getIndApproved() != null) {
                if (approvalProductRate.getIndApproved() == true) {
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

    private void saveApprovalRates(ApprovalProductRate _approvalProductRate) {
        ApprovalProductRate approvalProductRate = null;
        boolean indApproved;
        try {
            if (_approvalProductRate != null) {
                approvalProductRate = _approvalProductRate;
            } else {
                approvalProductRate = new ApprovalProductRate();
            }
            
            if (rApprovedYes.isChecked()) {
                indApproved = true;
            } else {
                indApproved = false;
            }
            
            //Guarda la aprobación de las tarifas por programa
            approvalProductRate.setProductId(product);
            approvalProductRate.setApprovalDate(txtApprovalDate.getValue());
            approvalProductRate.setIndApproved(indApproved);
            approvalProductRate.setUserId(user);
            approvalProductRate.setCreateDate(new Timestamp(new Date().getTime()));
            approvalProductRate = productEJB.saveApprovalProductRate(approvalProductRate);
            
            //Actualiza las tarifas del programa que se está aprobando
            updateProductRate(approvalProductRate);
            
            this.showMessage("sp.common.save.success", false, null);
            EventQueues.lookup("updateApprovalProgramRate", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void updateProductRate(ApprovalProductRate approvalProductRate) {
        try {
            Map params = new HashMap();
            EJBRequest request1 = new EJBRequest();
            params.put(QueryConstants.PARAM_PRODUCT_ID, product.getId());
            request1.setParams(params);
            rateByProductByProductList = productEJB.getRateByProductByProduct(request1);
            for (RateByProduct rateByProduct: rateByProductByProductList) {
                rateByProduct.setApprovalProductRateId(approvalProductRate);
                rateByProduct = productEJB.saveRateByProduct(rateByProduct);
            }
        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
           showError(ex); 
        } catch (GeneralException ex) {
            showError(ex);
        } catch (RegisterNotFoundException ex) {
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
                    saveApprovalRates(approvalProductRateParam);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void onClick$btnBack() {
        winAdminApprovalProductRate.detach();
    }

    public void loadData() {
        try {
            switch (eventType) {
                case WebConstants.EVENT_EDIT:
                    loadFields(approvalProductRateParam);
                break;
                case WebConstants.EVENT_VIEW:
                    loadFields(approvalProductRateParam);
                    blockFields();
                break;
                case WebConstants.EVENT_ADD:
                    lblProgram.setValue(program.getName());
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