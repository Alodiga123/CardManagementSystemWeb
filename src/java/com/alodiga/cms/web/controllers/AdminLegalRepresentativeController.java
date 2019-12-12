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
import com.cms.commons.models.LegalPersonHasLegalRepresentatives;
import com.cms.commons.models.LegalRepresentatives;
import com.cms.commons.models.NaturalPerson;
import com.cms.commons.models.Person;
import com.cms.commons.models.PhonePerson;
import com.cms.commons.models.PhoneType;
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
import javafx.scene.control.RadioButton;
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
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;

public class AdminLegalRepresentativeController extends GenericAbstractAdminController {

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

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        legalRepresentativesParam = (Sessions.getCurrent().getAttribute("object") != null) ? (LegalRepresentatives) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
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

    public void onClick$btnCodes() {
        Executions.getCurrent().sendRedirect("/docs/T-SP-E.164D-2009-PDF-S.pdf", "_blank");
    }

    private void saveLegalRepresentatives(LegalRepresentatives _legalRepresentatives) {
        //tabLegalRepresentatives.setSelected(true);
        String indGender = null;
        try {
            LegalRepresentatives legalRepresentatives = null;
            LegalPersonHasLegalRepresentatives legalPersonHasLegalRepresentatives = null;
            PhonePerson phonePerson = null;

            if (_legalRepresentatives != null) {
                legalRepresentatives = _legalRepresentatives;
            } else {//New LegalPerson
                legalRepresentatives = new LegalRepresentatives();
                legalPersonHasLegalRepresentatives = new LegalPersonHasLegalRepresentatives();
                phonePerson = new PhonePerson();
            }
            //Person
            EJBRequest request1 = new EJBRequest();
            request1.setParam(Constants.PERSON_ID_KEY);
            Person person = personEJB.loadPerson(request1);

            legalRepresentatives.setPersonId(person);
            legalRepresentatives.setFirstNames(txtFullName.getText());
            legalRepresentatives.setLastNames(txtFullLastName.getText());
            legalRepresentatives.setIdentificationNumber(txtIdentificationNumber.getText());
            legalRepresentatives.setDueDateDocumentIdentification(txtDueDateIdentification.getValue());
            legalRepresentatives.setAge(Integer.parseInt(txtAge.getText().toString()));
            if (genderFemale.isChecked()) {
                indGender = "F";
            } else {
                indGender = "M";
            }
            legalRepresentatives.setGender(indGender);
            legalRepresentatives.setPlaceBirth(txtBirthPlace.getText());
            legalRepresentatives.setDateBirth(txtBirthDay.getValue());
            legalRepresentatives.setDocumentsPersonTypeId((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue());
            legalRepresentatives = utilsEJB.saveLegalRepresentatives(legalRepresentatives);
            legalRepresentativesParam = legalRepresentatives;

            //LegalPersonHasLegalRepresentatives
            legalPersonHasLegalRepresentatives.setLegalPersonId(person.getLegalPerson());
            legalPersonHasLegalRepresentatives.setLegalRepresentativesid(legalRepresentatives);
            legalPersonHasLegalRepresentatives = personEJB.saveLegalPersonHasLegalRepresentatives(legalPersonHasLegalRepresentatives);

            //phonePerson
            phonePerson.setNumberPhone(txtPhoneNumber.getText());
            phonePerson.setPersonId(person);
            phonePerson.setPhoneTypeId((PhoneType) cmbPhoneType.getSelectedItem().getValue());
            phonePerson = personEJB.savePhonePerson(phonePerson);
            this.showMessage("sp.common.save.success", false, null);
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

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(legalRepresentativesParam);
                loadCmbCountry(eventType);
                loadCmbCivilState(eventType);
                loadCmbPhoneType(eventType);
                onChange$cmbCountry();
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
        //cmbDocumentsPersonType
        EJBRequest request1 = new EJBRequest();
        cmbDocumentsPersonType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        request1.setParams(params);
        List<DocumentsPersonType> documentsPersonType;
        try {
            documentsPersonType = utilsEJB.getDocumentsPersonByCity(request1);
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
        //cmbCivilState
        EJBRequest request1 = new EJBRequest();
        List<CivilStatus> civilStatuses;

        try {
            civilStatuses = personEJB.getCivilStatus(request1);
            loadGenericCombobox(civilStatuses, cmbCivilState, "description", evenInteger, Long.valueOf(legalRepresentativesParam != null ? legalRepresentativesParam.getPersonId().getNaturalPerson().getCivilStatusId().getId() : 0));
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