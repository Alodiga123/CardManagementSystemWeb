package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.CivilStatus;
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.LegalCustomer;
import com.cms.commons.models.LegalCustomerHasLegalRepresentatives;
import com.cms.commons.models.LegalPerson;
import com.cms.commons.models.LegalPersonHasLegalRepresentatives;
import com.cms.commons.models.LegalRepresentatives;
import com.cms.commons.models.Person;
import com.cms.commons.models.PersonClassification;
import com.cms.commons.models.PhonePerson;
import com.cms.commons.models.PhoneType;
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
import org.zkoss.zul.Window;

public class AdminLegalRepresentativesCardProgramManagerController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentificationNumber;
    private Textbox txtFullName;
    private Textbox txtFullLastName;
    private Textbox txtBirthPlace;
    private Textbox txtAge;
    private Textbox txtPhoneNumber;
    private Combobox cmbCountry;
    private Combobox cmbDocumentsPersonType;
    private Combobox cmbCivilState;
    private Combobox cmbPhoneType;
    private Radio genderMale;
    private Radio genderFemale;
    private Datebox txtDueDateIdentification;
    private Datebox txtBirthDay;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private LegalRepresentatives legalRepresentativesParam;
    private Button btnSave;
    private Integer eventType;
    public Window winAdminLegalRepresentCardProgMan;
    public String indGender = null;
    public AdminCardProgramManagerController adminCardProgramManager = null;
    public AdminLegalPersonController adminLegalPerson = null;
    public AdminLegalPersonCustomerController adminLegalCustomerPerson = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
//        adminRequest = new AdminRequestController();
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);                
        if (eventType == WebConstants.EVENT_ADD) {
            legalRepresentativesParam = null;
        } else {
            legalRepresentativesParam = (LegalRepresentatives) Sessions.getCurrent().getAttribute("object");
        }
        
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                legalRepresentativesParam = (LegalRepresentatives) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_VIEW:
                legalRepresentativesParam = (LegalRepresentatives) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_ADD:
                legalRepresentativesParam = null;
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onChange$cmbCountry() {
        cmbDocumentsPersonType.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbDocumentsPersonType(eventType, country.getId());
    }

    public void clearFields() {
        txtIdentificationNumber.setRawValue(null);
        txtFullName.setRawValue(null);
        txtFullLastName.setRawValue(null);
        txtBirthPlace.setRawValue(null);
        txtAge.setRawValue(null);
        txtPhoneNumber.setRawValue(null);
        txtDueDateIdentification.setRawValue(null);
        txtBirthDay.setRawValue(null);
    }

    private void loadFields(LegalRepresentatives legalRepresentatives) {
        try {
            txtFullName.setText(legalRepresentatives.getFirstNames());
            txtFullLastName.setText(legalRepresentatives.getLastNames());
            txtIdentificationNumber.setText(legalRepresentatives.getIdentificationNumber());
            txtDueDateIdentification.setValue(legalRepresentatives.getDueDateDocumentIdentification());
            txtAge.setText(legalRepresentatives.getAge().toString());
            txtBirthPlace.setText(legalRepresentatives.getPlaceBirth());
            txtBirthDay.setValue(legalRepresentatives.getDateBirth());
            txtPhoneNumber.setText(legalRepresentatives.getPersonId().getPhonePerson().getNumberPhone());
            if (legalRepresentatives.getGender().trim().equalsIgnoreCase("F")) {
                genderFemale.setChecked(true);
            } else {
                genderMale.setChecked(true);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtFullName.setReadonly(true);
        txtFullLastName.setReadonly(true);
        txtIdentificationNumber.setReadonly(true);
        txtDueDateIdentification.setDisabled(true);
        txtAge.setReadonly(true);
        txtBirthPlace.setReadonly(true);
        txtBirthDay.setDisabled(true);
        txtPhoneNumber.setReadonly(true);
        cmbCountry.setDisabled(true);
        cmbDocumentsPersonType.setDisabled(true);
        cmbPhoneType.setDisabled(true);

        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtIdentificationNumber.getText().isEmpty()) {
            txtIdentificationNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtFullName.getText().isEmpty()) {
            txtFullName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtFullLastName.getText().isEmpty()) {
            txtFullLastName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;

    }

    private void saveLegalRepresentatives(LegalRepresentatives _legalRepresentatives) {
        LegalPerson legalPerson = null;
        LegalCustomer legalCustomer = null;
        Person personLegalRepresentatives = null;
        try {
            LegalRepresentatives legalRepresentatives = null;
            LegalPersonHasLegalRepresentatives legalPersonHasLegalRepresentatives = null;
            LegalCustomerHasLegalRepresentatives legalCustomerHasLegalRepresentatives = null;
            PhonePerson phonePerson = null;
            Person person = null;

            if (_legalRepresentatives != null) {
                legalRepresentatives = _legalRepresentatives;
                person = legalRepresentatives.getPersonId();
                phonePerson = legalRepresentatives.getPersonId().getPhonePerson();
            } else {//New LegalPerson
                person = new Person();
                legalRepresentatives = new LegalRepresentatives();
                legalPersonHasLegalRepresentatives = new LegalPersonHasLegalRepresentatives();
                legalCustomerHasLegalRepresentatives = new LegalCustomerHasLegalRepresentatives();
                phonePerson = new PhonePerson();
            }

            if (genderFemale.isChecked()) {
                indGender = "F";
            } else {
                indGender = "M";
            }

            adminLegalPerson = new AdminLegalPersonController();
            adminLegalCustomerPerson = new AdminLegalPersonCustomerController();

            if (adminLegalPerson.getLegalPerson() != null) {
                legalPerson = adminLegalPerson.getLegalPerson();
            } else if (adminLegalCustomerPerson.getLegalCustomer() != null) {
                legalCustomer = adminLegalCustomerPerson.getLegalCustomer();
            }

            //Obtener la clasificacion del Representante Legal
            EJBRequest request1 = new EJBRequest();
            request1.setParam(Constants.CLASSIFICATION_PERSON_LEGAL_REPRESENTATIVES);
            PersonClassification personClassification = utilsEJB.loadPersonClassification(request1);

            //Guardar la persona
            person.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            person.setPersonTypeId(((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue()).getPersonTypeId());
            if (eventType == 1) {
                person.setCreateDate(new Timestamp(new Date().getTime()));
                person.setPersonClassificationId(personClassification);
            }
            person = personEJB.savePerson(person);
            personLegalRepresentatives = person;

            //Guarda el Representante Legal
            legalRepresentatives.setPersonId(personLegalRepresentatives);
            legalRepresentatives.setFirstNames(txtFullName.getText());
            legalRepresentatives.setLastNames(txtFullLastName.getText());
            legalRepresentatives.setIdentificationNumber(txtIdentificationNumber.getText());
            legalRepresentatives.setDueDateDocumentIdentification(txtDueDateIdentification.getValue());
            legalRepresentatives.setAge(Integer.parseInt(txtAge.getText().toString()));
            legalRepresentatives.setGender(indGender);
            legalRepresentatives.setPlaceBirth(txtBirthPlace.getText());
            legalRepresentatives.setDateBirth(txtBirthDay.getValue());
            legalRepresentatives.setDocumentsPersonTypeId((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue());
            legalRepresentatives.setCivilStatusId((CivilStatus) cmbCivilState.getSelectedItem().getValue());
            legalRepresentatives = utilsEJB.saveLegalRepresentatives(legalRepresentatives);
            legalRepresentativesParam = legalRepresentatives;

            //Guarda el telefono del representante legal
            phonePerson.setNumberPhone(txtPhoneNumber.getText());
            phonePerson.setPersonId(personLegalRepresentatives);
            phonePerson.setPhoneTypeId((PhoneType) cmbPhoneType.getSelectedItem().getValue());
            phonePerson = personEJB.savePhonePerson(phonePerson);

            //Asocia el Representante Legal al Solicitante Jurídico
            if (eventType == 1) {
                if (legalPerson != null) {
                    legalPersonHasLegalRepresentatives.setLegalPersonId(legalPerson);
                    legalPersonHasLegalRepresentatives.setLegalRepresentativesid(legalRepresentatives);
                    legalPersonHasLegalRepresentatives = personEJB.saveLegalPersonHasLegalRepresentatives(legalPersonHasLegalRepresentatives);
                } else if (legalCustomer != null) {
                    legalCustomerHasLegalRepresentatives.setLegalCustomerId(legalCustomer);
                    legalCustomerHasLegalRepresentatives.setLegalRepresentativesId(legalRepresentatives);
                    legalCustomerHasLegalRepresentatives = personEJB.saveLegalCustomerHasLegalRepresentatives(legalCustomerHasLegalRepresentatives);
                }
            }
            this.showMessage("sp.common.save.success", false, null);
            EventQueues.lookup("updateLegalRepresentative", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveLegalRepresentatives(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveLegalRepresentatives(legalRepresentativesParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void onClick$btnBack() {
        winAdminLegalRepresentCardProgMan.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(legalRepresentativesParam);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                loadCmbCivilState(eventType);
                loadCmbPhoneType(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(legalRepresentativesParam);
                txtIdentificationNumber.setDisabled(true);
                txtFullName.setDisabled(true);
                txtFullLastName.setDisabled(true);
                txtBirthPlace.setDisabled(true);
                txtAge.setDisabled(true);
                txtPhoneNumber.setDisabled(true);
                txtDueDateIdentification.setDisabled(true);
                txtBirthDay.setDisabled(true);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                loadCmbCivilState(eventType);
                loadCmbPhoneType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbCivilState(eventType);
                loadCmbPhoneType(eventType);
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(legalRepresentativesParam != null ? legalRepresentativesParam.getPersonId().getCountryId().getId() : 0));
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
        params.put(QueryConstants.PARAM_IND_NATURAL_PERSON, WebConstants.IND_NATURAL_PERSON);
        request1.setParams(params);
        List<DocumentsPersonType> documentsPersonType;
        try {
            documentsPersonType = utilsEJB.getDocumentsPersonByCountry(request1);
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger, Long.valueOf(legalRepresentativesParam != null ? legalRepresentativesParam.getDocumentsPersonTypeId().getId() : 0));
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
        List<CivilStatus> civilStatusList;

        try {
            civilStatusList = personEJB.getCivilStatus(request1);
            loadGenericCombobox(civilStatusList, cmbCivilState, "description", evenInteger, Long.valueOf(legalRepresentativesParam != null ? legalRepresentativesParam.getCivilStatusId().getId() : 0));
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
            loadGenericCombobox(phoneType, cmbPhoneType, "description", evenInteger, Long.valueOf(legalRepresentativesParam != null ? legalRepresentativesParam.getPersonId().getPhonePerson().getPhoneTypeId().getId() : 0));
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
