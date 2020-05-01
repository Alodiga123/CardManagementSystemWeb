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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class AdminOwnerLegalPersonController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentificationNumber;
    private Textbox txtTradeName;
    private Textbox txtEnterpriseName;
    private Textbox txtRegistryNumber;
    private Textbox txtPaidInCapital;
    private Textbox txtPersonId;
    private Textbox txtWebSite;
    private Textbox txtEmail;
    private Combobox cmbCountry;
    private Combobox cmbDocumentsPersonType;
    private Combobox cmbEconomicActivity;
    private Datebox txtDateInscriptionRegister;
    private UtilsEJB utilsEJB = null;
    private PersonEJB personEJB = null;
    private RequestEJB requestEJB = null;
    public static LegalPerson legalOwnerParam = null;
    private Person person;
    private Button btnSave;
    private Toolbarbutton tbbTitle;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            legalOwnerParam = null;
        } else {
            legalOwnerParam = (LegalPerson) Sessions.getCurrent().getAttribute("object");
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
    
    public LegalPerson getLegalPerson() {
        return legalOwnerParam;
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
        txtWebSite.setRawValue(null);
        txtEmail.setRawValue(null);
        txtIdentificationNumber.setRawValue(null);
    }

    private void loadFields(LegalPerson legalOwner) {
        try {
            txtPersonId.setText(legalOwner.getPersonId().toString());
            txtTradeName.setText(legalOwner.getTradeName());
            txtEnterpriseName.setText(legalOwner.getEnterpriseName());
            txtDateInscriptionRegister.setValue(legalOwner.getDateInscriptionRegister());
            txtRegistryNumber.setText(legalOwner.getRegisterNumber());
            txtPaidInCapital.setText(legalOwner.getPayedCapital().toString());
            txtWebSite.setValue(legalOwner.getWebSite());
            txtEmail.setValue(legalOwner.getPersonId().getEmail());
            txtIdentificationNumber.setText(legalOwner.getIdentificationNumber());
            
            legalOwnerParam = legalOwner;
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
        txtWebSite.setReadonly(true);
        txtEmail.setReadonly(true);
        txtIdentificationNumber.setReadonly(true);
        cmbCountry.setDisabled(true);
        cmbDocumentsPersonType.setDisabled(true);
        cmbEconomicActivity.setDisabled(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtIdentificationNumber.getText().isEmpty()) {
            txtIdentificationNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtEnterpriseName.getText().isEmpty()) {
            txtEnterpriseName.setFocus(true);
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


    private void saveLegalOwner(LegalPerson _legalOwner) {
        try {
            LegalPerson legalOwner = null;
            Person person = null;

            if (_legalOwner != null) {
                legalOwner = _legalOwner;
            } else {//New LegalPerson
                legalOwner = new LegalPerson();
                person = new Person();
            }

            //Request
            EJBRequest request1 = new EJBRequest();
            request1.setParam(Constants.REQUEST_ID_LEGAL_PERSON);
            Request request = requestEJB.loadRequest(request1);

            //PersonClassification
            request1 = new EJBRequest();
            request1.setParam(Constants.CLASSIFICATION_PERSON_OWNER);
            PersonClassification personClassification = utilsEJB.loadPersonClassification(request1);

            //Guardar Person
            String id = cmbCountry.getSelectedItem().getParent().getId();
            person.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            person.setPersonTypeId(request.getPersonTypeId());
            person.setEmail(txtEmail.getText());
            if (eventType == WebConstants.EVENT_ADD) {
                person.setCreateDate(new Timestamp(new Date().getTime()));
            } else {
                person.setUpdateDate(new Timestamp(new Date().getTime()));
            }
            person.setPersonClassificationId(personClassification);
            person = personEJB.savePerson(person);

            //Guarda los cambios en el Propietario Jurídico
            legalOwner.setPersonId(person);
            legalOwner.setTradeName(txtTradeName.getText());
            legalOwner.setEnterpriseName(txtEnterpriseName.getText());
            legalOwner.setDateInscriptionRegister(new Timestamp(txtDateInscriptionRegister.getValue().getTime()));
            legalOwner.setRegisterNumber(txtRegistryNumber.getText());
            legalOwner.setPayedCapital(Float.parseFloat(txtPaidInCapital.getText()));
            legalOwner.setWebSite(txtWebSite.getText());
            legalOwner.setEconomicActivityId((EconomicActivity) cmbEconomicActivity.getSelectedItem().getValue());
            legalOwner.setDocumentsPersonTypeId((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue());
            legalOwner.setIdentificationNumber(txtIdentificationNumber.getText());
            legalOwner = utilsEJB.saveLegalPerson(legalOwner);
            legalOwnerParam = legalOwner;
            
            this.showMessage("sp.common.save.success", false, null);
            btnSave.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveLegalOwner(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveLegalOwner(legalOwnerParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(legalOwnerParam);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                loadCmbEconomicActivity(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(legalOwnerParam);
                blockFields();
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(legalOwnerParam != null ? legalOwnerParam.getPersonId().getCountryId().getId() : 0));
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
        params.put(QueryConstants.PARAM_IND_NATURAL_PERSON, WebConstants.IND_LEGAL_PERSON);
        request1.setParams(params);
        List<DocumentsPersonType> documentsPersonType;
        try {
            documentsPersonType = utilsEJB.getDocumentsPersonByCountry(request1);
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger, Long.valueOf(legalOwnerParam != null ? legalOwnerParam.getDocumentsPersonTypeId().getId() : 0));
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
            loadGenericCombobox(economicActivity, cmbEconomicActivity, "description", evenInteger, Long.valueOf(legalOwnerParam != null ? legalOwnerParam.getEconomicActivityId().getId() : 0));
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