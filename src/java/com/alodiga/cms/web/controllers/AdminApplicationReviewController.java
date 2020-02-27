package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import static com.alodiga.cms.web.controllers.AdminNaturalPersonController.applicant;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.ApplicantNaturalPerson;
import com.cms.commons.models.Country;
import com.cms.commons.models.FamilyReferences;
import com.cms.commons.models.LegalCustomer;
import com.cms.commons.models.NaturalCustomer;
import com.cms.commons.models.Person;
import com.cms.commons.models.PersonClassification;
import com.cms.commons.models.Product;
import com.cms.commons.models.Request;
import com.cms.commons.models.RequestHasCollectionsRequest;
import com.cms.commons.models.ReviewRequest;
import com.cms.commons.models.ReviewRequestType;
import com.cms.commons.models.StatusCustomer;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

public class AdminApplicationReviewController extends GenericAbstractAdminController {

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
    Map params = null;
    private Request requestCard;
    Request requestNumber = null;
    private List<RequestHasCollectionsRequest> requestHasCollectionsRequestList;
    private List<ApplicantNaturalPerson> cardComplementaryList = null;
    private NaturalCustomer naturalCustomerParent = null;
    public static Person customer = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        adminRequest = new AdminRequestController();
        if (adminRequest.getRequest() != null) {
            requestCard = adminRequest.getRequest();
        }
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
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

            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(QueryConstants.PARAM_REQUEST_ID, requestCard.getId());
            request1.setParams(params);
            reviewCollectionsRequest = requestEJB.getReviewRequestByRequest(request1);
            for (ReviewRequest r : reviewCollectionsRequest) {
                reviewCollectionsRequestParam = r;
            }
        } catch (Exception ex) {
            showError(ex);
        } finally {
            loadData();
        }
    }

    public void clearFields() {
        txtMaximumRechargeAmount.setRawValue(null);
        txtReviewDate.setRawValue(null);
        txtObservations.setRawValue(null);
    }

    private void loadFields(ReviewRequest reviewCollectionsRequest) throws EmptyListException, GeneralException, NullParameterException {
        try {
            txtCity.setValue(user.getComercialAgencyId().getCityId().getName());
            txtAgency.setValue(user.getComercialAgencyId().getName());
            txtCommercialAssessorUserCode.setValue(user.getCode());
            txtAssessorName.setValue(user.getFirstNames() + " " + user.getLastNames());
            txtIdentification.setValue(user.getIdentificationNumber());
            txtMaximumRechargeAmount.setText(reviewCollectionsRequest.getMaximumRechargeAmount().toString());
            txtReviewDate.setValue(reviewCollectionsRequest.getReviewDate());
            txtObservations.setText(reviewCollectionsRequest.getObservations());
            if (reviewCollectionsRequest.getIndApproved() == true) {
                    rApprovedYes.setChecked(true);
                } else {
                    rApprovedNo.setChecked(true);
                }
        } catch (Exception ex) {
            showError(ex);
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
            int indReviewCollection = 0;

            if (_reviewCollectionsRequest != null) {
                reviewCollectionsRequest = _reviewCollectionsRequest;
            } else {//New reviewCollectionsRequest
                reviewCollectionsRequest = new ReviewRequest();
            }

            AdminRequestController adminRequest = new AdminRequestController();
            if (adminRequest.getRequest().getId() != null) {
                requestNumber = adminRequest.getRequest();
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
                indApproved = true;

                Map params = new HashMap();
                EJBRequest request1 = new EJBRequest();
                params.put(Constants.REQUESTS_KEY, adminRequest.getRequest().getId());
                request1.setParams(params);
                requestHasCollectionsRequestList = requestEJB.getRequestsHasCollectionsRequestByRequest(request1);
                for (RequestHasCollectionsRequest r : requestHasCollectionsRequestList) {
                    if (r.getIndApproved() == 0) {
                        indReviewCollection = 1;
                        indApproved = false;
                    }
                }
            } else {
                indApproved = false;
            }

            //Guarda la revision
            reviewCollectionsRequest.setRequestId(requestNumber);
            reviewCollectionsRequest.setReviewDate(txtReviewDate.getValue());
            reviewCollectionsRequest.setMaximumRechargeAmount(Float.parseFloat(txtMaximumRechargeAmount.getText()));
            reviewCollectionsRequest.setUserId(user);
            reviewCollectionsRequest.setProductId((Product) cmbProduct.getSelectedItem().getValue());
            reviewCollectionsRequest.setObservations(txtObservations.getText());
            reviewCollectionsRequest.setReviewRequestTypeId(reviewRequestType);
            reviewCollectionsRequest.setIndApproved(indApproved);
            reviewCollectionsRequest = requestEJB.saveReviewRequest(reviewCollectionsRequest);
            this.showMessage("sp.common.save.success", false, null);

            if (adminRequest.getRequest().getIndPersonNaturalRequest() == true) {
                if (reviewCollectionsRequest.getIndApproved() == true) {
                    if (indReviewCollection == 0) {
                        //creando al titular
                        saveNaturalCustomer(adminRequest);
                        //creando las tarjetas complementarias
                        saveNaturalPersonCustomer(adminRequest);
                        //saveFamilyReferentCustomer(adminRequest);
                    } else {
                        //updateRequest(adminRequest);
                    }
                }
            } else {
                saveLegalCustomer(adminRequest);
            }

        } catch (Exception ex) {
            showError(ex);
        }
    }

//    public void updateRequest(AdminRequestController adminRequest) {
//        try {
//
//            Request requestCollection = null;
//            requestCollection = new Request();
//            
//            //colocar request "RECHAZADA"
//            EJBRequest request1 = new EJBRequest();
//            request1 = new EJBRequest();
//            request1.setParam(Constants.STATUS_REQUEST_IN_REJECTED);
//            StatusRequest statusRequest = utilsEJB.loadStatusRequest(request1);
//            
//            
//            requestCollection.setStatusRequestId(statusRequest);
//            requestCollection = requestEJB.saveRequest(requestCollection);
//            this.showMessage("cms.crud.applicantionRiview.customer", false, null);
//
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
    public void saveNaturalCustomer(AdminRequestController adminRequest) {
        try {
            Person person = null;
            person = new Person();
            NaturalCustomer naturalCustomer = null;
            naturalCustomer = new NaturalCustomer();

            //colocar estatus del cliente "ACTIVO"
            EJBRequest request1 = new EJBRequest();
            request1 = new EJBRequest();
            request1.setParam(Constants.STATUS_CUSTOMER_ACTIVE);
            StatusCustomer statusCustomer = personEJB.loadStatusCustomer(request1);

            //colocar la clasificacion de la persona como CLIENTE
            EJBRequest request2 = new EJBRequest();
            request2 = new EJBRequest();
            request2.setParam(Constants.PERSON_CLASSIFICATION_CUSTOMER);
            PersonClassification personClassification = utilsEJB.loadPersonClassification(request2);

            //Guardar la persona
            person.setCountryId(adminRequest.getRequest().getPersonId().getCountryId());
            person.setPersonTypeId(adminRequest.getRequest().getPersonId().getPersonTypeId());
            person.setEmail(adminRequest.getRequest().getPersonId().getEmail());
            person.setCreateDate(new Timestamp(new Date().getTime()));
            person.setPersonClassificationId(personClassification);
            //person = personEJB.savePerson(person);
            customer = person;

            naturalCustomer.setPersonId(person);
            naturalCustomer.setDocumentsPersonTypeId(adminRequest.getRequest().getPersonId().getApplicantNaturalPerson().getDocumentsPersonTypeId());
            naturalCustomer.setIdentificationNumber(adminRequest.getRequest().getPersonId().getApplicantNaturalPerson().getIdentificationNumber());
            naturalCustomer.setDueDateDocumentIdentification(adminRequest.getRequest().getPersonId().getApplicantNaturalPerson().getDueDateDocumentIdentification());
            naturalCustomer.setStatusCustomerId(statusCustomer);
            naturalCustomer.setFirstNames(adminRequest.getRequest().getPersonId().getApplicantNaturalPerson().getFirstNames());
            naturalCustomer.setLastNames(adminRequest.getRequest().getPersonId().getApplicantNaturalPerson().getLastNames());
            naturalCustomer.setMarriedLastName(adminRequest.getRequest().getPersonId().getApplicantNaturalPerson().getMarriedLastName());
            naturalCustomer.setGender(adminRequest.getRequest().getPersonId().getApplicantNaturalPerson().getGender());
            naturalCustomer.setPlaceBirth(adminRequest.getRequest().getPersonId().getApplicantNaturalPerson().getPlaceBirth());
            naturalCustomer.setDateBirth(adminRequest.getRequest().getPersonId().getApplicantNaturalPerson().getDateBirth());
            naturalCustomer.setCivilStatusId(adminRequest.getRequest().getPersonId().getApplicantNaturalPerson().getCivilStatusId());
            naturalCustomer.setFamilyResponsibilities(adminRequest.getRequest().getPersonId().getApplicantNaturalPerson().getFamilyResponsibilities());
            naturalCustomer.setProfessionId(adminRequest.getRequest().getPersonId().getApplicantNaturalPerson().getProfessionId());
            naturalCustomer.setKinShipApplicantId(adminRequest.getRequest().getPersonId().getApplicantNaturalPerson().getKinShipApplicantId());
            naturalCustomer.setCreateDate(new Timestamp(new Date().getTime()));
            naturalCustomerParent = naturalCustomer;

            this.showMessage("cms.common.save.success.customer", false, null);

        } catch (Exception ex) {
            showError(ex);
        }
    }

//    public void saveFamilyReferentCustomer(AdminRequestController adminRequest) {
//
//        try {
//            List<FamilyReferences> familyReferences = null;
//            familyReferences = new ArrayList<FamilyReferences>();
//            ApplicantNaturalPerson applicantNaturalPerson = null;
//
//            //Solicitante de Tarjeta
//            AdminNaturalPersonController adminNaturalPerson = new AdminNaturalPersonController();
//            if (adminNaturalPerson.getApplicantNaturalPerson() != null) {
//                applicantNaturalPerson = adminNaturalPerson.getApplicantNaturalPerson();
//            }
//            EJBRequest request1 = new EJBRequest();
//            Map params = new HashMap();
//            params.put(Constants.APPLICANT_NATURAL_PERSON_KEY, applicantNaturalPerson.getId());
//            request1.setParams(params);
//            familyReferences = personEJB.getFamilyReferencesByApplicant(request1);
//
//            if (familyReferences != null) {
//                //Guarda la referencia familiar asociada al solicitante
//                familyReferences.setFirstNames();
//                familyReferences.setApplicantNaturalPersonId(applicantNaturalPerson);
//                familyReferences.setCity(txtCity.getText());
//                familyReferences.setLocalPhone(txtLocalPhone.getText());
//                familyReferences.setCellPhone(txtCellPhone.getText());
//                familyReferences.setLastNames(txtFullLastName.getText());
//            //familyReferences = personEJB.saveFamilyReferences(familyReferences);
//                //familyReferencesParam = familyReferences;
//                this.showMessage("sp.common.save.success", false, null);
//            }
//        } catch (Exception ex) {
//            showError(ex);
//        }
//    }
    public void saveNaturalPersonCustomer(AdminRequestController adminRequest) {
        try {
            Long countCardComplementary = 0L;
            Person person = null;
            person = new Person();
            ApplicantNaturalPerson applicantNaturalPerson = null;
            NaturalCustomer naturalCustomer = null;
            naturalCustomer = new NaturalCustomer();

            //Solicitante de Tarjeta
            AdminNaturalPersonController adminNaturalPerson = new AdminNaturalPersonController();
            if (adminNaturalPerson.getApplicantNaturalPerson() != null) {
                applicantNaturalPerson = adminNaturalPerson.getApplicantNaturalPerson();
            }

            countCardComplementary = personEJB.countCardComplementaryByApplicant(applicantNaturalPerson.getId());

            if (countCardComplementary != 0) {
                EJBRequest request1 = new EJBRequest();
                Map params = new HashMap();
                params.put(Constants.APPLICANT_NATURAL_PERSON_KEY, applicantNaturalPerson.getId());
                request1.setParams(params);
                cardComplementaryList = personEJB.getCardComplementaryByApplicant(request1);

                //colocar estatus del cliente "ACTIVO"
                EJBRequest request2 = new EJBRequest();
                request2 = new EJBRequest();
                request2.setParam(Constants.STATUS_CUSTOMER_ACTIVE);
                StatusCustomer statusCustomer = personEJB.loadStatusCustomer(request2);

                //colocar la clasificacion de la persona como CLIENTE
                EJBRequest request3 = new EJBRequest();
                request3 = new EJBRequest();
                request3.setParam(Constants.PERSON_CLASSIFICATION_CUSTOMER);
                PersonClassification personClassification = utilsEJB.loadPersonClassification(request3);

                for (ApplicantNaturalPerson r : cardComplementaryList) {
                    if (r.getApplicantParentId() != null) {

                        //Guardar la persona
                        person.setCountryId(r.getPersonId().getCountryId());
                        person.setPersonTypeId(r.getPersonId().getPersonTypeId());
                        person.setEmail(r.getPersonId().getEmail());
                        person.setCreateDate(new Timestamp(new Date().getTime()));
                        person.setPersonClassificationId(personClassification);
                        //person = personEJB.savePerson(person);
                        customer = person;

                        naturalCustomer.setPersonId(r.getPersonId());
                        naturalCustomer.setDocumentsPersonTypeId(r.getDocumentsPersonTypeId());
                        naturalCustomer.setIdentificationNumber(r.getIdentificationNumber());
                        naturalCustomer.setDueDateDocumentIdentification(r.getDueDateDocumentIdentification());
                        naturalCustomer.setStatusCustomerId(statusCustomer);
                        naturalCustomer.setFirstNames(r.getFirstNames());
                        naturalCustomer.setLastNames(r.getLastNames());
                        naturalCustomer.setMarriedLastName(r.getMarriedLastName());
                        naturalCustomer.setGender(r.getGender());
                        naturalCustomer.setPlaceBirth(r.getPlaceBirth());
                        naturalCustomer.setDateBirth(r.getDateBirth());
                        naturalCustomer.setCivilStatusId(r.getCivilStatusId());
                        naturalCustomer.setFamilyResponsibilities(r.getFamilyResponsibilities());
                        naturalCustomer.setProfessionId(r.getProfessionId());
                        naturalCustomer.setNaturalCustomerId(naturalCustomerParent.getNaturalCustomerId());
                        naturalCustomer.setKinShipApplicantId(r.getKinShipApplicantId());
                        naturalCustomer.setCreateDate(new Timestamp(new Date().getTime()));

                        this.showMessage("cms.common.save.success.customer", false, null);
                    }
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void saveLegalCustomer(AdminRequestController adminRequest) {
        try {
            LegalCustomer legalCustomer = null;
            legalCustomer = new LegalCustomer();

            //colocar estatus del cliente "ACTIVO"
            EJBRequest request1 = new EJBRequest();
            request1 = new EJBRequest();
            request1.setParam(Constants.STATUS_CUSTOMER_ACTIVE);
            StatusCustomer statusCustomer = personEJB.loadStatusCustomer(request1);

            legalCustomer.setPersonId(adminRequest.getRequest().getPersonId());
            legalCustomer.setDocumentsPersonTypeId(adminRequest.getRequest().getPersonId().getLegalPerson().getDocumentsPersonTypeId());
            legalCustomer.setIdentificationNumber(adminRequest.getRequest().getPersonId().getLegalPerson().getIdentificationNumber());
            legalCustomer.setTradeName(adminRequest.getRequest().getPersonId().getLegalPerson().getTradeName());
            legalCustomer.setEnterpriseName(adminRequest.getRequest().getPersonId().getLegalPerson().getEnterpriseName());
            legalCustomer.setStatusCustomerId(statusCustomer);
            legalCustomer.setEconomicActivityId(adminRequest.getRequest().getPersonId().getLegalPerson().getEconomicActivityId());
            legalCustomer.setDateInscriptionRegister(adminRequest.getRequest().getPersonId().getLegalPerson().getDateInscriptionRegister());
            legalCustomer.setRegisterNumber(adminRequest.getRequest().getPersonId().getLegalPerson().getRegisterNumber());
            legalCustomer.setPayedCapital(adminRequest.getRequest().getPersonId().getLegalPerson().getPayedCapital());
            legalCustomer.setWebSite(adminRequest.getRequest().getPersonId().getLegalPerson().getWebSite());
            legalCustomer.setCreateDate(new Timestamp(new Date().getTime()));

            this.showMessage("cms.common.save.success.customer", false, null);

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
            loadFields(reviewCollectionsRequestParam);
            switch (eventType) {
                case WebConstants.EVENT_EDIT:
                    loadCmbProduct(eventType);
                    break;
                case WebConstants.EVENT_VIEW:
                    blockFields();
                    loadCmbProduct(eventType);
                    break;
                case WebConstants.EVENT_ADD:
                    loadCmbProduct(eventType);
                    break;
            }
        } catch (EmptyListException ex) {
            Logger.getLogger(AdminApplicationReviewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(AdminApplicationReviewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(AdminApplicationReviewController.class.getName()).log(Level.SEVERE, null, ex);
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
