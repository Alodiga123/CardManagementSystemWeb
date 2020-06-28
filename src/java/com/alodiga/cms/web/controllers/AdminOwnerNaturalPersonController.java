package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import static com.alodiga.cms.web.controllers.AdminNaturalPersonController.applicant;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.CivilStatus;
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.NaturalPerson;
import com.cms.commons.models.Person;
import com.cms.commons.models.PersonClassification;
import com.cms.commons.models.PhonePerson;
import com.cms.commons.models.PhoneType;
import com.cms.commons.models.Profession;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class AdminOwnerNaturalPersonController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentificationNumber;
    private Textbox txtIdentificationNumberOld;
    private Textbox txtFullName;
    private Textbox txtFullLastName;
    private Textbox txtMarriedLastName;
    private Textbox txtBirthPlace;
    private Textbox txtFamilyResponsibilities;
    private Textbox txtPhoneNumber;
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
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private Toolbarbutton tbbTitle;
    private Button btnSave;
    private Integer eventType;
    public static NaturalPerson naturalPersonParam = null;
    private Integer indSelect = 1;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        Sessions.getCurrent().setAttribute(WebConstants.IND_OWNER_PROGRAM_SELECT, indSelect);
        if (eventType == WebConstants.EVENT_ADD) {
            naturalPersonParam = null;
        } else {
            naturalPersonParam = (NaturalPerson) Sessions.getCurrent().getAttribute("object");
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("cms.common.programOwner.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("cms.common.programOwner.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("cms.common.programOwner.add"));
                break;
            default:
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
        this.clearMessage();
        cmbDocumentsPersonType.setVisible(true);
        cmbDocumentsPersonType.setValue("");
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbDocumentsPersonType(eventType, country.getId());
    }

    public Integer getEventType() {
        return this.eventType;
    }

    public NaturalPerson getNaturalPerson() {
        return naturalPersonParam;
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
        String Gender = "M";
        try {
            txtIdentificationNumber.setText(naturalPerson.getIdentificationNumber());
            if (txtDueDateDocumentIdentification != null) {
                txtDueDateDocumentIdentification.setValue(naturalPerson.getDueDateDocumentIdentification());
            }
            if (naturalPerson.getIdentificationNumberOld() != null) {
                txtIdentificationNumberOld.setText(naturalPerson.getIdentificationNumberOld());
            }
            txtFullName.setText(naturalPerson.getFirstNames());
            txtFullLastName.setText(naturalPerson.getLastNames());
            if (naturalPerson.getGender().equals(Gender)) {
                genderMale.setChecked(true);
            } else {
                genderFemale.setChecked(true);
            }
            txtBirthPlace.setText(naturalPerson.getPlaceBirth());
            txtBirthDay.setValue(naturalPerson.getDateBirth());
            txtFamilyResponsibilities.setText(naturalPerson.getFamilyResponsibilities().toString());
            txtMarriedLastName.setText(naturalPerson.getMarriedLastName());
            txtEmail.setText(naturalPerson.getPersonId().getEmail());
            txtPhoneNumber.setText(naturalPerson.getPersonId().getPhonePerson().getNumberPhone());

            naturalPersonParam = naturalPerson;
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtIdentificationNumber.setReadonly(true);
        txtDueDateDocumentIdentification.setDisabled(true);
        txtIdentificationNumberOld.setReadonly(true);
        txtFullName.setReadonly(true);
        txtFullLastName.setReadonly(true);
        txtMarriedLastName.setReadonly(true);
        genderMale.setDisabled(true);
        genderFemale.setDisabled(true);
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

    private void saveOwnerNaturalPerson(NaturalPerson _naturalPerson) {
        NaturalPerson naturalPerson = null;

        try {
            Person person = null;
            PhonePerson phonePerson = null;

            if (_naturalPerson != null) {
                naturalPerson = _naturalPerson;
                person = naturalPerson.getPersonId();
                phonePerson = naturalPerson.getPersonId().getPhonePerson();
            } else {//New ApplicantNaturalPerson
                naturalPerson = new NaturalPerson();
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
            request1.setParam(Constants.CLASSIFICATION_PERSON_OWNER);
            PersonClassification personClassification = utilsEJB.loadPersonClassification(request1);

            //Guardar la persona
            person.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            person.setPersonTypeId(((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue()).getPersonTypeId());
            person.setEmail(txtEmail.getText());
            if (eventType == WebConstants.EVENT_ADD) {
                person.setCreateDate(new Timestamp(new Date().getTime()));
            } else {
                person.setUpdateDate(new Timestamp(new Date().getTime()));
            }
            person.setPersonClassificationId(personClassification);
            person = personEJB.savePerson(person);
            applicant = person;

            //naturalPerson            
            naturalPerson.setPersonId(person);
            naturalPerson.setDocumentsPersonTypeId((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue());
            naturalPerson.setIdentificationNumber(txtIdentificationNumber.getText());
            naturalPerson.setDueDateDocumentIdentification(txtDueDateDocumentIdentification.getValue());
            if (!txtIdentificationNumberOld.getText().equals("")) {
                naturalPerson.setIdentificationNumberOld(txtIdentificationNumberOld.getText());
            }
            naturalPerson.setFirstNames(txtFullName.getText());
            naturalPerson.setLastNames(txtFullLastName.getText());
            naturalPerson.setMarriedLastName(txtMarriedLastName.getText());
            naturalPerson.setGender(indGender);
            naturalPerson.setPlaceBirth(txtBirthPlace.getText());
            naturalPerson.setDateBirth(txtBirthDay.getValue());
            naturalPerson.setCivilStatusId((CivilStatus) cmbCivilState.getSelectedItem().getValue());
            naturalPerson.setFamilyResponsibilities(Integer.parseInt(txtFamilyResponsibilities.getText()));
            naturalPerson.setProfessionId((Profession) cmbProfession.getSelectedItem().getValue());
            if (eventType == WebConstants.EVENT_ADD) {
                naturalPerson.setCreateDate(new Timestamp(new Date().getTime()));
            } else {
                naturalPerson.setUpdateDate(new Timestamp(new Date().getTime()));
            }
            naturalPerson = personEJB.saveNaturalPerson(naturalPerson);
            naturalPersonParam = naturalPerson;

            //phonePerson
            if (!txtPhoneNumber.getText().equals("")) {
                phonePerson.setNumberPhone(txtPhoneNumber.getText());
                phonePerson.setPersonId(person);
                phonePerson.setPhoneTypeId((PhoneType) cmbPhoneType.getSelectedItem().getValue());
                phonePerson = personEJB.savePhonePerson(phonePerson);
            }

            this.showMessage("sp.common.save.success", false, null);
            btnSave.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveOwnerNaturalPerson(null);
                break;
            case WebConstants.EVENT_EDIT:
                saveOwnerNaturalPerson(naturalPersonParam);
                break;
            default:
                break;
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(naturalPersonParam);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                loadCmbCivilState(eventType);
                loadCmbProfession(eventType);
                loadCmbPhoneType(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                blockFields();
                loadFields(naturalPersonParam);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                loadCmbCivilState(eventType);
                loadCmbProfession(eventType);
                loadCmbPhoneType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbCivilState(eventType);
                loadCmbProfession(eventType);
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
        EJBRequest request1 = new EJBRequest();
        cmbDocumentsPersonType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        params.put(QueryConstants.PARAM_IND_NATURAL_PERSON, WebConstants.IND_NATURAL_PERSON);
        params.put(QueryConstants.PARAM_ORIGIN_APPLICATION_ID, Constants.ORIGIN_APPLICATION_CMS_ID);
        request1.setParams(params);
        List<DocumentsPersonType> documentsPersonType;
        try {
            documentsPersonType = utilsEJB.getDocumentsPersonByCountry(request1);
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger, Long.valueOf(naturalPersonParam != null ? naturalPersonParam.getDocumentsPersonTypeId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (GeneralException ex) {
            showError(ex);
            ex.printStackTrace();
        } catch (NullParameterException ex) {
            showError(ex);
        } finally {
            if (documentsPersonType == null) {
                this.showMessage("cms.msj.DocumentsPersonTypeNaturalPersonNull", false, null);
            }            
        }
    }

    private void loadCmbCivilState(Integer evenInteger) {
        //cmbCivilState
        EJBRequest request1 = new EJBRequest();
        List<CivilStatus> civilStatuses;

        try {
            civilStatuses = personEJB.getCivilStatus(request1);
            loadGenericCombobox(civilStatuses, cmbCivilState, "description", evenInteger, Long.valueOf(naturalPersonParam != null ? naturalPersonParam.getCivilStatusId().getId() : 0));
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

    private void loadCmbPhoneType(Integer evenInteger) {
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
}
