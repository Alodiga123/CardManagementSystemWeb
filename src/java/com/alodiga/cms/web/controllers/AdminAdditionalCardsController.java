package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.CardRequestNaturalPerson;
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.LegalCustomer;
import com.cms.commons.models.LegalPerson;
import com.cms.commons.models.Person;
import com.cms.commons.models.PersonClassification;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class AdminAdditionalCardsController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentificationNumber;
    private Textbox txtFullName;
    private Textbox txtFullLastName;
    private Textbox txtPositionEnterprise;
    private Textbox txtProposedLimit;
    private Combobox cmbCountry;
    private Combobox cmbDocumentsPersonType;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private CardRequestNaturalPerson cardRequestNaturalPersonParam;
    public Window winAdminAdditionalCards;
    private Button btnSave;
    private Integer eventType;
    public AdminRequestController adminRequest = null;
    public AdminLegalPersonController adminLegalPerson = null;
    public AdminLegalPersonCustomerController adminLegalCustomerPerson = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        adminRequest = new AdminRequestController();
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        switch (eventType) {
                case WebConstants.EVENT_EDIT:
                    cardRequestNaturalPersonParam = (CardRequestNaturalPerson) Sessions.getCurrent().getAttribute("object");
                break;
                case WebConstants.EVENT_VIEW:
                    cardRequestNaturalPersonParam = (CardRequestNaturalPerson) Sessions.getCurrent().getAttribute("object");
                break;
                case WebConstants.EVENT_ADD:
                    cardRequestNaturalPersonParam = null;
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

    public void onChange$cmbCountry() {
        cmbDocumentsPersonType.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbDocumentsPersonType(eventType, country.getId());
    }

    public void clearFields() {
        txtFullName.setRawValue(null);
        txtFullLastName.setRawValue(null);
        txtIdentificationNumber.setRawValue(null);
        txtPositionEnterprise.setRawValue(null);
        txtProposedLimit.setRawValue(null);
    }

    private void loadFields(CardRequestNaturalPerson cardRequestNaturalPerson) {
        try {
            txtFullName.setText(cardRequestNaturalPerson.getFirstNames());
            txtFullLastName.setText(cardRequestNaturalPerson.getLastNames());
            txtIdentificationNumber.setText(cardRequestNaturalPerson.getIdentificationNumber());
            txtPositionEnterprise.setText(cardRequestNaturalPerson.getPositionEnterprise());
            txtProposedLimit.setText(cardRequestNaturalPerson.getProposedLimit().toString());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtFullName.setReadonly(true);
        txtFullLastName.setReadonly(true);
        txtIdentificationNumber.setReadonly(true);
        txtPositionEnterprise.setDisabled(true);
        txtProposedLimit.setDisabled(true);
        cmbCountry.setDisabled(true);
        cmbDocumentsPersonType.setDisabled(true);
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
        } else if (txtPositionEnterprise.getText().isEmpty()) {
            txtPositionEnterprise.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtProposedLimit.getText().isEmpty()) {
            txtProposedLimit.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }

    private void saveCardRequestNaturalPerson(CardRequestNaturalPerson _cardRequestNaturalPerson) {
        LegalPerson legalPerson = null;
        LegalCustomer legalCustomer = null;
        Person personCardRequestNaturalPerson = null;
        try {
            CardRequestNaturalPerson cardRequestNaturalPerson = null;
            Person person = null;

            if (_cardRequestNaturalPerson != null) {
                cardRequestNaturalPerson = _cardRequestNaturalPerson;
                person = cardRequestNaturalPerson.getPersonId();
            } else {//New CardRequestNaturalPerson
                person = new Person();
                cardRequestNaturalPerson = new CardRequestNaturalPerson();            
            }

            //Solicitante Jurídico
            adminLegalPerson = new AdminLegalPersonController();
            adminLegalCustomerPerson = new AdminLegalPersonCustomerController();

            if (adminLegalPerson.getLegalPerson() != null) {
                legalPerson = adminLegalPerson.getLegalPerson();
            } else if (adminLegalCustomerPerson.getLegalCustomer() != null) {
                legalCustomer = adminLegalCustomerPerson.getLegalCustomer();
            }
            
            //Obtener la clasificacion del solicitante de tarjeta adicional
            EJBRequest request1 = new EJBRequest();
            request1.setParam(Constants.CLASSIFICATION_PERSON_CARD_REQUEST_NATURAL_PERSON);
            PersonClassification personClassification = utilsEJB.loadPersonClassification(request1);

            //Guardar la persona
            person.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            if (eventType == 1) {
                person.setCreateDate(new Timestamp(new Date().getTime()));
                person.setPersonClassificationId(personClassification);
            }
            person = personEJB.savePerson(person);
            personCardRequestNaturalPerson = person;

            //Guarda el solicitante adicional de tarjeta
            cardRequestNaturalPerson.setPersonId(personCardRequestNaturalPerson);
            if (legalPerson != null) {
                cardRequestNaturalPerson.setLegalPersonid(legalPerson);
            }
            cardRequestNaturalPerson.setFirstNames(txtFullName.getText());
            cardRequestNaturalPerson.setLastNames(txtFullLastName.getText());
            cardRequestNaturalPerson.setIdentificationNumber(txtIdentificationNumber.getText());
            cardRequestNaturalPerson.setPositionEnterprise(txtPositionEnterprise.getText());
            cardRequestNaturalPerson.setProposedLimit(Float.parseFloat(txtProposedLimit.getText()));
            cardRequestNaturalPerson.setDocumentsPersonTypeId((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue());
            if (legalCustomer != null) {
                cardRequestNaturalPerson.setLegalCustomerId(legalCustomer);
            }
            cardRequestNaturalPerson = personEJB.saveCardRequestNaturalPerson(cardRequestNaturalPerson);
            cardRequestNaturalPersonParam = cardRequestNaturalPerson;
            this.showMessage("sp.common.save.success", false, null);
            
            EventQueues.lookup("updateCardRequestNaturalPerson", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveCardRequestNaturalPerson(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveCardRequestNaturalPerson(cardRequestNaturalPersonParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void onClick$btnBack() {
        winAdminAdditionalCards.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(cardRequestNaturalPersonParam);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(cardRequestNaturalPersonParam);
                txtIdentificationNumber.setDisabled(true);
                txtFullName.setDisabled(true);
                txtFullLastName.setDisabled(true);
                txtPositionEnterprise.setDisabled(true);
                txtProposedLimit.setDisabled(true);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(cardRequestNaturalPersonParam != null ? cardRequestNaturalPersonParam.getPersonId().getCountryId().getId() : 0));
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
        params.put(QueryConstants.PARAM_IND_NATURAL_PERSON,WebConstants.IND_NATURAL_PERSON);
        request1.setParams(params);
        List<DocumentsPersonType> documentsPersonType;
        try {
            documentsPersonType = utilsEJB.getDocumentsPersonByCountry(request1);
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger, Long.valueOf(cardRequestNaturalPersonParam != null ? cardRequestNaturalPersonParam.getDocumentsPersonTypeId().getId() : 0));
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
