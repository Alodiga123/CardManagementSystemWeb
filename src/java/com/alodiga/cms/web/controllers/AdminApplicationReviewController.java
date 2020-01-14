package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Product;
import com.cms.commons.models.ReviewCollectionsRequest;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Textbox;

public class AdminApplicationReviewController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;

    private Textbox txtCity;
    private Textbox txtAgency;
    private Textbox txtCommercialAssessorUserCode;
    private Textbox txtAssessorName;
    private Textbox txtIdentification;
    private Textbox txtMaximumRechargeAmount;
    private Textbox txtObservations;
    private Datebox txtReviewDate;
    private Combobox cmbProduct;
    private ProductEJB productEJB = null;
    private User user = null;
    private RequestEJB requestEJB = null;
    private ReviewCollectionsRequest reviewCollectionsRequestParam;
    private Button btnSave;
    private Integer eventType;
    private AdminRequestController adminRequest = null;
    Map params = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        AdminRequestController adminRequest = new AdminRequestController();
        AdminNaturalPersonController adminPerson = new AdminNaturalPersonController();
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            user = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtCity.setRawValue(null);
        txtAgency.setRawValue(null);
        txtCommercialAssessorUserCode.setRawValue(null);
        txtAssessorName.setRawValue(null);
        txtIdentification.setRawValue(null);
        txtMaximumRechargeAmount.setRawValue(null);
        txtReviewDate.setRawValue(null);
        txtObservations.setRawValue(null);
    }

    private void loadFields(ReviewCollectionsRequest reviewCollectionsRequest) {
        try {
            txtCity.setValue(user.getComercialAgencyId().getCityId().getName());
            txtAgency.setValue(user.getComercialAgencyId().getName());
            txtCommercialAssessorUserCode.setValue(user.getCode());
            txtAssessorName.setValue(user.getFirstNames() + " " + user.getLastNames());
            txtMaximumRechargeAmount.setValue(reviewCollectionsRequest.getMaximumRechargeAmount().toString());
            txtReviewDate.setValue(reviewCollectionsRequest.getReviewDate());
            txtObservations.setValue(reviewCollectionsRequest.getObservations());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtReviewDate.setReadonly(true);
        txtMaximumRechargeAmount.setReadonly(true);
        cmbProduct.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtReviewDate.getText().isEmpty()) {
            txtReviewDate.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtMaximumRechargeAmount.getText().isEmpty()) {
            txtMaximumRechargeAmount.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtObservations.getText().isEmpty()) {
            txtObservations.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveReviewCollectionsRequest(ReviewCollectionsRequest _reviewCollectionsRequest) {
        AdminRequestController adminRequest = new AdminRequestController();
        try {
            ReviewCollectionsRequest reviewCollectionsRequest = null;

            if (_reviewCollectionsRequest != null) {
                reviewCollectionsRequest = _reviewCollectionsRequest;
            } else {//New address
                reviewCollectionsRequest = new ReviewCollectionsRequest();
            }

            //Guarda la dirección del solicitante
            reviewCollectionsRequest.setRequestId(adminRequest.getRequest());
            reviewCollectionsRequest.setReviewDate(txtReviewDate.getValue());
            reviewCollectionsRequest.setMaximumRechargeAmount(Float.parseFloat(txtMaximumRechargeAmount.getText()));
            //reviewCollectionsRequest.setUserId(User);
            reviewCollectionsRequest.setProductId((Product) cmbProduct.getSelectedItem().getValue());
            reviewCollectionsRequest.setObservations(txtObservations.getText());
            reviewCollectionsRequest = requestEJB.saveReviewCollectionsRequest(reviewCollectionsRequest);

            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveReviewCollectionsRequest(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveReviewCollectionsRequest(reviewCollectionsRequestParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        loadFields(reviewCollectionsRequestParam);
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
//                txtAssessorName.setDisabled(true);
                loadCmbProduct(eventType);
                break;
            case WebConstants.EVENT_VIEW:
//                txtAssessorName.setDisabled(true);
                blockFields();
                loadCmbProduct(eventType);
                break;
            case WebConstants.EVENT_ADD:
//                txtAssessorName.setDisabled(true);
                loadCmbProduct(eventType);
                break;
        }
    }

    private void loadCmbProduct(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Product> product;
        try {
            product = productEJB.getProduct(request1);
            loadGenericCombobox(product, cmbProduct, "name", evenInteger, Long.valueOf(reviewCollectionsRequestParam != null ? reviewCollectionsRequestParam.getProductId().getId() : 0));
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
