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
import com.cms.commons.models.KinShipApplicant;
import com.cms.commons.models.NaturalCustomer;
import com.cms.commons.models.Person;
import com.cms.commons.models.Profession;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

public class AdminNaturalPersonCustomerController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentificationNumber;
    private Textbox txtIdentificationNumberOld;
    private Textbox txtCountryStayTime;
    private Textbox txtFullName;
    private Textbox txtFullLastName;
    private Textbox txtMarriedLastName;
    private Textbox txtBirthPlace;
    private Textbox txtFamilyResponsibilities;
    private Combobox cmbCountry;
    private Combobox cmbDocumentsPersonType;
    private Combobox cmbCivilState;
    private Combobox cmbProfession;
    private Combobox cmbKinShipApplicant;
    private Datebox txtBirthDay;
    private Datebox txtDueDateDocumentIdentification;
    private Radio genderMale;
    private Radio genderFemale;
    private Radio naturalizedYes;
    private Radio naturalizedNo;
    private Radio foreignYes;
    private Radio foreignNo;
    public String indGender = null;
    public Boolean indNaturalized = null;
    public Boolean indForeign = null;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private Button btnSave;
    private Integer eventType;
    public static NaturalCustomer naturalCustomerParent = null;
    public NaturalCustomer personCustomerParam;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        personCustomerParam = (Sessions.getCurrent().getAttribute("object") != null) ? (NaturalCustomer) Sessions.getCurrent().getAttribute("object") : null;
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

    public void onChange$cmbCountry() {
        cmbDocumentsPersonType.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbDocumentsPersonType(eventType, country.getId());
    }

    public NaturalCustomer getNaturalCustomerParent() {
        return naturalCustomerParent;
    }

    public void onClick$naturalizedYes() {
        txtIdentificationNumberOld.setDisabled(false);
    }

    public void onClick$naturalizedNo() {
        txtIdentificationNumberOld.setDisabled(true);
    }

    public void onClick$foreignYes() {
        txtCountryStayTime.setDisabled(false);
    }

    public void onClick$foreignNo() {
        txtCountryStayTime.setDisabled(true);
    }

    public void clearFields() {
        txtIdentificationNumber.setRawValue(null);
        txtDueDateDocumentIdentification.setRawValue(null);
        txtIdentificationNumberOld.setRawValue(null);
        txtCountryStayTime.setRawValue(null);
        txtFullName.setRawValue(null);
        txtFullLastName.setRawValue(null);
        txtMarriedLastName.setRawValue(null);
        txtBirthPlace.setRawValue(null);
        txtBirthDay.setRawValue(null);
        txtFamilyResponsibilities.setRawValue(null);
    }

    private void loadFields(NaturalCustomer naturalCustomer) {
        try {
            txtIdentificationNumber.setText(naturalCustomer.getIdentificationNumber());
            txtDueDateDocumentIdentification.setValue(naturalCustomer.getDueDateDocumentIdentification());
            if (naturalCustomer.getIndNaturalized() == true) {
                naturalizedYes.setChecked(true);
                txtIdentificationNumberOld.setDisabled(false);
            } else {
                naturalizedNo.setChecked(true);
                txtIdentificationNumberOld.setDisabled(true);
            }
            txtIdentificationNumberOld.setText(naturalCustomer.getIdentificationNumberOld());
            if (naturalCustomer.getIndForeign() == true) {
                foreignYes.setChecked(true);
                txtCountryStayTime.setDisabled(false);
            } else {
                foreignNo.setChecked(true);
                txtCountryStayTime.setDisabled(true);
            }
            txtFullName.setText(naturalCustomer.getFirstNames());
            txtFullLastName.setText(naturalCustomer.getLastNames());

            if (naturalCustomer.getGender() == "M") {
                genderMale.setChecked(true);
            } else {
                genderFemale.setChecked(true);
            }
            txtBirthPlace.setText(naturalCustomer.getPlaceBirth());
            txtBirthDay.setValue(naturalCustomer.getDateBirth());
            txtFamilyResponsibilities.setText(naturalCustomer.getFamilyResponsibilities().toString());
            txtCountryStayTime.setText(naturalCustomer.getCountryStayTime().toString());
            txtMarriedLastName.setText(naturalCustomer.getMarriedLastName());
            naturalCustomerParent = naturalCustomer;
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtIdentificationNumber.setReadonly(true);
        txtDueDateDocumentIdentification.setDisabled(true);
        naturalizedYes.setDisabled(true);
        naturalizedNo.setDisabled(true);
        txtIdentificationNumberOld.setReadonly(true);
        foreignYes.setDisabled(true);
        foreignNo.setDisabled(true);
        txtCountryStayTime.setReadonly(true);
        txtFullName.setReadonly(true);
        txtFullLastName.setReadonly(true);
        txtMarriedLastName.setReadonly(true);
        genderMale.setDisabled(true);
        genderFemale.setDisabled(true);
        txtBirthPlace.setReadonly(true);
        txtBirthDay.setReadonly(true);
        txtFamilyResponsibilities.setReadonly(true);
        cmbCountry.setReadonly(true);
        cmbCivilState.setReadonly(true);
        cmbProfession.setReadonly(true);
        btnSave.setVisible(false);
    }

    private void saveNaturalPersonCustomer(NaturalCustomer _naturalCustomer) {
        NaturalCustomer naturalCustomer = null;
        AdminRequestController adminRequest = new AdminRequestController();
        try {
            Person person = null;

            if (_naturalCustomer != null) {
                naturalCustomer = _naturalCustomer;
                person = naturalCustomer.getPersonId();
            } else {//New ApplicantNaturalPerson
                naturalCustomer = new NaturalCustomer();
                person = new Person();
            }

            if (genderFemale.isChecked()) {
                indGender = "F";
            } else {
                indGender = "M";
            }

            if (naturalizedYes.isChecked()) {
                indNaturalized = true;
            } else {
                indNaturalized = false;
            }

            if (foreignYes.isChecked()) {
                indForeign = true;
            } else {
                indForeign = false;
            }

            //naturalPerson            
            naturalCustomer.setPersonId(person);
            naturalCustomer.setDocumentsPersonTypeId((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue());
            naturalCustomer.setIdentificationNumber(txtIdentificationNumber.getText());
            naturalCustomer.setDueDateDocumentIdentification(txtDueDateDocumentIdentification.getValue());
            naturalCustomer.setIndNaturalized(indNaturalized);
            if (!txtIdentificationNumberOld.getText().equals("")) {
                naturalCustomer.setIdentificationNumberOld(txtIdentificationNumberOld.getText());
            }
            //naturalCustomer.setStatusCustomerId(null);
            naturalCustomer.setIndForeign(indForeign);
            if (!txtCountryStayTime.getText().equals("")) {
                naturalCustomer.setCountryStayTime(Integer.parseInt(txtCountryStayTime.getText()));
            }
            naturalCustomer.setFirstNames(txtFullName.getText());
            naturalCustomer.setLastNames(txtFullLastName.getText());
            naturalCustomer.setMarriedLastName(txtMarriedLastName.getText());
            naturalCustomer.setGender(indGender);
            naturalCustomer.setPlaceBirth(txtBirthPlace.getText());
            naturalCustomer.setDateBirth(txtBirthDay.getValue());
            naturalCustomer.setCivilStatusId((CivilStatus) cmbCivilState.getSelectedItem().getValue());
            naturalCustomer.setFamilyResponsibilities(Integer.parseInt(txtFamilyResponsibilities.getText()));
            naturalCustomer.setProfessionId((Profession) cmbProfession.getSelectedItem().getValue());
            naturalCustomer.setKinShipApplicantId((KinShipApplicant) cmbKinShipApplicant.getSelectedItem().getValue());
            naturalCustomer.setUpdatedate(new Timestamp(new Date().getTime()));
            naturalCustomer = personEJB.saveNaturalCustomer(naturalCustomer);
            naturalCustomerParent = naturalCustomer;
            this.showMessage("sp.common.save.success", false, null);

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveNaturalPersonCustomer(null);
                break;
            case WebConstants.EVENT_EDIT:
                saveNaturalPersonCustomer(personCustomerParam);
                break;
            default:
                break;
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(personCustomerParam);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                loadCmbCivilState(eventType);
                loadCmbProfession(eventType);
                loadCmbKinShipApplicant(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                blockFields();
                loadFields(personCustomerParam);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                loadCmbCivilState(eventType);
                loadCmbProfession(eventType);
                loadCmbKinShipApplicant(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbCivilState(eventType);
                loadCmbProfession(eventType);
                loadCmbKinShipApplicant(eventType);
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(personCustomerParam != null ? personCustomerParam.getPersonId().getCountryId().getId() : 0));
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
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger, Long.valueOf(personCustomerParam != null ? personCustomerParam.getDocumentsPersonTypeId().getId() : 0));
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
            loadGenericCombobox(civilStatuses, cmbCivilState, "description", evenInteger, Long.valueOf(personCustomerParam != null ? personCustomerParam.getPersonId().getApplicantNaturalPerson().getCivilStatusId().getId() : 0));
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
            loadGenericCombobox(profession, cmbProfession, "name", evenInteger, Long.valueOf(personCustomerParam != null ? personCustomerParam.getProfessionId().getId() : 0));
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

    private void loadCmbKinShipApplicant(Integer evenInteger) {
        //cmbKinShipApplicant
        EJBRequest request1 = new EJBRequest();
        List<KinShipApplicant> kinShipApplicants;

        try {
            kinShipApplicants = personEJB.getKinShipApplicant(request1);
            loadGenericCombobox(kinShipApplicants, cmbKinShipApplicant, "description", evenInteger, Long.valueOf(personCustomerParam != null ? personCustomerParam.getKinShipApplicantId().getId() : 0));
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
