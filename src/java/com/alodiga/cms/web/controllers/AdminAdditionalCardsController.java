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
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.sun.xml.ws.rx.mc.dev.AdditionalResponses;
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
    private Datebox txtDueDateIdentification;

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

    public void clearFields() {
        txtIdentificationNumber.setRawValue(null);
        txtFullName.setRawValue(null);
        txtFullLastName.setRawValue(null);
        txtPositionEnterprise.setRawValue(null);
        txtProposedLimit.setRawValue(null);
    }

    private void loadFields(CardRequestNaturalPerson cardRequestNaturalPerson) {
        try {
            txtIdentificationNumber.setText(cardRequestNaturalPerson.getIdentificationNumber());
            txtFullName.setText(cardRequestNaturalPerson.getFirstNames());
            txtFullLastName.setText(cardRequestNaturalPerson.getLastNames());
            txtPositionEnterprise.setText(cardRequestNaturalPerson.getPositionEnterprise());
            txtProposedLimit.setText(cardRequestNaturalPerson.getProposedLimit().toString());
            //txtProposedLimit.setText(cardRequestNaturalPerson.getPersonId().setNaturalPerson(null));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtIdentificationNumber.setReadonly(true);
        txtFullName.setReadonly(true);
        txtFullLastName.setReadonly(true);
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

    private void saveLegalRepresentatives(CardRequestNaturalPerson _cardRequestNaturalPerson) {
        try {
            CardRequestNaturalPerson cardRequestNaturalPerson = null;

            if (_cardRequestNaturalPerson != null) {
                cardRequestNaturalPerson = _cardRequestNaturalPerson;
            } else {//New CardRequestNaturalPerson
                cardRequestNaturalPerson = new CardRequestNaturalPerson();
            }

            cardRequestNaturalPerson.setIdentificationNumber(txtIdentificationNumber.getText());
            cardRequestNaturalPerson.setFirstNames(txtFullName.getText());
            cardRequestNaturalPerson.setLastNames(txtFullLastName.getText());
            cardRequestNaturalPerson.setPositionEnterprise(txtPositionEnterprise.getText());
            //cardRequestNaturalPerson.setLegalPersonid((txtPositionEnterprise));
            //cardRequestNaturalPerson.setProposedLimit(txtProposedLimit.getText());

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
                    saveLegalRepresentatives(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveLegalRepresentatives(cardRequestNaturalPersonParam);
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
                loadCmbDocumentsPersonType(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(cardRequestNaturalPersonParam);
                txtIdentificationNumber.setDisabled(true);
                txtFullName.setDisabled(true);
                txtFullLastName.setDisabled(true);
                txtPositionEnterprise.setDisabled(true);
                txtProposedLimit.setDisabled(true);
                loadCmbCountry(eventType);
                loadCmbDocumentsPersonType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbDocumentsPersonType(eventType);
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

    private void loadCmbDocumentsPersonType(Integer evenInteger) {
        //cmbDocumentsPersonType
        EJBRequest request1 = new EJBRequest();
        List<DocumentsPersonType> documentsPersonType;

        try {
            documentsPersonType = utilsEJB.getDocumentsPersonTypes(request1);
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
