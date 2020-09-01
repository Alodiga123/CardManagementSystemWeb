package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.ApplicantNaturalPerson;
import com.cms.commons.models.CardRequestNaturalPerson;
import com.cms.commons.models.LegalPerson;
import com.cms.commons.models.LegalRepresentatives;
import com.cms.commons.models.Person;
import com.cms.commons.models.ReviewOFAC;
import com.cms.commons.models.StatusApplicant;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

public class AdminApplicantOFACController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;

    private Label lblNameApplicant;
    private Label lblDocumentTypeApplicant;
    private Label lblNoIdentificationApplicant;
    private Label lblKinShipApplicant;
    private Label lblPercentageMatchApplicant;
    private Combobox cmbStatusApplicant;
    private PersonEJB personEJB = null;
    private Person applicantParam;
    private Button btnSave;
    private Integer eventType;
    public Window winAdminApplicantOFAC;
    public String indGender = null;
    private Long optionMenu;
    private RequestEJB requestEJB = null;
    private UtilsEJB utilsEJB = null;
    private AdminRequestController adminRequest = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                applicantParam = (Person) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_VIEW:
                applicantParam = (Person) Sessions.getCurrent().getAttribute("object");
                break;
        }    
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            optionMenu = (Long) session.getAttribute(WebConstants.OPTION_MENU);
            adminRequest = new AdminRequestController();
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
    }

    private void loadFields(Person applicant) {
        ReviewOFAC reviewOFAC = null;
        Float percentageMatchApplicant = 0.00F;
        try {
            if (adminRequest.getRequest().getPersonTypeId().getIndNaturalPerson() == true) {
                StringBuilder builder = new StringBuilder(applicant.getApplicantNaturalPerson().getFirstNames());
                builder.append(" ");
                builder.append(applicant.getApplicantNaturalPerson().getLastNames());  
                lblNameApplicant.setValue(builder.toString());
                lblDocumentTypeApplicant.setValue(applicant.getApplicantNaturalPerson().getDocumentsPersonTypeId().getDescription());
                lblNoIdentificationApplicant.setValue(applicant.getApplicantNaturalPerson().getIdentificationNumber());
                if (applicant.getApplicantNaturalPerson().getKinShipApplicantId() != null) {
                    lblKinShipApplicant.setValue(applicant.getApplicantNaturalPerson().getKinShipApplicantId().getDescription());
                } else {
                    lblKinShipApplicant.setValue(WebConstants.MAIN_APPLICANT);
                }
                percentageMatchApplicant = Float.parseFloat(getReviewOFAC(applicant).getResultReview())*100;
                lblPercentageMatchApplicant.setValue(percentageMatchApplicant.toString());
            } else {
                if (applicant.getPersonClassificationId().getId() == 4) {
                    lblNameApplicant.setValue(applicant.getLegalPerson().getEnterpriseName());
                    lblDocumentTypeApplicant.setValue(applicant.getLegalPerson().getDocumentsPersonTypeId().getDescription());
                    lblNoIdentificationApplicant.setValue(applicant.getLegalPerson().getIdentificationNumber());
                    lblKinShipApplicant.setValue(WebConstants.MAIN_APPLICANT);
                    percentageMatchApplicant = Float.parseFloat(getReviewOFAC(applicant).getResultReview())*100;
                    lblPercentageMatchApplicant.setValue(percentageMatchApplicant.toString());
                } else if (applicant.getPersonClassificationId().getId() == 8) {
                    StringBuilder builder = new StringBuilder(applicant.getCardRequestNaturalPerson().getFirstNames());
                    builder.append(" ");
                    builder.append(applicant.getCardRequestNaturalPerson().getLastNames());  
                    lblNameApplicant.setValue(builder.toString());
                    lblDocumentTypeApplicant.setValue(applicant.getCardRequestNaturalPerson().getDocumentsPersonTypeId().getDescription());
                    lblNoIdentificationApplicant.setValue(applicant.getCardRequestNaturalPerson().getIdentificationNumber());
                    lblKinShipApplicant.setValue(WebConstants.CARD_REQUEST_NATURAL_PERSON);
                    percentageMatchApplicant = Float.parseFloat(getReviewOFAC(applicant).getResultReview())*100;
                    lblPercentageMatchApplicant.setValue(percentageMatchApplicant.toString());
                } else if (applicant.getPersonClassificationId().getId() == 5) {
                    StringBuilder builder = new StringBuilder(applicant.getLegalRepresentatives().getFirstNames());
                    builder.append(" ");
                    builder.append(applicant.getLegalRepresentatives().getLastNames());  
                    lblNameApplicant.setValue(builder.toString());
                    lblDocumentTypeApplicant.setValue(applicant.getLegalRepresentatives().getDocumentsPersonTypeId().getDescription());
                    lblNoIdentificationApplicant.setValue(applicant.getLegalRepresentatives().getIdentificationNumber());
                    lblKinShipApplicant.setValue(WebConstants.LEGAL_REPRESENTATIVE);
                    percentageMatchApplicant = Float.parseFloat(getReviewOFAC(applicant).getResultReview())*100;
                    lblPercentageMatchApplicant.setValue(percentageMatchApplicant.toString());
                }
            }            
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public ReviewOFAC getReviewOFAC(Person applicant) {
        ReviewOFAC reviewOFAC = null;
        try {
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_KEY, applicant.getId());
            params.put(Constants.REQUESTS_KEY, adminRequest.getRequest().getId());
            request.setParams(params);
            List<ReviewOFAC> reviewOFACList = requestEJB.getReviewOFACByApplicantByRequest(request);
            for (ReviewOFAC r: reviewOFACList) {
                reviewOFAC = r;
            }
        } catch (Exception ex) {
            showError(ex);
        }
        return reviewOFAC;
    }

    public void blockFields() {
    }

    private void saveApplicantOFAC(Person _person) {
        try {
            Person person = _person;
            //Guarda el cambio del estatus en el solicitante
            if (adminRequest.getRequest().getPersonTypeId().getIndNaturalPerson() == true) {
                ApplicantNaturalPerson applicantNaturalPerson =  person.getApplicantNaturalPerson();
                applicantNaturalPerson.setStatusApplicantId((StatusApplicant) cmbStatusApplicant.getSelectedItem().getValue());
                applicantNaturalPerson = personEJB.saveApplicantNaturalPerson(person.getApplicantNaturalPerson());
            } else if (person.getPersonClassificationId().getId() == 4) {
                LegalPerson legalPerson = person.getLegalPerson();
                legalPerson.setStatusApplicantId((StatusApplicant) cmbStatusApplicant.getSelectedItem().getValue());
                legalPerson = personEJB.saveLegalegalPerson(legalPerson);
            } else if (person.getPersonClassificationId().getId() == 8) {
                CardRequestNaturalPerson cardRequestnaturalPerson = person.getCardRequestNaturalPerson();
                cardRequestnaturalPerson.setStatusApplicantId((StatusApplicant) cmbStatusApplicant.getSelectedItem().getValue());
                cardRequestnaturalPerson = personEJB.saveCardRequestNaturalPerson(cardRequestnaturalPerson);
            } else if (person.getPersonClassificationId().getId() == 5) {
                LegalRepresentatives legalRepresentative = person.getLegalRepresentatives();
                legalRepresentative.setStatusApplicantId((StatusApplicant) cmbStatusApplicant.getSelectedItem().getValue());
                legalRepresentative = utilsEJB.saveLegalRepresentatives(legalRepresentative);
            }            
            this.showMessage("sp.common.save.success", false, null);
            EventQueues.lookup("updateApplicantOFAC", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                saveApplicantOFAC(applicantParam);
                break;
            default:
                break;
        }
    }

    public void onClick$btnBack() {
        winAdminApplicantOFAC.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(applicantParam);
                loadCmbStatusApplicant(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(applicantParam);
                loadCmbStatusApplicant(eventType);
                blockFields();
                break;
            }
    }
    
    private void loadCmbStatusApplicant(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<StatusApplicant> statusApplicantList;
        try {
            statusApplicantList = requestEJB.getStatusApplicant(request1);
            if (adminRequest.getRequest().getPersonTypeId().getIndNaturalPerson() == true) {
                loadGenericCombobox(statusApplicantList, cmbStatusApplicant, "description", evenInteger, Long.valueOf(applicantParam != null ? applicantParam.getApplicantNaturalPerson().getStatusApplicantId().getId() : 0));
            } else if (applicantParam.getPersonClassificationId().getId() == 4) {
                loadGenericCombobox(statusApplicantList, cmbStatusApplicant, "description", evenInteger, Long.valueOf(applicantParam != null ? applicantParam.getLegalPerson().getStatusApplicantId().getId() : 0));
            } else if (applicantParam.getPersonClassificationId().getId() == 8) {
                loadGenericCombobox(statusApplicantList, cmbStatusApplicant, "description", evenInteger, Long.valueOf(applicantParam != null ? applicantParam.getCardRequestNaturalPerson().getStatusApplicantId().getId() : 0));
            } else if (applicantParam.getPersonClassificationId().getId() == 5) {
                loadGenericCombobox(statusApplicantList, cmbStatusApplicant, "description", evenInteger, Long.valueOf(applicantParam != null ? applicantParam.getLegalRepresentatives().getStatusApplicantId().getId() : 0));
            }
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
