package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.BinSponsor;
import com.cms.commons.models.CardType;
import com.cms.commons.models.Country;
import com.cms.commons.models.Currency;
import com.cms.commons.models.Issuer;
import com.cms.commons.models.KindCard;
import com.cms.commons.models.Product;
import com.cms.commons.models.ProgramType;
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
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;

public class AdminProductController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private UtilsEJB utilsEJB = null;
    private ProductEJB productEJB = null;
    private Product productParam;
    private Textbox txtName;
    private Textbox txtBinNumber;
    private Textbox txtValidityYears;
    private Textbox txtDaysBeforeExpiration;
    private Textbox txtDaysToInactivate;
    private Textbox txtDaysToActivate;
    private Textbox txtDaysToUse;
    private Textbox txtDaysToWithdrawCard;
    private Textbox txtBeginDateValidity;
    private Textbox txtEndDateValidity;
    private Combobox cmbCountry;
    private Combobox cmbCardType;
    private Combobox cmbIssuer;
    private Combobox cmbKindCard;
    private Combobox cmbProgramType;
    private Combobox cmbBinSponsor;
    private Combobox cmbLevelProduct;
    private Combobox cmbProductUse;
    private Combobox cmbDomesticCurrency;
    private Combobox cmbInternationalCurrency;
    private Combobox cmbStorageMedio;
    private Combobox cmbSegmentCommerce;
    private Combobox cmbCommerceCategory;
    private Combobox cmbSegmentMarketing;
    private Tab tabNetwork;
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
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void clearFields() {
        txtName.setRawValue(null);
        txtBinNumber.setRawValue(null);
        txtValidityYears.setRawValue(null);
        txtDaysBeforeExpiration.setRawValue(null);
        txtDaysToInactivate.setRawValue(null);
        txtDaysToActivate.setRawValue(null);
        txtDaysToUse.setRawValue(null);
        txtDaysToWithdrawCard.setRawValue(null);
        txtBeginDateValidity.setRawValue(null);
        txtEndDateValidity.setRawValue(null);
    }
    
    private void loadFields(Product product) {
        try {
            txtName.setText(product.getName());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtName.setReadonly(true);
        cmbCountry.setReadonly(true);
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
            product.setName(txtName.getText());
            product.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            product.setCardTypeId((CardType) cmbCardType.getSelectedItem().getValue());
            product.setBinSponsorId((BinSponsor) cmbBinSponsor.getSelectedItem().getValue());
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
                    saveProduct(productParam);
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
                loadCmbIssuer(eventType);
                loadCmbCardType(eventType);
                loadCmbKindCard(eventType);
                loadCmbProgramType(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(productParam);
                txtName.setDisabled(true);
                blockFields();
                loadCmbCountry(eventType);
                loadCmbIssuer(eventType);
                loadCmbCardType(eventType);
                loadCmbKindCard(eventType);
                loadCmbProgramType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbIssuer(eventType);
                loadCmbCardType(eventType);
                loadCmbKindCard(eventType);
                loadCmbProgramType(eventType);
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

    private void loadCmbIssuer(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<Issuer> issuerList;
        try {
            issuerList = utilsEJB.getIssuers(request1);
            loadGenericCombobox(issuerList,cmbIssuer,"name",eventType,Long.valueOf(productParam != null? productParam.getIssuerId().getId(): 0) );            
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

    private void loadCmbCardType(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<CardType> cardTypeList;
        try {
            cardTypeList = utilsEJB.getCardTypes(request1);
            loadGenericCombobox(cardTypeList,cmbCardType,"description",eventType,Long.valueOf(productParam != null? productParam.getCardTypeId().getId(): 0) );            
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
    
    private void loadCmbKindCard(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<KindCard> kindCardList;
        try {
            kindCardList = utilsEJB.getKindCard(request1);
            loadGenericCombobox(kindCardList,cmbKindCard,"description",eventType,Long.valueOf(productParam != null? productParam.getKindCardId().getId(): 0) );            
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
    
    private void loadCmbProgramType(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<ProgramType> programTypeList;
        try {
            programTypeList = utilsEJB.getProgramType(request1);
            loadGenericCombobox(programTypeList,cmbProgramType,"name",eventType,Long.valueOf(productParam != null? productParam.getProgramTypeId().getId(): 0) );            
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
