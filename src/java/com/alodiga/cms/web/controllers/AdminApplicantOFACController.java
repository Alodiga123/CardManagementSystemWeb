package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.ApplicantNaturalPerson;
import com.cms.commons.models.FamilyReferences;
import com.cms.commons.models.KinShipApplicant;
import com.cms.commons.models.NaturalCustomer;
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
import org.zkoss.zul.Textbox;
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
    private ApplicantNaturalPerson applicantNaturalPersonParam;
    private Button btnSave;
    private Integer eventType;
    public Window winAdminApplicantOFAC;
    public String indGender = null;
    private int optionMenu;
    private RequestEJB requestEJB = null;
    private AdminRequestController adminRequest = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                applicantNaturalPersonParam = (ApplicantNaturalPerson) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_VIEW:
                applicantNaturalPersonParam = (ApplicantNaturalPerson) Sessions.getCurrent().getAttribute("object");
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
            optionMenu = (Integer) session.getAttribute(WebConstants.OPTION_MENU);
            adminRequest = new AdminRequestController();
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
    }

    private void loadFields(ApplicantNaturalPerson applicant) {
        ReviewOFAC reviewOFAC = null;
        Float percentageMatchApplicant = 0.00F;
        try {
            StringBuilder builder = new StringBuilder(applicant.getFirstNames());
            builder.append(" ");
            builder.append(applicant.getLastNames());  
            lblNameApplicant.setValue(builder.toString());
            lblDocumentTypeApplicant.setValue(applicant.getDocumentsPersonTypeId().getDescription());
            lblNoIdentificationApplicant.setValue(applicant.getIdentificationNumber());
            lblKinShipApplicant.setValue(applicant.getKinShipApplicantId().getDescription());
            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_KEY, applicant.getPersonId().getId());
            params.put(Constants.REQUESTS_KEY, adminRequest.getRequest().getId());
            request.setParams(params);
            List<ReviewOFAC> reviewOFACList = requestEJB.getReviewOFACByApplicantByRequest(request);
            for (ReviewOFAC r: reviewOFACList) {
                reviewOFAC = r;
            }
            percentageMatchApplicant = Float.parseFloat(reviewOFAC.getResultReview())*100;
            lblPercentageMatchApplicant.setValue(percentageMatchApplicant.toString());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
    }

    private void saveApplicantOFAC(ApplicantNaturalPerson _applicantNaturalPerson) {
        try {
            ApplicantNaturalPerson applicantNaturalPerson = _applicantNaturalPerson;

            //Guarda el cambio de status en el solicitante
            applicantNaturalPerson.setStatusApplicantId((StatusApplicant) cmbStatusApplicant.getSelectedItem().getValue());
            applicantNaturalPerson = personEJB.saveApplicantNaturalPerson(applicantNaturalPerson);
            this.showMessage("sp.common.save.success", false, null);
            EventQueues.lookup("updateApplicantOFAC", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                saveApplicantOFAC(applicantNaturalPersonParam);
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
                loadFields(applicantNaturalPersonParam);
                loadCmbStatusApplicant(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(applicantNaturalPersonParam);
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
            loadGenericCombobox(statusApplicantList, cmbStatusApplicant, "description", evenInteger, Long.valueOf(applicantNaturalPersonParam != null ? applicantNaturalPersonParam.getStatusApplicantId().getId() : 0));
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
