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
import com.cms.commons.models.CommerceCategory;
import com.cms.commons.models.Country;
import com.cms.commons.models.Currency;
import com.cms.commons.models.Issuer;
import com.cms.commons.models.KindCard;
import com.cms.commons.models.LevelProduct;
import com.cms.commons.models.Product;
import com.cms.commons.models.ProductHasCommerceCategory;
import com.cms.commons.models.ProductUse;
import com.cms.commons.models.Program;
import com.cms.commons.models.ProgramType;
import com.cms.commons.models.SegmentCommerce;
import com.cms.commons.models.SegmentMarketing;
import com.cms.commons.models.StorageMedio;
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
import org.zkoss.zul.Datebox;

public class AdminProductController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private UtilsEJB utilsEJB = null;
    private ProductEJB productEJB = null;
    private ProgramEJB programEJB = null;
    private Product productParam;
    private Textbox txtName;
    private Textbox txtBinNumber;
    private Textbox txtValidityYears;
    private Textbox txtDaysBeforeExpiration;
    private Textbox txtDaysToInactivate;
    private Textbox txtDaysToActivate;
    private Textbox txtDaysToUse;
    private Textbox txtDaysToWithdrawCard;
    private Datebox txtBeginDateValidity;
    private Datebox txtEndDateValidity;
    private Combobox cmbCountry;
    private Combobox cmbCardType;
    private Combobox cmbIssuer;
    private Combobox cmbKindCard;
    private Combobox cmbProgram;
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
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
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
            //Guardar Producto
            product.setName(txtName.getText());
            product.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            product.setCardTypeId((CardType) cmbCardType.getSelectedItem().getValue());
            product.setBinSponsorId((BinSponsor) cmbBinSponsor.getSelectedItem().getValue());
            product.setIssuerId((Issuer) cmbIssuer.getSelectedItem().getValue());
            product.setKindCardId((KindCard) cmbKindCard.getSelectedItem().getValue());
            product.setProgramTypeId((ProgramType) cmbProgramType.getSelectedItem().getValue());
            product.setLevelProductId((LevelProduct) cmbLevelProduct.getSelectedItem().getValue());
            product.setBinNumber(txtBinNumber.getText());
            product.setProductUseId((ProductUse) cmbProductUse.getSelectedItem().getValue());
            product.setDomesticCurrencyId((Currency) cmbDomesticCurrency.getSelectedItem().getValue());
            product.setInternationalCurrencyId((Currency) cmbInternationalCurrency.getSelectedItem().getValue());
            product.setValidityYears((Integer.parseInt(txtValidityYears.getText())));
            product.setStorageMedioid((StorageMedio) cmbStorageMedio.getSelectedItem().getValue());
            product.setDaysBeforeExpiration((Integer.parseInt(txtDaysBeforeExpiration.getText())));
            product.setDaysToInactivate((Integer.parseInt(txtDaysToInactivate.getText())));
            product.setDaysToActivate((Integer.parseInt(txtDaysToActivate.getText())));
            product.setDaysToUse((Integer.parseInt(txtDaysToUse.getText())));
            product.setDaysToWithdrawCard((Integer.parseInt(txtDaysToWithdrawCard.getText())));
            product.setBeginDateValidity((txtBeginDateValidity.getValue()));
            product.setEndDateValidity((txtEndDateValidity.getValue())); 
            product.setsegmentMarketingId((SegmentMarketing) cmbSegmentMarketing.getSelectedItem().getValue());
            product.setProgramId((Program) cmbProgram.getSelectedItem().getValue());
            product = productEJB.saveProduct(product);
            productParam = product;
            
            //Guardar ProductHasCommerceCategory
            ProductHasCommerceCategory productHasCommerceCategory = new ProductHasCommerceCategory();
            productHasCommerceCategory.setProductId(product);
            productHasCommerceCategory.setCommerceCategoryId((CommerceCategory) cmbCommerceCategory.getSelectedItem().getValue());
            
            
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

    public void onChange$cmbSegmentCommerce() {
        SegmentCommerce segmentCommerce = (SegmentCommerce) cmbSegmentCommerce.getSelectedItem().getValue();
        loadCmbCommerceCategory(eventType, segmentCommerce.getId());
    }
    
    public void onChange$cmbProgramType() {
        ProgramType programType = (ProgramType) cmbProgramType.getSelectedItem().getValue();
        loadCmbProgram(eventType, programType.getId());
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
                loadCmbBinSponsor(eventType);
                loadCmbLevelProduct(eventType);
                loadCmbProductUse(eventType);
                loadCmbDomesticCurrency(eventType);
                loadCmbInternationalCurrency(eventType);
                loadCmbStorageMedio(eventType);
                loadCmbSegmentMarketing(eventType);
                loadCmbSegmentCommerce(eventType);
                onChange$cmbSegmentCommerce();
                onChange$cmbProgramType();
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
                loadCmbBinSponsor(eventType);
                loadCmbLevelProduct(eventType);
                loadCmbProductUse(eventType);
                loadCmbDomesticCurrency(eventType);
                loadCmbInternationalCurrency(eventType);
                loadCmbStorageMedio(eventType);
                loadCmbSegmentMarketing(eventType);
                loadCmbSegmentCommerce(eventType);
                onChange$cmbSegmentCommerce();
                onChange$cmbProgramType();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbIssuer(eventType);
                loadCmbCardType(eventType);
                loadCmbKindCard(eventType);
                loadCmbProgramType(eventType);
                loadCmbBinSponsor(eventType);
                loadCmbLevelProduct(eventType);
                loadCmbProductUse(eventType);
                loadCmbDomesticCurrency(eventType);
                loadCmbInternationalCurrency(eventType);
                loadCmbStorageMedio(eventType);
                loadCmbSegmentMarketing(eventType);
                loadCmbSegmentCommerce(eventType);
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
    
    private void loadCmbBinSponsor(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<BinSponsor> binSponsorList;
        try {
            binSponsorList = utilsEJB.getBinSponsor(request1);
            loadGenericCombobox(binSponsorList,cmbBinSponsor,"description",eventType,Long.valueOf(productParam != null? productParam.getBinSponsorId().getId(): 0) );            
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
    
    private void loadCmbLevelProduct(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<LevelProduct> levelProductList;
        try {
            levelProductList = productEJB.getLevelProduct(request1);
            loadGenericCombobox(levelProductList,cmbLevelProduct,"description",eventType,Long.valueOf(productParam != null? productParam.getLevelProductId().getId(): 0) );            
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
    
    private void loadCmbProductUse(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<ProductUse> productUseList;
        try {
            productUseList = productEJB.getProductUse(request1);
            loadGenericCombobox(productUseList,cmbProductUse,"description",eventType,Long.valueOf(productParam != null? productParam.getProductUseId().getId(): 0) );            
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
    
    private void loadCmbDomesticCurrency(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<Currency> domesticCurrencyList;
        try {
            domesticCurrencyList = utilsEJB.getCurrency(request1);
            loadGenericCombobox(domesticCurrencyList,cmbDomesticCurrency,"name",eventType,Long.valueOf(productParam != null? productParam.getDomesticCurrencyId().getId(): 0) );            
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
    
    private void loadCmbInternationalCurrency(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<Currency> internationalCurrencyList;
        try {
            internationalCurrencyList = utilsEJB.getCurrency(request1);
            loadGenericCombobox(internationalCurrencyList,cmbInternationalCurrency,"name",eventType,Long.valueOf(productParam != null? productParam.getInternationalCurrencyId().getId(): 0) );            
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
    
    private void loadCmbStorageMedio(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<StorageMedio> storageMedioList;
        try {
            storageMedioList = productEJB.getStorageMedio(request1);
            loadGenericCombobox(storageMedioList,cmbStorageMedio,"description",eventType,Long.valueOf(productParam != null? productParam.getStorageMedioid().getId(): 0) );            
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
    
    private void loadCmbSegmentMarketing(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<SegmentMarketing> segmentMarketingList;
        try {
            segmentMarketingList = productEJB.getSegmentMarketing(request1);
            loadGenericCombobox(segmentMarketingList,cmbSegmentMarketing,"name",eventType,Long.valueOf(productParam != null? productParam.getsegmentMarketingId().getId(): 0) );            
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
    
    private void loadCmbSegmentCommerce(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<SegmentCommerce> segmentCommerceList;
        try {
            segmentCommerceList = productEJB.getSegmentCommerce(request1);
            loadGenericCombobox(segmentCommerceList,cmbSegmentCommerce,"name",eventType,Long.valueOf(productParam != null? productParam.getProductHasCommerceCategory().getCommerceCategoryId().getsegmentCommerceId().getId(): 0) );            
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
    
    private void loadCmbCommerceCategory(Integer evenInteger, int segmentCommerceId) {
        EJBRequest request1 = new EJBRequest();
        cmbCommerceCategory.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_SEGMENT_COMMERCE_ID, segmentCommerceId);
        request1.setParams(params);
        List<CommerceCategory> commerceCategoryList;
        try {
            commerceCategoryList = productEJB.getCommerceCategoryBySegmentCommerce(request1);
            loadGenericCombobox(commerceCategoryList,cmbCommerceCategory,"economicActivity",evenInteger,Long.valueOf(productParam != null? productParam.getProductHasCommerceCategory().getCommerceCategoryId().getId(): 0) );            
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
    
     private void loadCmbProgram(Integer evenInteger, int programTypeId) {
        EJBRequest request1 = new EJBRequest();
        cmbProgram.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_PROGRAM_TYPE_ID, programTypeId);
        request1.setParams(params);
        List<Program> programList;
        try {
            programList = programEJB.getProgramByProgramType(request1);
            loadGenericCombobox(programList,cmbProgram,"name",evenInteger,Long.valueOf(productParam != null? productParam.getProgramId().getId(): 0) );            
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
