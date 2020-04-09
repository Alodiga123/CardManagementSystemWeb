package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.CivilStatus;
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.KinShipApplicant;
import com.cms.commons.models.NaturalCustomer;
import com.cms.commons.models.Person;
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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

public class AdminCustomerCardComplementariesController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentificationNumber;
    private Textbox txtIdentificationNumberOld;
    private Textbox txtFullName;
    private Textbox txtFullLastName;
    private Textbox txtBirthPlace;
    private Textbox txtEmail;
    private Textbox txtLocalPhone;
    private Textbox txtCellPhone;
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
    public Boolean indNaturalized = null;
    public Boolean indForeign = null;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private NaturalCustomer naturalCustomerParam;
    private Person person;
    private List<PhonePerson> phonePersonList = null;
    public static Person customerCard = null;
    private Button btnSave;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            naturalCustomerParam = null;
        } else {
            naturalCustomerParam = (NaturalCustomer) Sessions.getCurrent().getAttribute("object");
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
    
    public Person getCustomerCard() {
        return this.customerCard;
    }

    public void onClick$naturalizedYes() {
        txtIdentificationNumberOld.setDisabled(false);
    }

    public void onClick$naturalizedNo() {
        txtIdentificationNumberOld.setDisabled(true);
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
    }

    private void loadFields(NaturalCustomer naturalCustomer) {
        try {
            txtIdentificationNumber.setText(naturalCustomer.getIdentificationNumber());
            if (txtDueDateDocumentIdentification != null) {
                txtDueDateDocumentIdentification.setValue(naturalCustomer.getDueDateDocumentIdentification());
            }
            if (naturalCustomer.getIdentificationNumberOld() != null) {
                txtIdentificationNumberOld.setText(naturalCustomer.getIdentificationNumberOld());
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
            txtEmail.setText(naturalCustomer.getPersonId().getEmail());

            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_KEY, naturalCustomer.getPersonId().getId());
            request.setParams(params);
            phonePersonList = personEJB.getPhonePersonByperson(request);
            if (phonePersonList != null) {
                for (PhonePerson p : phonePersonList) {
                    if (p.getPhoneTypeId().getId() == Constants.PHONE_TYPE_ROOM) {
                        txtLocalPhone.setText(p.getNumberPhone());
                    }
                    if (p.getPhoneTypeId().getId() == Constants.PHONE_TYPE_MOBILE) {
                        txtCellPhone.setText(p.getNumberPhone());
                    }
                }
            }
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
        genderMale.setDisabled(true);
        genderFemale.setDisabled(true);
        txtBirthPlace.setReadonly(true);
        txtBirthDay.setReadonly(true);
        txtEmail.setReadonly(true);
        txtLocalPhone.setReadonly(true);
        txtCellPhone.setReadonly(true);
        cmbCountry.setReadonly(true);
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

    private void saveNaturalPerson(NaturalCustomer _naturalCustomer) {
        NaturalCustomer naturalCustomer = null;
        NaturalCustomer naturalCustomerP = null;
        Person person = null;
        PhonePerson phonePerson1 = null;
        PhonePerson phonePerson2 = null;
        PhoneType phonePersonH = null;
        PhoneType phonePersonC = null;

        try {
            if (_naturalCustomer != null) {
                naturalCustomer = _naturalCustomer;
                person = naturalCustomer.getPersonId();
            } else {
                naturalCustomer = new NaturalCustomer();
                person = new Person();
            }

            if (genderFemale.isChecked()) {
                indGender = "F";
            } else {
                indGender = "M";
            }

            //Solicitante Principal
            AdminNaturalPersonCustomerController adminNaturalCustomer = new AdminNaturalPersonCustomerController();
            if (adminNaturalCustomer != null) {
                naturalCustomerP = adminNaturalCustomer.getNaturalCustomer();
            }

            //Guarda los datos basicos de la persona
            String id = cmbCountry.getSelectedItem().getParent().getId();
            person.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            person.setPersonTypeId(((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue()).getPersonTypeId());
            person.setEmail(txtEmail.getText());
            person.setCreateDate(new Timestamp(new Date().getTime()));
            person = personEJB.savePerson(person);
            customerCard = person;

            ////Guarda el Solicitante Adicional
            naturalCustomer.setPersonId(person);
            naturalCustomer.setDocumentsPersonTypeId((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue());
            naturalCustomer.setIdentificationNumber(txtIdentificationNumber.getText());
            naturalCustomer.setDueDateDocumentIdentification(txtDueDateDocumentIdentification.getValue());
            naturalCustomer.setIdentificationNumberOld(txtIdentificationNumberOld.getText());
            naturalCustomer.setFirstNames(txtFullName.getText());
            naturalCustomer.setLastNames(txtFullLastName.getText());
            naturalCustomer.setGender(indGender);
            naturalCustomer.setPlaceBirth(txtBirthPlace.getText());
            naturalCustomer.setDateBirth(txtBirthDay.getValue());
            naturalCustomer.setCivilStatusId((CivilStatus) cmbCivilState.getSelectedItem().getValue());
            naturalCustomer.setProfessionId((Profession) cmbProfession.getSelectedItem().getValue());
            naturalCustomer.setNaturalCustomerId(naturalCustomerP);
            naturalCustomer.setKinShipApplicantId((KinShipApplicant) cmbRelationship.getSelectedItem().getValue());
            naturalCustomer.setUpdatedate(new Timestamp(new Date().getTime()));
            naturalCustomer = personEJB.saveNaturalCustomer(naturalCustomer);

            EJBRequest request = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.PERSON_KEY, person.getId());
            request.setParams(params);
            phonePersonList = personEJB.getPhonePersonByperson(request);
            if (phonePersonList != null) {
                for (PhonePerson p : phonePersonList) {
                    if (p.getPhoneTypeId().getId() == Constants.PHONE_TYPE_ROOM) {
                        phonePerson1 = p;
                    }
                    if (p.getPhoneTypeId().getId() == Constants.PHONE_TYPE_MOBILE) {
                        phonePerson2 = p;
                    }
                }
                //Actualiza el Telefono de Habitacion          
                phonePerson1.setNumberPhone(txtLocalPhone.getText());
                phonePerson1 = personEJB.savePhonePerson(phonePerson1);

                //Actualiza el Telefono Celular
                phonePerson2.setNumberPhone(txtCellPhone.getText());
                phonePerson2 = personEJB.savePhonePerson(phonePerson2);
            }

            this.showMessage("sp.common.save.success", false, null);
            EventQueues.lookup("updateCustomerCardComplementaries", EventQueues.APPLICATION, true).publish(new Event(""));

        } catch (NullParameterException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (RegisterNotFoundException ex) {
            showError(ex);
        } finally {
            try {
                if (phonePersonList == null) {
                    phonePerson1 = new PhonePerson();

                    //Obtiene los tipos de telefonos
                    EJBRequest request4 = new EJBRequest();
                    request4.setParam(Constants.PHONE_TYPE_ROOM);
                    phonePersonH = personEJB.loadPhoneType(request4);

                    request4 = new EJBRequest();
                    request4.setParam(Constants.PHONE_TYPE_MOBILE);
                    phonePersonC = personEJB.loadPhoneType(request4);
                    
                    //Se guarda los telefonos
                    phonePerson1.setPersonId(person);
                    phonePerson1.setPhoneTypeId(phonePersonH);
                    phonePerson1.setNumberPhone(txtLocalPhone.getText());
                    phonePerson1 = personEJB.savePhonePerson(phonePerson1);
                    //Guarda el Telefono Celular
                    phonePerson2 = new PhonePerson();
                    phonePerson2.setPersonId(person);
                    phonePerson2.setPhoneTypeId(phonePersonC);
                    phonePerson2.setNumberPhone(txtCellPhone.getText());
                    phonePerson2 = personEJB.savePhonePerson(phonePerson2);
                    this.showMessage("sp.common.save.success", false, null);
                }
            } catch (RegisterNotFoundException ex) {
                showError(ex);
            } catch (NullParameterException ex) {
                showError(ex);
            } catch (GeneralException ex) {
                showError(ex);
            }
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveNaturalPerson(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveNaturalPerson(naturalCustomerParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(naturalCustomerParam);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                loadCmbCivilState(eventType);
                loadCmbProfession(eventType);
                loadCmbRelationship(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(naturalCustomerParam);
                blockFields();
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(naturalCustomerParam != null ? naturalCustomerParam.getPersonId().getCountryId().getId() : 0));
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
        params.put(QueryConstants.PARAM_IND_NATURAL_PERSON, naturalCustomerParam.getDocumentsPersonTypeId().getPersonTypeId().getIndNaturalPerson());
        request1.setParams(params);
        List<DocumentsPersonType> documentsPersonType;
        try {
            documentsPersonType = utilsEJB.getDocumentsPersonByCountry(request1);
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger, Long.valueOf(naturalCustomerParam != null ? naturalCustomerParam.getDocumentsPersonTypeId().getId() : 0));
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
            loadGenericCombobox(civilStatuses, cmbCivilState, "description", evenInteger, Long.valueOf(naturalCustomerParam != null ? naturalCustomerParam.getCivilStatusId().getId() : 0));
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
            loadGenericCombobox(profession, cmbProfession, "name", evenInteger, Long.valueOf(naturalCustomerParam != null ? naturalCustomerParam.getProfessionId().getId() : 0));
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
            loadGenericCombobox(kinShipApplicant, cmbRelationship, "description", evenInteger, Long.valueOf(naturalCustomerParam != null ? naturalCustomerParam.getKinShipApplicantId().getId() : 0));
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
