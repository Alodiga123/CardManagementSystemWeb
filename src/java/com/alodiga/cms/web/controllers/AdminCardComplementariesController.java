package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.ApplicantNaturalPerson;
import com.cms.commons.models.CivilStatus;
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.KinShipApplicant;
import com.cms.commons.models.Person;
import com.cms.commons.models.PersonClassification;
import com.cms.commons.models.PhonePerson;
import com.cms.commons.models.PhoneType;
import com.cms.commons.models.Profession;
import com.cms.commons.models.Request;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

public class AdminCardComplementariesController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentificationNumber;
    private Textbox txtIdentificationNumberOld;
    private Textbox txtFullName;
    private Textbox txtFullLastName;
    private Textbox txtBirthPlace;
    private Textbox txtLocalPhone;
    private Textbox txtCellPhone;
    private Textbox txtEmail;
    private Combobox cmbCountry;
    private Combobox cmbDocumentsPersonType;
    private Combobox cmbCivilState;
    private Combobox cmbProfession;
    private Combobox cmbRelationship;
    private Datebox txtBirthDay;
    private Datebox txtDueDateDocumentIdentification;
    private Radio genderMale;
    private Radio genderFemale;
    public String indGender = null;
    private Tab tabAddress;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private ApplicantNaturalPerson applicantNaturalPersonParam;
    private Person person;
    private Button btnSave;
    private Integer eventType;
    public Tabbox tb;
    public static Person personCardComplementary = null;
    private AdminRequestController adminRequest = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        adminRequest = new AdminRequestController();
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        switch (eventType) {
                case WebConstants.EVENT_EDIT:
                    applicantNaturalPersonParam = (ApplicantNaturalPerson) Sessions.getCurrent().getAttribute("object");
                break;
                case WebConstants.EVENT_VIEW:
                    applicantNaturalPersonParam = (ApplicantNaturalPerson) Sessions.getCurrent().getAttribute("object");
                break;
                case WebConstants.EVENT_ADD:
                    applicantNaturalPersonParam = null;
                break;
           }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public Person getPersonCardComplementary() {
        return personCardComplementary;
    }

    public void onChange$cmbCountry() {
        cmbDocumentsPersonType.setValue("");
        cmbDocumentsPersonType.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbDocumentsPersonType(eventType, country.getId());
    }

    public void clearFields() {
        txtIdentificationNumber.setRawValue(null);
        txtDueDateDocumentIdentification.setRawValue(null);
        txtIdentificationNumberOld.setRawValue(null);
        txtFullName.setRawValue(null);
        txtFullLastName.setRawValue(null);
        txtBirthPlace.setRawValue(null);
        txtBirthDay.setRawValue(null);
        txtEmail.setRawValue(null);
        txtLocalPhone.setRawValue(null);
        txtCellPhone.setRawValue(null);
    }

    private void loadFields(ApplicantNaturalPerson applicantNaturalPerson) {
        try {
            txtIdentificationNumber.setText(applicantNaturalPerson.getIdentificationNumber());
            txtDueDateDocumentIdentification.setValue(applicantNaturalPerson.getDueDateDocumentIdentification());
            txtIdentificationNumberOld.setText(applicantNaturalPerson.getIdentificationNumberOld());
            txtFullName.setText(applicantNaturalPerson.getFirstNames());
            txtFullLastName.setText(applicantNaturalPerson.getLastNames());
            if(applicantNaturalPerson.getGender().trim().equalsIgnoreCase("F")){
                genderFemale.setChecked(true);
            }else{
                genderMale.setChecked(true);
            }
            txtBirthPlace.setText(applicantNaturalPerson.getPlaceBirth());
            txtBirthDay.setValue(applicantNaturalPerson.getDateBirth());
            txtEmail.setText(applicantNaturalPerson.getPersonId().getEmail());
            txtLocalPhone.setText(applicantNaturalPerson.getPersonId().getPhonePerson().getNumberPhone());
            txtCellPhone.setText(applicantNaturalPerson.getPersonId().getPhonePerson().getNumberPhone());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtIdentificationNumber.setReadonly(true);
        txtDueDateDocumentIdentification.setReadonly(true);
        txtIdentificationNumberOld.setReadonly(true);
        txtFullName.setReadonly(true);
        txtFullLastName.setReadonly(true);
        txtBirthPlace.setReadonly(true);
        txtBirthDay.setReadonly(true);
        txtEmail.setReadonly(true);
        txtLocalPhone.setReadonly(true);
        txtCellPhone.setReadonly(true);
        cmbCountry.setReadonly(true);
        cmbCivilState.setReadonly(true);
        cmbProfession.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtIdentificationNumber.getText().isEmpty()) {
            txtIdentificationNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtFullName.getText().isEmpty()) {
            txtFullName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtBirthPlace.getText().isEmpty()) {
            txtBirthPlace.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;

    }

    private void saveNaturalPerson(ApplicantNaturalPerson _applicantNaturalPerson) {
        ApplicantNaturalPerson applicantNaturalPersonParent = null;
        Request requestCard = null;
        try {
            ApplicantNaturalPerson applicantNaturalPerson = null;
            Person person = null;

            if (_applicantNaturalPerson != null) {
                applicantNaturalPerson = _applicantNaturalPerson;
                person = applicantNaturalPerson.getPersonId();
            } else {//New ApplicantNaturalPerson
                applicantNaturalPerson = new ApplicantNaturalPerson();
                person = new Person();
            }
            
            if (genderFemale.isChecked()) {
                indGender = "F";
            } else {
                indGender = "M";
            }
            
           //PersonClassification
            EJBRequest request2 = new EJBRequest();
            request2.setParam(Constants.CLASSIFICATION_PERSON_APPLICANT);
            PersonClassification personClassification = utilsEJB.loadPersonClassification(request2);
            
            //Solicitante Principal
            AdminNaturalPersonController adminNaturalPerson = new AdminNaturalPersonController();
            if (adminNaturalPerson.getApplicantNaturalPerson() != null) {
                applicantNaturalPersonParent = adminNaturalPerson.getApplicantNaturalPerson();
            }
            
            //Person
            String id = cmbCountry.getSelectedItem().getParent().getId();
            person.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            person.setPersonTypeId(((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue()).getPersonTypeId());
            person.setEmail(txtEmail.getText());
            person.setCreateDate(new Timestamp(new Date().getTime()));
            person.setPersonClassificationId(personClassification);
            person = personEJB.savePerson(person);
            personCardComplementary = person;
                        
            //naturalPerson            
            applicantNaturalPerson.setPersonId(person);
            applicantNaturalPerson.setIdentificationNumber(txtIdentificationNumber.getText());
            applicantNaturalPerson.setDueDateDocumentIdentification(txtDueDateDocumentIdentification.getValue());
            applicantNaturalPerson.setIdentificationNumberOld(txtIdentificationNumberOld.getText());
            applicantNaturalPerson.setFirstNames(txtFullName.getText());
            applicantNaturalPerson.setLastNames(txtFullLastName.getText());
            applicantNaturalPerson.setGender(indGender);
            applicantNaturalPerson.setPlaceBirth(txtBirthPlace.getText());
            applicantNaturalPerson.setDateBirth(txtBirthDay.getValue());
            applicantNaturalPerson.setCivilStatusId((CivilStatus) cmbCivilState.getSelectedItem().getValue());
            applicantNaturalPerson.setProfessionId((Profession) cmbProfession.getSelectedItem().getValue());
            applicantNaturalPerson.setCreateDate(new Timestamp(new Date().getTime()));
            applicantNaturalPerson.setApplicantParentId(applicantNaturalPersonParent);
            applicantNaturalPerson.setKinShipApplicantId((KinShipApplicant) cmbRelationship.getSelectedItem().getValue());
            applicantNaturalPerson.setDocumentsPersonTypeId((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue());
            applicantNaturalPerson = personEJB.saveApplicantNaturalPerson(applicantNaturalPerson);
            applicantNaturalPersonParam = applicantNaturalPerson;

            //Phone Habitacion
            EJBRequest request4 = new EJBRequest();
            request4.setParam(Constants.PHONE_TYPE_ROOM);
            PhoneType phonePersonH = personEJB.loadPhoneType(request4);
            PhonePerson phonePerson1 = new PhonePerson();
            phonePerson1.setNumberPhone(txtLocalPhone.getText());
            phonePerson1.setPersonId(person);
            phonePerson1.setPhoneTypeId(phonePersonH);
            phonePerson1 = personEJB.savePhonePerson(phonePerson1);
            
            //Phone Celular
            EJBRequest request5 = new EJBRequest();
            request5.setParam(Constants.PHONE_TYPE_MOBILE);
            PhoneType phonePersonC = personEJB.loadPhoneType(request5);
            PhonePerson phonePerson2 = new PhonePerson();
            phonePerson2.setNumberPhone(txtCellPhone.getText());
            phonePerson2.setPersonId(person);
            phonePerson2.setPhoneTypeId(phonePersonC);
            phonePerson2 = personEJB.savePhonePerson(phonePerson2);
            this.showMessage("sp.common.save.success", false, null);
            
            EventQueues.lookup("updateCardComplementaries", EventQueues.APPLICATION, true).publish(new Event(""));

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveNaturalPerson(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveNaturalPerson(applicantNaturalPersonParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(applicantNaturalPersonParam);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                loadCmbCivilState(eventType);
                loadCmbProfession(eventType);
                loadCmbRelationship(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(applicantNaturalPersonParam);
                txtIdentificationNumber.setDisabled(true);
                txtDueDateDocumentIdentification.setDisabled(true);
                txtIdentificationNumberOld.setDisabled(true);
                txtFullName.setDisabled(true);
                txtFullLastName.setDisabled(true);
                txtBirthPlace.setDisabled(true);
                txtBirthDay.setDisabled(true);
                txtEmail.setDisabled(true);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                loadCmbCivilState(eventType);
                loadCmbProfession(eventType);
                loadCmbRelationship(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbCivilState(eventType);
                loadCmbProfession(eventType);
                loadCmbRelationship(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbCountry(Integer evenInteger) {
        //cmbCountry
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;

        try {
            countries = utilsEJB.getCountries(request1);
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(applicantNaturalPersonParam != null ? applicantNaturalPersonParam.getPersonId().getCountryId().getId() : 0));
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

    private void loadCmbDocumentsPersonType(Integer evenInteger, int countryId) {
        EJBRequest request1 = new EJBRequest();
        cmbDocumentsPersonType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        params.put(QueryConstants.PARAM_IND_NATURAL_PERSON, adminRequest.getRequest().getPersonTypeId().getIndNaturalPerson());
        request1.setParams(params);
        List<DocumentsPersonType> documentsPersonType;
        try {
            documentsPersonType = utilsEJB.getDocumentsPersonByCountry(request1);
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger, Long.valueOf(applicantNaturalPersonParam != null ? applicantNaturalPersonParam.getDocumentsPersonTypeId().getId() : 0));
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

    private void loadCmbCivilState(Integer evenInteger) {
        //cmbCivilState
        EJBRequest request1 = new EJBRequest();
        List<CivilStatus> civilStatuses;

        try {
            civilStatuses = personEJB.getCivilStatus(request1);
            loadGenericCombobox(civilStatuses, cmbCivilState, "description", evenInteger, Long.valueOf(applicantNaturalPersonParam != null ? applicantNaturalPersonParam.getPersonId().getApplicantNaturalPerson().getCivilStatusId().getId() : 0));
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

    private void loadCmbProfession(Integer evenInteger) {
        //cmbProfession
        EJBRequest request1 = new EJBRequest();
        List<Profession> profession;

        try {
            profession = personEJB.getProfession(request1);
            loadGenericCombobox(profession, cmbProfession, "name", evenInteger, Long.valueOf(applicantNaturalPersonParam != null ? applicantNaturalPersonParam.getProfessionId().getId() : 0));
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

    private void loadCmbRelationship(Integer evenInteger) {
        //cmbRelationship
        EJBRequest request1 = new EJBRequest();
        List<KinShipApplicant> kinShipApplicant;

        try {
            kinShipApplicant = personEJB.getKinShipApplicant(request1);
            loadGenericCombobox(kinShipApplicant, cmbRelationship, "description", evenInteger, Long.valueOf(applicantNaturalPersonParam != null ? applicantNaturalPersonParam.getKinShipApplicantId().getId() : 0));
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
