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
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.EconomicActivity;
import com.cms.commons.models.LegalPerson;
import com.cms.commons.models.Person;
import com.cms.commons.models.PersonClassification;
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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;

public class AdminLegalPersonController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentificationNumber;
    private Textbox txtTradeName;
    private Textbox txtEnterpriseName;
    private Textbox txtPhoneNumber;
    private Textbox txtRegistryNumber;
    private Textbox txtPaidInCapital;
    private Textbox txtPersonId;
    private Textbox txtWebSite;
    private Tab tabAddress;
    private Tab tabLegalRepresentatives;
    private Tab tabAdditionalCards;
    private Textbox txtEmail;
    private Combobox cmbCountry;
    private Combobox cmbDocumentsPersonType;
    private Combobox cmbEconomicActivity;
    private Datebox txtExpirationDate;
    private Datebox txtDateInscriptionRegister;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private RequestEJB requestEJB = null;
    private LegalPerson legalPersonParam;
    private Person person;
    private Button btnSave;
    private Integer eventType;

    public Tabbox tb;

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
                        tabLegalRepresentatives.setDisabled(false);
                        tabAdditionalCards.setDisabled(false);
                    } else {
                        tabAddress.setDisabled(true);
                        tabLegalRepresentatives.setDisabled(true);
                        tabAdditionalCards.setDisabled(true);
                    }
                    if (adminRequest.getRequest().getPersonId() != null) {
                        legalPersonParam = adminRequest.getRequest().getPersonId().getLegalPerson();
                    } else {
                        legalPersonParam = null;
                    }
                break;
                case WebConstants.EVENT_VIEW:
                    if (adminRequest.getRequest().getPersonId() != null) {
                        tabAddress.setDisabled(false);
                        tabLegalRepresentatives.setDisabled(false);
                        tabAdditionalCards.setDisabled(false);
                    } else {
                        tabAddress.setDisabled(true);
                        tabLegalRepresentatives.setDisabled(true);
                        tabAdditionalCards.setDisabled(true);
                    }
                    if (adminRequest.getRequest().getPersonId() != null) {
                        legalPersonParam = adminRequest.getRequest().getPersonId().getLegalPerson();
                    } else {
                        legalPersonParam = null;
                    }
                break;
                case WebConstants.EVENT_ADD:
                    legalPersonParam = null;
                    tabAddress.setDisabled(true);
                    tabLegalRepresentatives.setDisabled(true);
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

    public void onChange$cmbCountry() {
        cmbDocumentsPersonType.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbDocumentsPersonType(eventType, country.getId());
    }

    public void clearFields() {
        txtPersonId.setRawValue(null);
        txtTradeName.setRawValue(null);
        txtEnterpriseName.setRawValue(null);
        txtDateInscriptionRegister.setRawValue(null);
        txtRegistryNumber.setRawValue(null);
        txtPaidInCapital.setRawValue(null);
        txtPhoneNumber.setRawValue(null);
        txtWebSite.setRawValue(null);
        txtEmail.setRawValue(null);
        txtIdentificationNumber.setRawValue(null);
    }

    private void loadFields(LegalPerson legalPerson) {
        try {
            txtPersonId.setText(legalPerson.getPersonId().toString());
            txtTradeName.setText(legalPerson.getTradeName());
            txtEnterpriseName.setText(legalPerson.getEnterpriseName());
            txtDateInscriptionRegister.setValue(legalPerson.getDateInscriptionRegister());
            txtRegistryNumber.setText(legalPerson.getRegisterNumber());
            txtPaidInCapital.setText(legalPerson.getPayedCapital().toString());
            txtPhoneNumber.setValue(legalPerson.getEnterprisePhone());
            txtWebSite.setValue(legalPerson.getWebSite());
            txtEmail.setValue(legalPerson.getPersonId().getEmail());
            txtIdentificationNumber.setText(legalPerson.getIdentificationNumber());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtTradeName.setReadonly(true);
        txtEnterpriseName.setReadonly(true);
        txtDateInscriptionRegister.setDisabled(true);
        txtRegistryNumber.setReadonly(true);
        txtPaidInCapital.setReadonly(true);
        txtPhoneNumber.setReadonly(true);
        txtWebSite.setReadonly(true);
        txtEmail.setReadonly(true);
        cmbEconomicActivity.setDisabled(true);
        cmbDocumentsPersonType.setDisabled(true);
        txtIdentificationNumber.setReadonly(true);
        txtExpirationDate.setDisabled(true);
        cmbCountry.setDisabled(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtIdentificationNumber.getText().isEmpty()) {
            txtIdentificationNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtEnterpriseName.getText().isEmpty()) {
            txtEnterpriseName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtPhoneNumber.getText().isEmpty()) {
            txtPhoneNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtRegistryNumber.getText().isEmpty()) {
            txtRegistryNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtPaidInCapital.getText().isEmpty()) {
            txtPaidInCapital.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;

    }

    public void onClick$btnCodes() {
        Executions.getCurrent().sendRedirect("/docs/T-SP-E.164D-2009-PDF-S.pdf", "_blank");
    }

    private void saveLegalPerson(LegalPerson _legalPerson) {
        tabAddress.setSelected(true);
        try {
            LegalPerson legalPerson = null;
            Person person = null;

            if (_legalPerson != null) {
                legalPerson = _legalPerson;
            } else {//New LegalPerson
                legalPerson = new LegalPerson();
                person = new Person();
            }

            //Request
            EJBRequest request1 = new EJBRequest();
            request1.setParam(Constants.REQUEST_ID_LEGAL_PERSON);
            Request request = requestEJB.loadRequest(request1);
            //System.out.println("Solicitud cableada: " + request.getRequestNumber());

            //PersonClassification
            request1 = new EJBRequest();
            request1.setParam(Constants.CLASSIFICATION_PERSON_APPLICANT);
            PersonClassification personClassification = utilsEJB.loadPersonClassification(request1);
            
            //Guardar Person
            String id = cmbCountry.getSelectedItem().getParent().getId();
            person.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            person.setPersonTypeId(request.getPersonTypeId());
            person.setEmail(txtEmail.getText());
            person.setCreateDate(new Timestamp(new Date().getTime()));
            person.setPersonClassificationId(personClassification);
            person = personEJB.savePerson(person);

            legalPerson.setPersonId(person);
            legalPerson.setTradeName(txtTradeName.getText());
            legalPerson.setEnterpriseName(txtEnterpriseName.getText());
            legalPerson.setDateInscriptionRegister(new Timestamp(txtDateInscriptionRegister.getValue().getTime()));
            legalPerson.setRegisterNumber(txtRegistryNumber.getText());
            legalPerson.setPayedCapital(Float.parseFloat(txtPaidInCapital.getText()));
            legalPerson.setEnterprisePhone(txtPhoneNumber.getText());
            legalPerson.setWebSite(txtWebSite.getText());
            legalPerson.setEconomicActivityId((EconomicActivity) cmbEconomicActivity.getSelectedItem().getValue());
            legalPerson.setDocumentsPersonTypeId((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue());
            legalPerson.setIdentificationNumber(txtIdentificationNumber.getText());
            legalPerson = utilsEJB.saveLegalPerson(legalPerson);
            legalPersonParam = legalPerson;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveLegalPerson(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveLegalPerson(legalPersonParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(legalPersonParam);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                loadCmbEconomicActivity(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(legalPersonParam);
                txtTradeName.setDisabled(true);
                txtEnterpriseName.setDisabled(true);
                txtDateInscriptionRegister.setDisabled(true);
                txtRegistryNumber.setDisabled(true);
                txtPaidInCapital.setDisabled(true);
                txtPhoneNumber.setDisabled(true);
                txtWebSite.setDisabled(true);
                txtEmail.setDisabled(true);
                txtIdentificationNumber.setDisabled(true);
                loadCmbCountry(eventType);
                loadCmbEconomicActivity(eventType);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbEconomicActivity(eventType);
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(legalPersonParam != null ? legalPersonParam.getPersonId().getCountryId().getId() : 0));
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
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger, Long.valueOf(legalPersonParam != null ? legalPersonParam.getDocumentsPersonTypeId().getId() : 0));
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

    private void loadCmbEconomicActivity(Integer evenInteger) {
        //cmbEconomicActivity
        EJBRequest request = new EJBRequest();
        List<EconomicActivity> economicActivity;

        try {
            economicActivity = utilsEJB.getEconomicActivitys(request);
            loadGenericCombobox(economicActivity, cmbEconomicActivity, "description", evenInteger, Long.valueOf(legalPersonParam != null ? legalPersonParam.getEconomicActivityId().getId() : 0));
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
