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
import com.cms.commons.models.CollectionsRequest;
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

public class AdminCollectionsRequestController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtName;
    private Textbox txtCode;
    private Textbox txtShortName;
    private Textbox txtAlternativeName1;
    
    private CollectionsRequest collectionsRequestParam;
    private UtilsEJB utilsEJB = null;
    private Combobox cmbCountry;
    private Combobox cmbPrograms;
    private Combobox cmbPersonType;
    private Combobox cmbProductType;
    private Button btnSave;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        collectionsRequestParam = (Sessions.getCurrent().getAttribute("object") != null) ? (CollectionsRequest) Sessions.getCurrent().getAttribute("object") : null;
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

//prueba
    }

   /* private void loadFields(CollectionsRequest collectionsRequest) {
        try {

        } catch (Exception ex) {
            showError(ex);
        }
    }*/

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

    private void saveCollectionsRequest(CollectionsRequest _collectionsRequest) {
        try {
            CollectionsRequest collectionsRequest = null;

            if (_collectionsRequest != null) {
                collectionsRequest = _collectionsRequest;
            } else {//New collectionsRequest
                collectionsRequest = new CollectionsRequest();
            }
            
            collectionsRequest.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            collectionsRequest = utilsEJB.saveCollectionRequest(collectionsRequest);
            collectionsRequestParam = collectionsRequest;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveCollectionsRequest(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveCollectionsRequest(collectionsRequestParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadCmbCountry(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadCmbCountry(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbCountry(Integer evenInteger) {
        //cmbCurrency
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;

        try {
            countries = utilsEJB.getCountries(request1);
            cmbCountry.getItems().clear();
            for (Country c : countries) {

                Comboitem item = new Comboitem();
                item.setValue(c);
                item.setLabel(c.getCode());
                item.setDescription(c.getName());
                item.setParent(cmbCountry);
                if (collectionsRequestParam != null && c.getId().equals(collectionsRequestParam.getCountryId().getId())) {
                    cmbCountry.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_ADD)) {
                cmbCountry.setSelectedIndex(1);
            } if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
                cmbCountry.setDisabled(true);
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