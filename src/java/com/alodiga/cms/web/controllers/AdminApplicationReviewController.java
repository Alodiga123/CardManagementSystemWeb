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
import com.cms.commons.models.Address;
import com.cms.commons.models.ApplicantNaturalPerson;
import com.cms.commons.models.CardRequestNaturalPerson;
import com.cms.commons.models.FamilyReferences;
import com.cms.commons.models.LegalCustomer;
import com.cms.commons.models.LegalCustomerHasLegalRepresentatives;
import com.cms.commons.models.LegalPerson;
import com.cms.commons.models.LegalPersonHasLegalRepresentatives;
import com.cms.commons.models.NaturalCustomer;
import com.cms.commons.models.Person;
import com.cms.commons.models.PersonClassification;
import com.cms.commons.models.PersonHasAddress;
import com.cms.commons.models.Product;
import com.cms.commons.models.ReasonRejectionRequest;
import com.cms.commons.models.Request;
import com.cms.commons.models.RequestHasCollectionsRequest;
import com.cms.commons.models.ReviewRequest;
import com.cms.commons.models.ReviewRequestType;
import com.cms.commons.models.StatusCustomer;
import com.cms.commons.models.StatusRequest;
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
    private AdminRequestController adminRequest = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        AdminRequestController adminRequest = new AdminRequestController();
        if (adminRequest.getRequest() != null) {
            requestCard = adminRequest.getRequest();
            eventType = adminRequest.getEventType();
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
                } else {
                    rApprovedNo.setChecked(true);
                }
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
            int indReviewCollectionApproved = 0;
            int indReviewCollectionIncomplete = 0;

            if (_reviewCollectionsRequest != null) {
                reviewCollectionsRequest = _reviewCollectionsRequest;
            } else {
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
                Map params = new HashMap();
                EJBRequest request1 = new EJBRequest();
                params.put(Constants.REQUESTS_KEY, adminRequest.getRequest().getId());
                request1.setParams(params);
                requestHasCollectionsRequestList = requestEJB.getRequestsHasCollectionsRequestByRequest(request1);
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
            reviewCollectionsRequest.setRequestId(requestNumber);
            reviewCollectionsRequest.setReviewDate(txtReviewDate.getValue());
            reviewCollectionsRequest.setMaximumRechargeAmount(Float.parseFloat(txtMaximumRechargeAmount.getText()));
            reviewCollectionsRequest.setUserId(user);
            reviewCollectionsRequest.setProductId((Product) cmbProduct.getSelectedItem().getValue());
            reviewCollectionsRequest.setObservations(txtObservations.getText());
            reviewCollectionsRequest.setReviewRequestTypeId(reviewRequestType);
            reviewCollectionsRequest.setIndApproved(indApproved);
            reviewCollectionsRequest = requestEJB.saveReviewRequest(reviewCollectionsRequest);
            
            //Si los recaudos están incompletos, se rechaza la solicitud
            if (indReviewCollectionIncomplete == 1) {
                updateRequestByCollectionsIncomplete(requestCard);
            }

            //Actualiza el agente comercial en la solictud de tarjeta
            requestCard.setUserId(user);
            requestCard = requestEJB.saveRequest(requestCard);
            
            if (adminRequest.getRequest().getIndPersonNaturalRequest() == true) { //La solicitud es de persona natural
                if (reviewCollectionsRequest.getIndApproved() == true) {
                    if (indReviewCollectionApproved == 0) {
                        //Se crea el cliente
                        saveNaturalCustomer(adminRequest);
                        saveCardComplementariesCustomer(adminRequest);
                        this.showMessage("cms.common.save.success.customer", false, null);
                    } else {
                        this.showMessage("cms.common.requestNotApproved", false, null);
                    }
                }
            } else { //la solicitud es de persona jurídica
                if (reviewCollectionsRequest.getIndApproved() == true) {
                    saveLegalCustomer(adminRequest);
                } else {
                    this.showMessage("cms.common.requestNotApproved", false, null);
                }   
            }
            this.showMessage("sp.common.save.success", false, null);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void updateRequestByCollectionsIncomplete(Request requestCard) {
        try {
            EJBRequest request = new EJBRequest();
            request.setParam(Constants.STATUS_REQUEST_REJECTED);
            StatusRequest statusRequestRejected = utilsEJB.loadStatusRequest(request);
            requestCard.setStatusRequestId(statusRequestRejected);
            request.setParam(Constants.REASON_REQUEST_REJECTED_BY_COLLECTIONS);
            ReasonRejectionRequest reasonRejectionRequest = requestEJB.loadReasonRejectionRequest(request);
            requestCard.setReasonRejectionRequestId(reasonRejectionRequest);
            requestCard = requestEJB.saveRequest(requestCard);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    
    public void saveNaturalCustomer(AdminRequestController adminRequest) {
        try {
            Person person = new Person();
            NaturalCustomer naturalCustomer = new NaturalCustomer();
            ApplicantNaturalPerson applicant = adminRequest.getRequest().getPersonId().getApplicantNaturalPerson();

            //Guardar la persona asociada al cliente
            person.setCountryId(adminRequest.getRequest().getPersonId().getCountryId());
            person.setPersonTypeId(adminRequest.getRequest().getPersonId().getPersonTypeId());
            person.setEmail(adminRequest.getRequest().getPersonId().getEmail());
            person.setCreateDate(new Timestamp(new Date().getTime()));
            person.setPersonClassificationId(getClassificationCustomer());
            person = personEJB.savePerson(person);

            //Guarda el Cliente
            naturalCustomer.setPersonId(person);
            naturalCustomer.setDocumentsPersonTypeId(applicant.getDocumentsPersonTypeId());
            naturalCustomer.setIdentificationNumber(applicant.getIdentificationNumber());
            naturalCustomer.setDueDateDocumentIdentification(applicant.getDueDateDocumentIdentification());
            naturalCustomer.setStatusCustomerId(getStatusActiveCustomer());
            naturalCustomer.setFirstNames(applicant.getFirstNames());
            naturalCustomer.setLastNames(applicant.getLastNames());
            naturalCustomer.setMarriedLastName(applicant.getMarriedLastName());
            naturalCustomer.setGender(applicant.getGender());
            naturalCustomer.setPlaceBirth(applicant.getPlaceBirth());
            naturalCustomer.setDateBirth(applicant.getDateBirth());
            naturalCustomer.setCivilStatusId(applicant.getCivilStatusId());
            naturalCustomer.setFamilyResponsibilities(applicant.getFamilyResponsibilities());
            naturalCustomer.setProfessionId(applicant.getProfessionId());
            naturalCustomer = personEJB.saveNaturalCustomer(naturalCustomer);
            naturalCustomerParent = naturalCustomer;

            saveFamilyReferentCustomer(naturalCustomer);
            saveAddressCustomer(naturalCustomer);


        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void saveFamilyReferentCustomer(NaturalCustomer naturalCustomer) {
        try {
            ApplicantNaturalPerson applicantNaturalPerson = null;
            List<FamilyReferences> familyReferences;
            FamilyReferences familyCustomer = null;

            //Solicitante de Tarjeta
            AdminNaturalPersonController adminNaturalPerson = new AdminNaturalPersonController();
            if (adminNaturalPerson.getApplicantNaturalPerson() != null) {
                applicantNaturalPerson = adminNaturalPerson.getApplicantNaturalPerson();
            }

            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.APPLICANT_NATURAL_PERSON_KEY, applicantNaturalPerson.getId());
            request1.setParams(params);
            familyReferences = personEJB.getFamilyReferencesByApplicant(request1);

            if (familyReferences != null) {
                for (FamilyReferences r : familyReferences) {
                    //Actualiza la referencia familiar colocandole ID del cliente nuevo
                    familyCustomer = r;
                    familyCustomer.setNaturalCustomerId(naturalCustomer);
                    familyCustomer = personEJB.saveFamilyReferences(familyCustomer);
                }
            } else {
                this.showMessage("sp.common.save.success", false, null);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void saveAddressCustomer(NaturalCustomer naturalCustomer) {
        try {
            List<PersonHasAddress> personHasAddress;
            personHasAddress = new ArrayList<PersonHasAddress>();
            ApplicantNaturalPerson applicantNaturalPerson = null;
            PersonHasAddress personAddressCustomer = null;

            //Solicitante de Tarjeta
            AdminNaturalPersonController adminNaturalPerson = new AdminNaturalPersonController();
            if (adminNaturalPerson.getApplicantNaturalPerson() != null) {
                applicantNaturalPerson = adminNaturalPerson.getApplicantNaturalPerson();
            }

            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_KEY, applicantNaturalPerson.getId());
            request1.setParams(params);
            personHasAddress = personEJB.getPersonHasAddressesByPerson(request1);

            if (personHasAddress != null) {
                for (PersonHasAddress r : personHasAddress) {
                    personAddressCustomer = new PersonHasAddress();
                    personAddressCustomer.setAddressId(r.getAddressId());
                    personAddressCustomer.setPersonId(naturalCustomer.getPersonId());
                    personAddressCustomer = personEJB.savePersonHasAddress(personAddressCustomer);
                }
            } else {
                this.showMessage("sp.common.save.success", false, null);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void saveCardComplementariesCustomer(AdminRequestController adminRequest) {
        try {
            Long countCardComplementary = 0L;
            Person person = null;
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

                //colocar la clasificacion de la persona como Tarjetas Complementarias
                EJBRequest request3 = new EJBRequest();
                request3 = new EJBRequest();
                request3.setParam(Constants.PERSON_CARD_COMPLEMENTARIES_CUSTOMER);
                PersonClassification personClassification = utilsEJB.loadPersonClassification(request3);

                for (ApplicantNaturalPerson r : cardComplementaryList) {
                        person = new Person();
                        naturalCustomer = new NaturalCustomer();

                        //Guardar la persona
                        person.setCountryId(r.getPersonId().getCountryId());
                        person.setPersonTypeId(r.getPersonId().getPersonTypeId());
                        person.setEmail(r.getPersonId().getEmail());
                        person.setCreateDate(new Timestamp(new Date().getTime()));
                        person.setPersonClassificationId(personClassification);
                        person = personEJB.savePerson(person);

                        naturalCustomer.setPersonId(person);
                        naturalCustomer.setDocumentsPersonTypeId(r.getDocumentsPersonTypeId());
                        naturalCustomer.setIdentificationNumber(r.getIdentificationNumber());
                        naturalCustomer.setDueDateDocumentIdentification(r.getDueDateDocumentIdentification());
                        naturalCustomer.setStatusCustomerId(getStatusActiveCustomer());
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
                        naturalCustomer = personEJB.saveNaturalCustomer(naturalCustomer);
                }
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public StatusCustomer getStatusActiveCustomer() {
        StatusCustomer statusCustomer = null;
        try {
            EJBRequest request = new EJBRequest();
            request.setParam(Constants.STATUS_CUSTOMER_ACTIVE);
            statusCustomer = personEJB.loadStatusCustomer(request);
        } catch (Exception ex) {
            showError(ex);
        }
        return statusCustomer;
    }
    
    public PersonClassification getClassificationCustomer() {
        PersonClassification personClassification = null;
        try {
            EJBRequest request = new EJBRequest();
            request.setParam(Constants.PERSON_CLASSIFICATION_CUSTOMER);
            personClassification = utilsEJB.loadPersonClassification(request);
        } catch (Exception ex) {
            showError(ex);
        }
        return personClassification;
    }

    public void saveLegalCustomer(AdminRequestController adminRequest) {
        List<LegalPersonHasLegalRepresentatives> legalRepresentativesByApplicantList = null;
        List<PersonHasAddress> AddressByApplicantList = null;
        PersonHasAddress personHasAddress = null;
        List<CardRequestNaturalPerson> cardAdditionalList = null;
        CardRequestNaturalPerson cardRequestNaturalPerson = null;
        try {
            Person person = new Person();
            LegalCustomer legalCustomer = new LegalCustomer();
            LegalPerson applicant = adminRequest.getRequest().getPersonId().getLegalPerson();

            //1. Se crea la persona asociada al cliente
            person.setCountryId(adminRequest.getRequest().getPersonId().getCountryId());
            person.setPersonTypeId(adminRequest.getRequest().getPersonId().getPersonTypeId());
            person.setEmail(adminRequest.getRequest().getPersonId().getEmail());
            person.setPersonClassificationId(getClassificationCustomer());
            person.setCreateDate(new Timestamp(new Date().getTime()));
            person = personEJB.savePerson(person);
            
            //2. Se crea el cliente        
            legalCustomer.setPersonId(person);
            legalCustomer.setDocumentsPersonTypeId(applicant.getDocumentsPersonTypeId());
            legalCustomer.setIdentificationNumber(applicant.getIdentificationNumber());
            legalCustomer.setTradeName(applicant.getTradeName());
            legalCustomer.setEnterpriseName(applicant.getEnterpriseName());
            legalCustomer.setStatusCustomerId(getStatusActiveCustomer());
            legalCustomer.setEconomicActivityId(applicant.getEconomicActivityId());
            legalCustomer.setDateInscriptionRegister(applicant.getDateInscriptionRegister());
            legalCustomer.setRegisterNumber(applicant.getRegisterNumber());
            legalCustomer.setPayedCapital(applicant.getPayedCapital());
            legalCustomer.setWebSite(applicant.getWebSite());
            legalCustomer.setCreateDate(new Timestamp(new Date().getTime()));
            legalCustomer = personEJB.saveLegalCustomer(legalCustomer);
            
            //3 Agregar la dirección del cliente
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_KEY, applicant.getPersonId().getId());
            request.setParams(params);
            AddressByApplicantList = personEJB.getPersonHasAddressesByPerson(request);
            for (PersonHasAddress addressApplicant : AddressByApplicantList){
                personHasAddress = new PersonHasAddress();
                personHasAddress.setAddressId(addressApplicant.getAddressId());
                personHasAddress.setPersonId(person);
                personHasAddress = personEJB.savePersonHasAddress(personHasAddress);
            }
            
            //4. Agregar los representantes legales asociados al cliente
            request = new EJBRequest();
            params = new HashMap();
            params.put(Constants.APPLICANT_LEGAL_PERSON_KEY, applicant.getId());
            request.setParams(params);
            legalRepresentativesByApplicantList = personEJB.getLegalRepresentativesesBylegalPerson(request);
            for (LegalPersonHasLegalRepresentatives legalRepresentatives: legalRepresentativesByApplicantList) {
                LegalCustomerHasLegalRepresentatives legalRepresentativesByCustomer = new LegalCustomerHasLegalRepresentatives();
                legalRepresentativesByCustomer.setLegalCustomerId(legalCustomer);
                legalRepresentativesByCustomer.setLegalRepresentativesId(legalRepresentatives.getLegalRepresentativesid());
                legalRepresentativesByCustomer = personEJB.saveLegalCustomerHasLegalRepresentatives(legalRepresentativesByCustomer);
            }
            
            //5. Agregar las tarjetas adicionales asociadas al cliente
            request = new EJBRequest();
            params = new HashMap();
            params.put(Constants.APPLICANT_LEGAL_PERSON_KEY, applicant.getId());
            request.setParams(params);
            cardAdditionalList = personEJB.getCardRequestNaturalPersonsByLegalApplicant(request);
            for (CardRequestNaturalPerson cardAdditional : cardAdditionalList) {
                cardRequestNaturalPerson = cardAdditional;
                cardRequestNaturalPerson.setLegalCustomerId(legalCustomer);
                cardRequestNaturalPerson = personEJB.saveCardRequestNaturalPerson(cardRequestNaturalPerson);
            }
            
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
            Logger.getLogger(AdminApplicationReviewController.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (GeneralException ex) {
            Logger.getLogger(AdminApplicationReviewController.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (NullParameterException ex) {
            Logger.getLogger(AdminApplicationReviewController.class
                    .getName()).log(Level.SEVERE, null, ex);
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
