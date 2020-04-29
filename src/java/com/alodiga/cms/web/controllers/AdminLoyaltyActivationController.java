package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.ApplicantNaturalPerson;
import com.cms.commons.models.CollectionsRequest;
import com.cms.commons.models.NaturalCustomer;
import com.cms.commons.models.Person;
import com.cms.commons.models.Product;
import com.cms.commons.models.Request;
import com.cms.commons.models.RequestHasCollectionsRequest;
import com.cms.commons.models.ReviewRequest;
import com.cms.commons.models.ReviewRequestType;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

public class AdminLoyaltyActivationController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;

    private Label txtCity;
    private Label txtAgency;
    private Label txtCommercialAssessorUserCode;
    private Label txtAssessorName;
    private Label txtIdentification;
    private Textbox txtMaximumRechargeAmount;
    private Textbox txtObservations;
    private Datebox txtReviewDate;
    private Combobox cmbProduct;
    private Radio rApprovedYes;
    private Radio rApprovedNo;
    private ProductEJB productEJB = null;
    private User user = null;
    private RequestEJB requestEJB = null;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private ReviewRequest reviewCollectionsRequestParam;
    private List<ReviewRequest> reviewCollectionsRequest;
    private Button btnSave;
    private Request requestCard;
    private Request requestNumber = null;
    private List<RequestHasCollectionsRequest> requestHasCollectionsRequestList;
    private List<CollectionsRequest> collectionsByRequestList;
    private List<ApplicantNaturalPerson> cardComplementaryList = null;
    private NaturalCustomer naturalCustomerParent = null;
    public static Person customer = null;
    private AdminRequestController adminRequest = null;
    private AdminNaturalPersonController adminNaturalPerson = null;
    private ApplicantNaturalPerson applicantNaturalPerson = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        adminRequest = new AdminRequestController();
        adminNaturalPerson = new AdminNaturalPersonController();
        if (adminRequest.getRequest() != null) {
            requestCard = adminRequest.getRequest();
            eventType = adminRequest.getEventType();
            if (adminNaturalPerson.getApplicantNaturalPerson() != null) {
                applicantNaturalPerson = adminNaturalPerson.getApplicantNaturalPerson();
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
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            getReviewCollectionsRequestParam();
            this.clearMessage();
        } catch (Exception ex) {
            showError(ex);
        } finally {
            loadData();
        }
    }

    public ReviewRequest getReviewCollectionsRequestParam() {
        try {
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(QueryConstants.PARAM_REQUEST_ID, requestCard.getId());
            params.put(QueryConstants.PARAM_REVIEW_REQUEST_TYPE_ID, Constants.REVIEW_REQUEST_TYPE_COLLECTIONS);
            request1.setParams(params);
            reviewCollectionsRequest = requestEJB.getReviewRequestByRequest(request1);
            for (ReviewRequest r : reviewCollectionsRequest) {
                reviewCollectionsRequestParam = r;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return reviewCollectionsRequestParam;
    }

    public void clearFields() {
        txtMaximumRechargeAmount.setRawValue(null);
        txtReviewDate.setRawValue(null);
        txtObservations.setRawValue(null);
    }

    private void loadUser() {
        txtCity.setValue(user.getComercialAgencyId().getCityId().getName());
        txtAgency.setValue(user.getComercialAgencyId().getName());
        txtCommercialAssessorUserCode.setValue(user.getCode());
        txtAssessorName.setValue(user.getFirstNames() + " " + user.getLastNames());
        txtIdentification.setValue(user.getIdentificationNumber());
    }

    private void loadFields(ReviewRequest reviewCollectionsRequest) throws EmptyListException, GeneralException, NullParameterException {
        try {
            if (reviewCollectionsRequest != null) {
                NumberFormat n = NumberFormat.getCurrencyInstance();
                txtCity.setValue(reviewCollectionsRequest.getUserId().getComercialAgencyId().getCityId().getName());
                txtAgency.setValue(reviewCollectionsRequest.getUserId().getComercialAgencyId().getName());
                txtCommercialAssessorUserCode.setValue(reviewCollectionsRequest.getUserId().getCode());
                txtAssessorName.setValue(reviewCollectionsRequest.getUserId().getFirstNames() + " " + reviewCollectionsRequest.getUserId().getLastNames());
                txtIdentification.setValue(reviewCollectionsRequest.getUserId().getIdentificationNumber());
                if (reviewCollectionsRequest.getMaximumRechargeAmount() != null) {
                    txtMaximumRechargeAmount.setText(reviewCollectionsRequest.getMaximumRechargeAmount().toString());
                }
                if (reviewCollectionsRequest.getReviewDate() != null) {
                    txtReviewDate.setValue(reviewCollectionsRequest.getReviewDate());
                }
                if (reviewCollectionsRequest.getObservations() != null) {
                    txtObservations.setText(reviewCollectionsRequest.getObservations());
                }
                if (reviewCollectionsRequest.getIndApproved() != null) {
                    if (reviewCollectionsRequest.getIndApproved() == true) {
                        rApprovedYes.setChecked(true);
                        if (reviewCollectionsRequest.getRequestId().getStatusRequestId().getId() != Constants.STATUS_REQUEST_COLLECTIONS_WITHOUT_APPROVAL) {
                            blockFields();
                        }
                        cmbProduct.setDisabled(true);
                    } else {
                        rApprovedNo.setChecked(true);
                    }
                }
            } else {
                txtCity = null;
                txtAgency = null;
                txtCommercialAssessorUserCode = null;
                txtAssessorName = null;
            }
        } catch (Exception ex) {
            showError(ex);
        } finally {
            txtCity.setValue(user.getComercialAgencyId().getCityId().getName());
            txtAgency.setValue(user.getComercialAgencyId().getName());
            txtCommercialAssessorUserCode.setValue(user.getCode());
            txtAssessorName.setValue(user.getFirstNames() + " " + user.getLastNames());
        }
    }

    public void blockFields() {
        txtReviewDate.setDisabled(true);
        txtMaximumRechargeAmount.setReadonly(true);
        txtObservations.setReadonly(true);
        rApprovedYes.setDisabled(true);
        rApprovedNo.setDisabled(true);
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

    private void saveReviewCollectionsRequest(ReviewRequest _reviewCollectionsRequest) {
        try {
            ReviewRequest reviewCollectionsRequest = null;
            boolean indApproved;
            int indReviewCollectionApproved = 0;
            int indReviewCollectionIncomplete = 0;

            if (_reviewCollectionsRequest != null) {
                reviewCollectionsRequest = _reviewCollectionsRequest;
            } else {
                reviewCollectionsRequest = new ReviewRequest();
            }

            if (rApprovedYes.isChecked()) {
                indApproved = true;
            } else {
                indApproved = false;
            }

            //Obtiene el tipo de revision Recaudos
            EJBRequest request = new EJBRequest();
            request.setParam(Constants.REVIEW_REQUEST_TYPE_COLLECTIONS);
            ReviewRequestType reviewRequestType = requestEJB.loadReviewRequestType(request);

            if (rApprovedYes.isChecked()) {
                //Recaudos que han sido revisados por Agente Comercial
                Map params = new HashMap();
                EJBRequest request1 = new EJBRequest();
                params.put(Constants.REQUESTS_KEY, adminRequest.getRequest().getId());
                request1.setParams(params);
                requestHasCollectionsRequestList = requestEJB.getRequestsHasCollectionsRequestByRequest(request1);
                //Recaudos asociados a la Solicitud
                params = new HashMap();
                params.put(Constants.COUNTRY_KEY, adminRequest.getRequest().getCountryId().getId());
                params.put(Constants.PROGRAM_KEY, adminRequest.getRequest().getProgramId().getId());
                params.put(Constants.PRODUCT_TYPE_KEY, adminRequest.getRequest().getProductTypeId().getId());
                params.put(Constants.PERSON_TYPE_KEY, adminRequest.getRequest().getPersonTypeId().getId());
                request1.setParams(params);
                collectionsByRequestList = requestEJB.getCollectionsByRequest(request1);
                //Se chequea si hay recaudos sin revisar
                if (collectionsByRequestList.size() > requestHasCollectionsRequestList.size()) {
                    indReviewCollectionIncomplete = 1;
                }
                for (RequestHasCollectionsRequest r : requestHasCollectionsRequestList) {
                    if (r.getIndApproved() == 0) {
                        indReviewCollectionApproved = 1;
                    }
                    if (r.getUrlImageFile() == null) {
                        indReviewCollectionIncomplete = 1;
                    }
                }
            }

            //Guarda la revision
            reviewCollectionsRequest.setRequestId(requestCard);
            reviewCollectionsRequest.setReviewDate(txtReviewDate.getValue());
            reviewCollectionsRequest.setMaximumRechargeAmount(Float.parseFloat(txtMaximumRechargeAmount.getText()));
            reviewCollectionsRequest.setUserId(user);
            reviewCollectionsRequest.setProductId((Product) cmbProduct.getSelectedItem().getValue());
            reviewCollectionsRequest.setObservations(txtObservations.getText());
            reviewCollectionsRequest.setReviewRequestTypeId(reviewRequestType);
            reviewCollectionsRequest.setIndApproved(indApproved);
            reviewCollectionsRequest.setCreateDate(new Timestamp(new Date().getTime()));
            reviewCollectionsRequest = requestEJB.saveReviewRequest(reviewCollectionsRequest);

            //Actualiza el agente comercial en la solictud de tarjeta
            requestCard.setUserId(user);
            requestCard = requestEJB.saveRequest(requestCard);

            
            btnSave.setVisible(false);
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
        try {
            switch (eventType) {
                case WebConstants.EVENT_EDIT:
                    getReviewCollectionsRequestParam();
                    if (reviewCollectionsRequestParam != null) {
                        loadFields(reviewCollectionsRequestParam);
                    } else {
                        loadUser();
                    }
                    loadCmbProduct(eventType, requestCard.getProgramId().getId());
                    break;
                case WebConstants.EVENT_VIEW:
                    getReviewCollectionsRequestParam();
                    if (reviewCollectionsRequestParam != null) {
                        loadFields(reviewCollectionsRequestParam);
                    } else {
                        loadUser();
                    }
                    blockFields();
                    loadCmbProduct(eventType, requestCard.getProgramId().getId());
                    break;
                case WebConstants.EVENT_ADD:
                    loadUser();
                    loadCmbProduct(eventType, requestCard.getProgramId().getId());
                    break;

            }
        } catch (EmptyListException ex) {
            Logger.getLogger(AdminLoyaltyActivationController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(AdminLoyaltyActivationController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(AdminLoyaltyActivationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadCmbProduct(Integer evenInteger, Long programId) {
        EJBRequest request1 = new EJBRequest();
        List<Product> product;
        cmbProduct.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_PROGRAM_ID, programId);
        request1.setParams(params);
        try {
            product = productEJB.getProductByProgram(request1);
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
