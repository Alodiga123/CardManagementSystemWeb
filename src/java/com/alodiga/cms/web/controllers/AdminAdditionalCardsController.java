package com.alodiga.cms.web.controllers;

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
import com.cms.commons.models.LegalPerson;
import com.cms.commons.models.Person;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
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
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;

public class AdminAdditionalCardsController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentificationNumber;
    private Textbox txtFullName;
    private Textbox txtFullLastName;
    private Textbox txtPositionEnterprise;
    private Textbox txtProposedLimit;
    private Combobox cmbCountry;
    private Combobox cmbDocumentsPersonType;
//    private Tab tabAdditionalCards;
    private UtilsEJB utilsEJB = null;
    private CardRequestNaturalPerson cardRequestNaturalPersonParam;
    private Button btnSave;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        cardRequestNaturalPersonParam = (Sessions.getCurrent().getAttribute("object") != null) ? (CardRequestNaturalPerson) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
        //initView(eventType, "sp.crud.country");
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
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

    public void onClick$btnCodes() {
        Executions.getCurrent().sendRedirect("/docs/T-SP-E.164D-2009-PDF-S.pdf", "_blank");
    }

    private void saveCarRequestNaturalPerson(CardRequestNaturalPerson _cardRequestNaturalPerson) {
//        tabAdditionalCards.setSelected(true);
        try {
            CardRequestNaturalPerson cardRequestNaturalPerson = null;

            if (_cardRequestNaturalPerson != null) {
                cardRequestNaturalPerson = _cardRequestNaturalPerson;
            } else {//New CardRequestNaturalPerson
                cardRequestNaturalPerson = new CardRequestNaturalPerson();
            }

            //Person
            EJBRequest request1 = new EJBRequest();
            request1.setParam(Constants.PERSON_ID_KEY);
            Person person = utilsEJB.loadPerson(request1);

            //LegalPerson
            request1 = new EJBRequest();
            request1.setParam(Constants.PERSON_ID_KEY);
            LegalPerson legalPerson = utilsEJB.loadLegalPerson(request1);

            cardRequestNaturalPerson.setPersonId(person);
            cardRequestNaturalPerson.setLegalPersonid(legalPerson);
            cardRequestNaturalPerson.setFirstNames(txtFullName.getText());
            cardRequestNaturalPerson.setLastNames(txtFullLastName.getText());
            cardRequestNaturalPerson.setIdentificationNumber(txtIdentificationNumber.getText());
            cardRequestNaturalPerson.setPositionEnterprise(txtPositionEnterprise.getText());
            cardRequestNaturalPerson.setProposedLimit(Float.parseFloat(txtProposedLimit.getText()));
            cardRequestNaturalPerson.setDocumentsPersonTypeId((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue());
            cardRequestNaturalPerson = utilsEJB.saveCardRequestNaturalPerson(cardRequestNaturalPerson);
            cardRequestNaturalPersonParam = cardRequestNaturalPerson;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveCarRequestNaturalPerson(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveCarRequestNaturalPerson(cardRequestNaturalPersonParam);
                    break;
                default:
                    break;
            }
        }
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
        //cmbDocumentsPersonType
        EJBRequest request1 = new EJBRequest();
        cmbDocumentsPersonType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        request1.setParams(params);
        List<DocumentsPersonType> documentsPersonType;

        try {
            documentsPersonType = utilsEJB.getDocumentsPersonByCity(request1);
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
