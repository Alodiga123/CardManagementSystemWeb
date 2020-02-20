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
import com.cms.commons.models.CivilStatus;
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tab;

public class AdminNaturalPersonController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentificationNumber;
    private Textbox txtIdentificationNumberOld;
    private Textbox txtFullName;
    private Textbox txtFullLastName;
    private Textbox txtMarriedLastName;
    private Textbox txtBirthPlace;
    private Textbox txtPhoneNumber;
    private Textbox txtFamilyResponsibilities;
    private Textbox txtEmail;
    private Combobox cmbCountry;
    private Combobox cmbDocumentsPersonType;
    private Combobox cmbCivilState;
    private Combobox cmbProfession;
    private Combobox cmbPhoneType;
    private Datebox txtBirthDay;
    private Datebox txtDueDateDocumentIdentification;
    private Radio genderMale;
    private Radio genderFemale;
    public String indGender = null;
    private Tab tabAddress;
    private Tab tabFamilyReferencesMain;
    private Tab tabAdditionalCards;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private RequestEJB requestEJB = null;
    private Person person;
    private Button btnSave;
    private Integer eventType;
    public static Person applicant = null;
    public static ApplicantNaturalPerson applicantNaturalPersonParent = null;
    private AdminRequestController adminRequest = null;
    public Person applicantPersonParam;
    public ApplicantNaturalPerson applicantNaturalPersonParam;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        AdminRequestController adminRequest = new AdminRequestController();
        if (adminRequest.getEventType()!= null) {
           eventType = adminRequest.getEventType();
           switch (eventType) {
                case WebConstants.EVENT_EDIT:
                    if (adminRequest.getRequest().getPersonId() != null) {
                        tabAddress.setDisabled(false);
                        tabFamilyReferencesMain.setDisabled(false);
                        tabAdditionalCards.setDisabled(false);
                    } else {
                        tabAddress.setDisabled(true);
                        tabFamilyReferencesMain.setDisabled(true);
                        tabAdditionalCards.setDisabled(true);
                    }
                    if (adminRequest.getRequest().getPersonId() != null) {
                        applicantNaturalPersonParam = adminRequest.getRequest().getPersonId().getApplicantNaturalPerson();
                    } else {
                        applicantNaturalPersonParam = null;
                    }
                break;
                case WebConstants.EVENT_VIEW:
                    if (adminRequest.getRequest().getPersonId() != null) {
                        tabAddress.setDisabled(false);
                        tabFamilyReferencesMain.setDisabled(false);
                        tabAdditionalCards.setDisabled(false);
                    } else {
                        tabAddress.setDisabled(true);
                        tabFamilyReferencesMain.setDisabled(true);
                        tabAdditionalCards.setDisabled(true);
                    }
                    if (adminRequest.getRequest().getPersonId() != null) {
                        applicantNaturalPersonParam = adminRequest.getRequest().getPersonId().getApplicantNaturalPerson();
                    } else {
                        applicantNaturalPersonParam = null;
                    }
                break;
                case WebConstants.EVENT_ADD:
                    applicantNaturalPersonParam = null;
                    tabAddress.setDisabled(true);
                    tabFamilyReferencesMain.setDisabled(true);
                    tabAdditionalCards.setDisabled(true);
                break;
           }
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public Person getApplicant() {
        return applicant;
    }
     
    public ApplicantNaturalPerson getApplicantNaturalPerson() {
        return applicantNaturalPersonParent;
    }
    
    public void onChange$cmbCountry() {
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
        txtMarriedLastName.setRawValue(null);
        txtBirthPlace.setRawValue(null);
        txtBirthDay.setRawValue(null);
        txtFamilyResponsibilities.setRawValue(null);
        txtEmail.setRawValue(null);
        txtPhoneNumber.setRawValue(null);
    }

    private void loadFields(ApplicantNaturalPerson applicantNaturalPerson) {
        try {
            txtIdentificationNumber.setText(applicantNaturalPerson.getIdentificationNumber());
            txtDueDateDocumentIdentification.setValue(applicantNaturalPerson.getDueDateDocumentIdentification());
            txtIdentificationNumberOld.setText(applicantNaturalPerson.getIdentificationNumberOld());
            txtFullName.setText(applicantNaturalPerson.getFirstNames());
            txtFullLastName.setText(applicantNaturalPerson.getLastNames());
            txtMarriedLastName.setText(applicantNaturalPerson.getMarriedLastName());
            txtBirthPlace.setText(applicantNaturalPerson.getPlaceBirth());
            txtBirthDay.setValue(applicantNaturalPerson.getDateBirth());
            txtFamilyResponsibilities.setText(applicantNaturalPerson.getFamilyResponsibilities().toString());
            txtEmail.setText(applicantNaturalPerson.getPersonId().getEmail());
            txtPhoneNumber.setText(applicantNaturalPerson.getPersonId().getPhonePerson().getNumberPhone());
            if (applicantNaturalPerson.getGender() == "M") {
                genderMale.setChecked(true);
            } else {
                genderFemale.setChecked(true);
            }
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
        txtMarriedLastName.setReadonly(true);
        txtBirthPlace.setReadonly(true);
        txtBirthDay.setReadonly(true);
        txtFamilyResponsibilities.setReadonly(true);
        txtEmail.setReadonly(true);
        txtPhoneNumber.setReadonly(true);
        cmbCountry.setReadonly(true);
        cmbCivilState.setReadonly(true);
        cmbProfession.setReadonly(true);
        cmbPhoneType.setReadonly(true);
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
        } else if (txtPhoneNumber.getText().isEmpty()) {
            txtPhoneNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveNaturalPerson(ApplicantNaturalPerson _applicantNaturalPerson) {
        ApplicantNaturalPerson applicantNaturalPerson = null;
        AdminRequestController adminRequest = new AdminRequestController();
        try {
            Person person = null;
            PhonePerson phonePerson = null;

            if (_applicantNaturalPerson != null) {
                applicantNaturalPerson = _applicantNaturalPerson;
                person = applicantNaturalPerson.getPersonId();
                phonePerson = applicantNaturalPerson.getPersonId().getPhonePerson();
            } else {//New ApplicantNaturalPerson
                applicantNaturalPerson = new ApplicantNaturalPerson();
                person = new Person();
                phonePerson = new PhonePerson();
            }
            
            if (genderFemale.isChecked()) {
                indGender = "F";
            } else {
                indGender = "M";
            }

            //Obtener la clasificacion del solicitante
            EJBRequest request1 = new EJBRequest();
            request1.setParam(Constants.CLASSIFICATION_PERSON_APPLICANT);
            PersonClassification personClassification = utilsEJB.loadPersonClassification(request1);

            //Guardar la persona
            person.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            person.setPersonTypeId(adminRequest.getRequest().getPersonTypeId());
            person.setEmail(txtEmail.getText());
            person.setCreateDate(new Timestamp(new Date().getTime()));
            person.setPersonClassificationId(personClassification);
            person = personEJB.savePerson(person);
            applicant = person;

            //naturalPerson            
            applicantNaturalPerson.setPersonId(person);
            applicantNaturalPerson.setIdentificationNumber(txtIdentificationNumber.getText());
            applicantNaturalPerson.setDueDateDocumentIdentification(txtDueDateDocumentIdentification.getValue());
            applicantNaturalPerson.setIdentificationNumberOld(txtIdentificationNumberOld.getText());
            applicantNaturalPerson.setFirstNames(txtFullName.getText());
            applicantNaturalPerson.setLastNames(txtFullLastName.getText());
            applicantNaturalPerson.setMarriedLastName(txtMarriedLastName.getText());
            applicantNaturalPerson.setGender(indGender);
            applicantNaturalPerson.setPlaceBirth(txtBirthPlace.getText());
            applicantNaturalPerson.setDateBirth(txtBirthDay.getValue());
            applicantNaturalPerson.setFamilyResponsibilities(Integer.parseInt(txtFamilyResponsibilities.getText()));
            applicantNaturalPerson.setCivilStatusId((CivilStatus) cmbCivilState.getSelectedItem().getValue());
            applicantNaturalPerson.setProfessionId((Profession) cmbProfession.getSelectedItem().getValue());
            applicantNaturalPerson.setCreateDate(new Timestamp(new Date().getTime()));
            applicantNaturalPerson.setDocumentsPersonTypeId((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue());
            applicantNaturalPerson = personEJB.saveApplicantNaturalPerson(applicantNaturalPerson);
            applicantNaturalPersonParent = applicantNaturalPerson;
            
            //Actualizar Solicitante en la Solicitud de Tarjeta
            if (adminRequest.getRequest() != null) {
                Request requestCard = adminRequest.getRequest();
                requestCard.setPersonId(person);
                requestEJB.saveRequest(requestCard); 
            }
            
            //phonePerson
            phonePerson.setNumberPhone(txtPhoneNumber.getText());
            phonePerson.setPersonId(person);
            phonePerson.setPhoneTypeId((PhoneType) cmbPhoneType.getSelectedItem().getValue());
            phonePerson = personEJB.savePhonePerson(phonePerson);
            this.showMessage("sp.common.save.success", false, null);
            tabAddress.setDisabled(false);
            tabFamilyReferencesMain.setDisabled(false);
            tabAdditionalCards.setDisabled(false);

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
                loadCmbCountry(eventType);
                if (applicantNaturalPersonParam != null) {
                    applicantNaturalPersonParent = applicantNaturalPersonParam;
                    applicant = applicantNaturalPersonParam.getPersonId();
                    loadFields(applicantNaturalPersonParam);
                    onChange$cmbCountry();
                }
                loadCmbCivilState(eventType);
                loadCmbPhoneType(eventType);
                loadCmbProfession(eventType);
                break;
            case WebConstants.EVENT_VIEW:               
                loadCmbCountry(eventType);
                if (applicantNaturalPersonParam != null) {
                    applicantNaturalPersonParent = applicantNaturalPersonParam;
                    applicant = applicantNaturalPersonParam.getPersonId();
                    loadFields(applicantNaturalPersonParam);
                    blockFields();
                    onChange$cmbCountry();
                }
                loadCmbCivilState(eventType);
                loadCmbPhoneType(eventType);
                loadCmbProfession(eventType);
                break;
            case WebConstants.EVENT_ADD:
                applicantNaturalPersonParent = null;
                loadCmbCountry(eventType);
                loadCmbCivilState(eventType);
                loadCmbPhoneType(eventType);
                loadCmbProfession(eventType);
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

    private void loadCmbPhoneType(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<PhoneType> phoneType;

        try {
            phoneType = personEJB.getPhoneType(request1);
            loadGenericCombobox(phoneType, cmbPhoneType, "description", evenInteger, Long.valueOf(applicantNaturalPersonParam != null ? applicantNaturalPersonParam.getPersonId().getPhonePerson().getPhoneTypeId().getId() : 0));  
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

}
