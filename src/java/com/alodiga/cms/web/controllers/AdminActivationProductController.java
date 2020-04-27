package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
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
    private UtilsEJB utilsEJB = null;
    private ProgramEJB programEJB = null;
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
    private AdminProductController adminProduct = null;
    
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        adminProduct = new AdminProductController();
        productParam = adminProduct.getProductParent();
        eventType = adminProduct.getEventType();
        if (eventType == WebConstants.EVENT_ADD) {
            productParam = null;
        } else {
            if (productParam.getIndActivation() == null) {
                productParam = null;
            }            
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
        dtbActivationDate.setRawValue(null);
    }

    private void loadFields(Product product) throws EmptyListException, GeneralException, NullParameterException {
        try {
            if (product != null ) {
                lblProduct.setValue(product.getName());
                lblAgency.setValue(product.getUserActivationId().getComercialAgencyId().getCityId().getName());
                lblUserActivation.setValue(product.getUserActivationId().getFirstNames() + " " + product.getUserActivationId().getLastNames());
                lblIdentification.setValue(product.getUserActivationId().getIdentificationNumber());
                dtbActivationDate.setValue(product.getActivationDate());
                txtObservations.setValue(product.getObservations().toString());
                if (product.getIndActivation() != null) {
                    if (product.getIndActivation() == true) {
                        rActivationYes.setChecked(true);    
                    } else {
                        rActivationNo.setChecked(true);
                    }
                }
            } else {
                lblProduct.setValue(productParam.getName());
                lblAgency.setValue(user.getComercialAgencyId().getName());
                lblUserActivation.setValue(user.getFirstNames() + " " + user.getLastNames());
                lblIdentification.setValue(user.getIdentificationNumber());
            }
            
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        rActivationYes.setDisabled(true);
        rActivationNo.setDisabled(true);        
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (dtbActivationDate.getText().isEmpty()) {
            dtbActivationDate.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveProduct(Product _product) {
        Product product= null;
        boolean indActivation;
        try {
            if (_product != null) {
                product = _product;
            } else {
                product = new Product();
            }
            
            if (rActivationYes.isChecked()) {
                indActivation = true;
            } else {
                indActivation = false;
            }
            
            //Guarda la activación de las tarjetas
            product.setName(lblProduct.getValue());
            product.setUserActivationId(user);   
            product.setActivationDate(dtbActivationDate.getValue());
            product.setIndActivation(indActivation);
            product.setObservations(txtObservations.getText().toString());
            product.setCreateDate(new Timestamp(new Date().getTime()));
            product = productEJB.saveProduct(product);
            this.showMessage("sp.common.save.success", false, null);
            EventQueues.lookup("updateApprovalProgramRate", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveProduct(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveProduct(productParam);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void onClick$btnBack() {
        winAdminActivationProduct.detach();
    }

    public void loadData() {
        try {
            switch (eventType) {
                case WebConstants.EVENT_EDIT:                    
                    loadFields(productParam);
                break;
                case WebConstants.EVENT_VIEW:
                    loadFields(productParam);
                    blockFields();
                break;
                case WebConstants.EVENT_ADD:
                    lblProduct.setValue(productParam.getName());
                    lblAgency.setValue(user.getComercialAgencyId().getName());
                    lblUserActivation.setValue(user.getFirstNames() + " " + user.getLastNames());
                    lblIdentification.setValue(user.getIdentificationNumber());
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