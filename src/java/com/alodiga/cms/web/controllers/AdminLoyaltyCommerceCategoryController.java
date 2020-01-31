package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.CommerceCategory;
import com.cms.commons.models.LoyaltyTransactionHasCommerceCategory;
import com.cms.commons.models.ProgramLoyaltyTransaction;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

public class AdminLoyaltyCommerceCategoryController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;

    private Label lblLoyalty;
    private Label lblChannel;
    private Label lblTransaction;
    private Combobox cmbCommerce;
    private ProductEJB productEJB = null;
    private ProgramEJB programEJB = null;
    private LoyaltyTransactionHasCommerceCategory loyaltyTransactionHasCommerceCategoryParam;
    private Button btnSave;
    private Integer eventType;
    public Window winAdminLoyaltyCommerceCategory;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loyaltyTransactionHasCommerceCategoryParam = (LoyaltyTransactionHasCommerceCategory) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_VIEW:
                loyaltyTransactionHasCommerceCategoryParam = (LoyaltyTransactionHasCommerceCategory) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_ADD:
                loyaltyTransactionHasCommerceCategoryParam = null;
                break;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {

    }

    private void loadField(LoyaltyTransactionHasCommerceCategory loyaltyTransactionHasCommerceCategory) {
        lblLoyalty.setValue(loyaltyTransactionHasCommerceCategory.getProgramLoyaltyTransactionId().getProgramLoyaltyId().getDescription());
        lblChannel.setValue(loyaltyTransactionHasCommerceCategory.getProgramLoyaltyTransactionId().getChannelId().getName());
        lblTransaction.setValue(loyaltyTransactionHasCommerceCategory.getProgramLoyaltyTransactionId().getTransactionId().getDescription());
    }
    
    private void loadFields(LoyaltyTransactionHasCommerceCategory loyaltyTransactionHasCommerceCategory) {
        ProgramLoyaltyTransaction programLoyaltyTransaction = null;
        
        AdminParametersController adminParameter = new AdminParametersController();
            if (adminParameter.getProgramLoyaltyTransactionParent().getId() != null) {
                programLoyaltyTransaction = adminParameter.getProgramLoyaltyTransactionParent();
            }
        
        lblLoyalty.setValue(programLoyaltyTransaction.getProgramLoyaltyId().getDescription());
        lblChannel.setValue(programLoyaltyTransaction.getChannelId().getName());
        lblTransaction.setValue(programLoyaltyTransaction.getTransactionId().getDescription());
    }

    public void blockFields() {
        btnSave.setVisible(false);
    }

//    public Boolean validateEmpty() {
//        if (txtProduct.getText().isEmpty()) {
//            txtProduct.setFocus(true);
//            this.showMessage("sp.error.field.cannotNull", true, null);
//        } else {
//            return true;
//        }
//        return false;
//
//    }
    private void saveLoyaltyTransactionHasCommerce(LoyaltyTransactionHasCommerceCategory _loyaltyTransactionHasCommerceCategory) {
        ProgramLoyaltyTransaction programLoyaltyTransaction = null;

        try {
            LoyaltyTransactionHasCommerceCategory loyaltyTransactionHasCommerceCategory = null;

            if (_loyaltyTransactionHasCommerceCategory != null) {
                loyaltyTransactionHasCommerceCategory = _loyaltyTransactionHasCommerceCategory;
            } else {//New LegalPerson
                loyaltyTransactionHasCommerceCategory = new LoyaltyTransactionHasCommerceCategory();
            }

            AdminParametersController adminParameter = new AdminParametersController();
            if (adminParameter.getProgramLoyaltyTransactionParent().getId() != null) {
                programLoyaltyTransaction = adminParameter.getProgramLoyaltyTransactionParent();
            }

            
            loyaltyTransactionHasCommerceCategory.setCommerceCategoryId((CommerceCategory) cmbCommerce.getSelectedItem().getValue());
            loyaltyTransactionHasCommerceCategory.setProgramLoyaltyTransactionId(programLoyaltyTransaction);
            loyaltyTransactionHasCommerceCategory = programEJB.saveLoyaltyTransactionHasCommerceCategory(loyaltyTransactionHasCommerceCategory);
            loyaltyTransactionHasCommerceCategoryParam = loyaltyTransactionHasCommerceCategory;
            this.showMessage("sp.common.save.success", false, null);

            EventQueues.lookup("updateLoyaltyCommerce", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
//        if (validateEmpty()) {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveLoyaltyTransactionHasCommerce(null);
                break;
            case WebConstants.EVENT_EDIT:
                saveLoyaltyTransactionHasCommerce(loyaltyTransactionHasCommerceCategoryParam);
                break;
            default:
                break;
        }
//        }
    }

    public void onClick$btnBack() {
        winAdminLoyaltyCommerceCategory.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadField(loyaltyTransactionHasCommerceCategoryParam);
                loadCmbCommerce(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadField(loyaltyTransactionHasCommerceCategoryParam);
                blockFields();
                loadCmbCommerce(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadFields(loyaltyTransactionHasCommerceCategoryParam);
                loadCmbCommerce(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbCommerce(Integer evenInteger) {
        //cmbCommerce
        EJBRequest request1 = new EJBRequest();
        List<CommerceCategory> commerceCategory;
        try {
            commerceCategory = productEJB.getCommerceCategory(request1);
            loadGenericCombobox(commerceCategory, cmbCommerce, "economicActivity", evenInteger, Long.valueOf(loyaltyTransactionHasCommerceCategoryParam != null ? loyaltyTransactionHasCommerceCategoryParam.getCommerceCategoryId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }
}
