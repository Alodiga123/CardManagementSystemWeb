package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Country;
import com.cms.commons.models.Currency;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.OriginApplication;
import com.cms.commons.models.PersonType;
import com.cms.commons.models.Product;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;

public class AdminProductController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private UtilsEJB utilsEJB = null;
    private ProductEJB productEJB = null;
    private Product productParam;
    private Textbox txtName;
    private Combobox cmbCountry;
    private Combobox cmbCardType;
    private Combobox cmbBinSponsor;
    private Button btnSave;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        productParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Product) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
        //initView(eventType, "sp.crud.country");
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            productEJB = (ProductEJB) EJBServiceLocator.getInstance().get(EjbConstants.PRODUCT_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void onChange$cmbCountry() {
        cmbCardType.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
//        loadCmbPersonType(eventType, country.getId());
    }

    public void clearFields() {
        txtName.setRawValue(null);
        
    }

    private void loadFields(Product product) {
        try {
            txtName.setText(product.getName());
//            txtIdentityCode.setText(documentsPersonType.getCodeIdentificationNumber());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
//        txtDocumentPerson.setReadonly(true);
//        txtIdentityCode.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
//        if (txtDocumentPerson.getText().isEmpty()) {
//            txtDocumentPerson.setFocus(true);
//            this.showMessage("sp.error.field.cannotNull", true, null);
//        } else if (txtIdentityCode.getText().isEmpty()) {
//            txtIdentityCode.setFocus(true);
//            this.showMessage("sp.error.field.cannotNull", true, null);
//        } else {
//            return true;
//        }
        return false;
    }

    public void onClick$btnCodes() {
        Executions.getCurrent().sendRedirect("/docs/T-SP-E.164D-2009-PDF-S.pdf", "_blank");
    }

    public void onClick$btnShortNames() {
        Executions.getCurrent().sendRedirect("/docs/countries-abbreviation.pdf", "_blank");
    }

    private void saveProduct(Product _product) {
        try {
            Product product = null;
            if (_product != null) {
                product = _product;
            } else {//New Product
                product = new Product();
            }
//            product.setDescription(txtDocumentPerson.getText());
//            product.setCodeIdentificationNumber(txtIdentityCode.getText());
//            product.setPersonTypeId((PersonType) cmbPersonType.getSelectedItem().getValue());
            product = productEJB.saveProduct(product);
            productParam = product;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveProduct(null);
                    break;
                case WebConstants.EVENT_EDIT:
//                    saveProduct(product);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(productParam);
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(productParam);
                txtName.setDisabled(true);
//                txtIdentityCode.setDisabled(true);
                blockFields();
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_ADD:
                System.out.println("Agregar un documentsPersonType");
                loadCmbCountry(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbCountry(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;
        try {
            countries = utilsEJB.getCountries(request1);
            loadGenericCombobox(countries,cmbCountry, "name",evenInteger,Long.valueOf(productParam != null? productParam.getCountryId().getId(): 0) );            
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
    
    /*private void loadCmbPersonType(Integer evenInteger, int countryId) {
        EJBRequest request1 = new EJBRequest();
        cmbPersonType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        params.put(QueryConstants.PARAM_ORIGIN_APPLICATION_ID, Constants.ORIGIN_APPLICATION_CMS_ID);
        request1.setParams(params);
        List<PersonType> personTypes;
        try {
            personTypes = utilsEJB.getPersonTypesByCountry(request1);
            loadGenericCombobox(personTypes,cmbPersonType, "description",evenInteger,Long.valueOf(documentsPersonTypeParam != null? documentsPersonTypeParam.getPersonTypeId().getId(): 0) );            
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
    }*/
}