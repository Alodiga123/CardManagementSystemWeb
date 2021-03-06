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
import java.util.List;
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
    private Textbox txtCodeIso2;
    private Textbox txtCodeIso3;
    private UtilsEJB utilsEJB = null;
    private Combobox cmbCurrency;
    private Country countryParam;
    private Button btnSave;
    private Integer eventType;
    

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        countryParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Country) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute( WebConstants.EVENTYPE);
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
        txtCodeIso2.setRawValue(null);
        txtCode.setRawValue(null);
        txtCodeIso3.setRawValue(null);

//Cambio prueba
    }

    private void loadFields(Country country) {
        try {
            txtName.setText(country.getName());
            txtCodeIso2.setText(country.getCodeIso2());
            txtCode.setText(country.getCode());
            txtCodeIso3.setText(country.getCodeIso3());

        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtName.setReadonly(true);
        txtCodeIso2.setReadonly(true);
        txtCode.setReadonly(true);
        txtCodeIso3.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtCodeIso2.getText().isEmpty()) {
            txtCodeIso2.setFocus(true);
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
            country.setCodeIso2(txtCodeIso2.getText());
            country.setCodeIso3(txtCodeIso3.getText());
            country.setCurrencyId((Currency) cmbCurrency.getSelectedItem().getValue());
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
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(countryParam);
                txtName.setDisabled(true);
                txtCodeIso2.setDisabled(true);
                txtCode.setDisabled(true);
                txtCodeIso3.setDisabled(true);
                loadCmbCurrency(eventType);
                break;
            case WebConstants.EVENT_ADD:
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
            cmbCurrency.getItems().clear();
            for (Currency c : currencies) {
 
                Comboitem item = new Comboitem();
                item.setValue(c);
                item.setLabel(c.getSymbol());
                item.setDescription(c.getName());
                item.setParent(cmbCurrency);
                if (countryParam != null && c.getId().equals(countryParam.getCurrencyId().getId())) {
                    cmbCurrency.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_ADD)) {
                cmbCurrency.setSelectedIndex(1);
            } if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
                cmbCurrency.setDisabled(true);
            }
            
            
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
