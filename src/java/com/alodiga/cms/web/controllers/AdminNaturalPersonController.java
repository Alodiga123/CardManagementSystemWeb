package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.CivilStatus;
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.EconomicActivity;
import com.cms.commons.models.NaturalPerson;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.impl.conn.Wire;
import org.codehaus.groovy.tools.shell.Command;
import org.jboss.weld.metadata.Selectors;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;

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
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private NaturalPerson naturalPersonParam;
    private Person person;
    private Button btnSave;
    private Integer eventType;
    public Tabbox tb;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        naturalPersonParam = (Sessions.getCurrent().getAttribute("object") != null) ? (NaturalPerson) Sessions.getCurrent().getAttribute("object") : null;
        //eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        eventType = 1;
        initialize();
        //initView(eventType, "sp.crud.country");
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

    private void loadFields(NaturalPerson naturalPerson) {
        try {
            txtIdentificationNumber.setText(naturalPerson.getIdentificationNumber());
            txtDueDateDocumentIdentification.setText(naturalPerson.getDueDateDocumentIdentification().toString());
            txtIdentificationNumberOld.setText(naturalPerson.getIdentificationNumberOld());
            txtFullName.setText(naturalPerson.getFirstNames());
            txtFullLastName.setText(naturalPerson.getLastNames());
            txtMarriedLastName.setText(naturalPerson.getMarriedLastName());
            txtBirthPlace.setText(naturalPerson.getPlaceBirth());
            txtBirthDay.setText(naturalPerson.getDateBirth().toString());
            txtFamilyResponsibilities.setText(naturalPerson.getFamilyResponsibilities().toString());
            txtEmail.setText(naturalPerson.getPersonId().getEmail());
            txtPhoneNumber.setText(naturalPerson.getPersonId().getPhonePerson().getNumberPhone());
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

    private void saveNaturalPerson(NaturalPerson _naturalPerson) {
        tabAddress.setSelected(true);
        try {
            NaturalPerson naturalPerson = null;
            Person person = null;
            PhonePerson phonePerson = null;

            if (_naturalPerson != null) {
                naturalPerson = _naturalPerson;
            } else {//New LegalPerson
                naturalPerson = new NaturalPerson();
                person = new Person();
                phonePerson = new PhonePerson();
            }

            //Request
            EJBRequest request1 = new EJBRequest();
            request1.setParam(Constants.REQUEST_ID_NATURAL_PERSON);
            Request request = utilsEJB.loadRequest(request1);

            //PersonClassification
            request1 = new EJBRequest();
            request1.setParam(Constants.CLASSIFICATION_PERSON_APPLICANT);
            PersonClassification personClassification = utilsEJB.loadPersonClassification(request1);

            //Person
            String id = cmbCountry.getSelectedItem().getParent().getId();
            person.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            person.setPersonTypeId(request.getPersonTypeId());
            person.setEmail(txtEmail.getText());
            person.setCreateDate(new Timestamp(new Date().getTime()));
            person.setPersonClassificationId(personClassification);
            person = personEJB.savePerson(person);

            //naturalPerson            
            naturalPerson.setPersonId(person);
            naturalPerson.setIdentificationNumber(txtIdentificationNumber.getText());
            naturalPerson.setDueDateDocumentIdentification(txtDueDateDocumentIdentification.getValue());
            naturalPerson.setIdentificationNumberOld(txtIdentificationNumberOld.getText());
            naturalPerson.setFirstNames(txtFullName.getText());
            naturalPerson.setLastNames(txtFullLastName.getText());
            naturalPerson.setMarriedLastName(txtMarriedLastName.getText());
            if (genderFemale.isChecked()) {
                indGender = "F";
            } else {
                indGender = "M";
            }
            naturalPerson.setGender(indGender);
            naturalPerson.setPlaceBirth(txtBirthPlace.getText());
            naturalPerson.setDateBirth(txtBirthDay.getValue());
            naturalPerson.setFamilyResponsibilities(Integer.parseInt(txtFamilyResponsibilities.getText()));
            naturalPerson.setCivilStatusId((CivilStatus) cmbCivilState.getSelectedItem().getValue());
            naturalPerson.setProfessionId((Profession) cmbProfession.getSelectedItem().getValue());
            naturalPerson.setCreateDate(new Timestamp(new Date().getTime()));
            naturalPerson.setDocumentsPersonTypeId((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue());
            naturalPerson = personEJB.saveNaturalPerson(naturalPerson);
            naturalPersonParam = naturalPerson;

            //phonePerson
            phonePerson.setNumberPhone(txtPhoneNumber.getText());
            phonePerson.setPersonId(person);
            phonePerson.setPhoneTypeId((PhoneType) cmbPhoneType.getSelectedItem().getValue());
            phonePerson = utilsEJB.savePhonePerson(phonePerson);
            this.showMessage("sp.common.save.success", false, null);

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
                    saveNaturalPerson(naturalPersonParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(naturalPersonParam);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                loadCmbCivilState(eventType);
                loadCmbPhoneType(eventType);
                loadCmbProfession(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(naturalPersonParam);
                txtIdentificationNumber.setDisabled(true);
                txtDueDateDocumentIdentification.setDisabled(true);
                txtIdentificationNumberOld.setDisabled(true);
                txtFullName.setDisabled(true);
                txtFullLastName.setDisabled(true);
                txtMarriedLastName.setDisabled(true);
                txtBirthPlace.setDisabled(true);
                txtBirthDay.setDisabled(true);
                txtFamilyResponsibilities.setDisabled(true);
                txtEmail.setDisabled(true);
                txtPhoneNumber.setDisabled(true);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                loadCmbCivilState(eventType);
                loadCmbPhoneType(eventType);
                loadCmbProfession(eventType);
                break;
            case WebConstants.EVENT_ADD:
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(naturalPersonParam != null ? naturalPersonParam.getPersonId().getCountryId().getId() : 0));
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
        //cmbDocumentsPersonType
        EJBRequest request1 = new EJBRequest();
        cmbDocumentsPersonType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        request1.setParams(params);
        List<DocumentsPersonType> documentsPersonType;
        try {
            documentsPersonType = utilsEJB.getDocumentsPersonByCity(request1);
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger, Long.valueOf(naturalPersonParam != null ? naturalPersonParam.getDocumentsPersonTypeId().getId() : 0));
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
            civilStatuses = utilsEJB.getCivilStatus(request1);
            loadGenericCombobox(civilStatuses, cmbCivilState, "description", evenInteger, Long.valueOf(naturalPersonParam != null ? naturalPersonParam.getPersonId().getNaturalPerson().getCivilStatusId().getId() : 0));
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
        //cmbPhoneType
        EJBRequest request1 = new EJBRequest();
        List<PhoneType> phoneType;

        try {
            phoneType = personEJB.getPhoneType(request1);
            loadGenericCombobox(phoneType, cmbPhoneType, "description", evenInteger, Long.valueOf(naturalPersonParam != null ? naturalPersonParam.getPersonId().getPhonePerson().getPhoneTypeId().getId() : 0));
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
            profession = utilsEJB.getProfession(request1);
            loadGenericCombobox(profession, cmbProfession, "name", evenInteger, Long.valueOf(naturalPersonParam != null ? naturalPersonParam.getProfessionId().getId() : 0));
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
