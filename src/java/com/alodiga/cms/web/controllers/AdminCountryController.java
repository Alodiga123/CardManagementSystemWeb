package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Country;
import com.cms.commons.models.Currency;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;

public class AdminCountryController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtName;
    private Textbox txtCode;
    private Textbox txtShortName;
    private Textbox txtAlternativeName1;
    private UtilsEJB utilsEJB = null;
    private Combobox cmbCurrency;
    private Country countryParam;
    private Button btnSave;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        countryParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Country) Sessions.getCurrent().getAttribute("object") : null;
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
        txtName.setRawValue(null);
        txtShortName.setRawValue(null);
        txtCode.setRawValue(null);
        txtAlternativeName1.setRawValue(null);
    }

    private void loadFields(Country country) {
        try {
            txtName.setText(country.getName());
            txtShortName.setText(country.getCodeIso2());
            txtCode.setText(country.getCode());
            txtAlternativeName1.setText(country.getCodeIso3());

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtName.setReadonly(true);
        txtShortName.setReadonly(true);
        txtCode.setReadonly(true);
        txtAlternativeName1.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtShortName.getText().isEmpty()) {
            txtShortName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtCode.getText().isEmpty()) {
            txtCode.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;

    }

    public void onClick$btnCodes() {
        Executions.getCurrent().sendRedirect("/docs/T-SP-E.164D-2009-PDF-S.pdf", "_blank");
    }

    public void onClick$btnShortNames() {
        Executions.getCurrent().sendRedirect("/docs/countries-abbreviation.pdf", "_blank");
    }

    private void saveCountry(Country _country) {
        try {
            Country country = null;

            if (_country != null) {
                country = _country;
            } else {//New country
                country = new Country();
            }
            country.setName(txtName.getText());
            country.setCode(txtCode.getText());
            country.setCodeIso2(txtShortName.getText());
            country.setCodeIso3(txtAlternativeName1.getText());
            country.setCurrencyId((Currency) cmbCurrency.getSelectedItem().getValue());//prueba
            country = utilsEJB.saveCountry(country);
            countryParam = country;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveCountry(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveCountry(countryParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(countryParam);
                loadCmbCurrency(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(countryParam);
                txtName.setDisabled(true);
                txtShortName.setDisabled(true);
                txtCode.setDisabled(true);
                txtAlternativeName1.setDisabled(true);
                loadCmbCurrency(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCurrency(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbCurrency(Integer evenInteger) {
        //cmbCurrency
        EJBRequest request1 = new EJBRequest();
        List<Currency> currencies;
        try {
            currencies = utilsEJB.getCurrency(request1);
            loadGenericCombobox(currencies,cmbCurrency, "name",evenInteger,Long.valueOf(countryParam != null? countryParam.getId(): 0) );            
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