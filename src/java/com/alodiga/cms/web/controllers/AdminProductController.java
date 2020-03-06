package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.BinSponsor;
import com.cms.commons.models.Country;
import com.cms.commons.models.Currency;
import com.cms.commons.models.Issuer;
import com.cms.commons.models.KindCard;
import com.cms.commons.models.LevelProduct;
import com.cms.commons.models.Product;
import com.cms.commons.models.ProductType;
import com.cms.commons.models.ProductUse;
import com.cms.commons.models.Program;
import com.cms.commons.models.ProgramType;
import com.cms.commons.models.SegmentMarketing;
import com.cms.commons.models.StorageMedio;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Toolbarbutton;

public class AdminProductController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private UtilsEJB utilsEJB = null;
    private ProductEJB productEJB = null;
    private ProgramEJB programEJB = null;
    private Product productParam;
    private Textbox txtName;
    private Textbox txtDaysBeforeExpiration;
    private Textbox txtDaysToInactivate;
    private Textbox txtDaysToActivate;
    private Textbox txtDaysToUse;
    private Textbox txtDaysToWithdrawCard;
    private Datebox dtbBeginDateValidity;
    private Datebox dtbEndDateValidity;
    private Combobox cmbCountry;
    private Combobox cmbProgram;
    private Combobox cmbProgramType;
    private Combobox cmbKindCard;
    private Combobox cmbLevelProduct;
    private Combobox cmbProductUse;
    private Combobox cmbDomesticCurrency;
    private Combobox cmbInternationalCurrency;
    private Combobox cmbStorageMedio;
    private Combobox cmbSegmentMarketing;
    private Radio r24Months;
    private Radio r36Months;
    private Radio r48Months;
    private Label lblIssuer;
    private Label lblProductType;
    private Label lblBinSponsor;
    private Label lblBinNumber;
    private Tab tabCommerceCategory;
    private Tab tabRestrictions; 
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;
    public static Product productParent = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        productParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Product) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:   
                tbbTitle.setLabel(Labels.getLabel("cms.crud.product.edit"));
                break;
            case WebConstants.EVENT_VIEW:  
                tbbTitle.setLabel(Labels.getLabel("cms.crud.product.view"));
                break;
            case WebConstants.EVENT_ADD:
                tabCommerceCategory.setDisabled(true);
                tabRestrictions.setDisabled(true);
                break;
            default:
                break;
        }
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
        txtDaysBeforeExpiration.setRawValue(null);
        txtDaysToInactivate.setRawValue(null);
        txtDaysToActivate.setRawValue(null);
        txtDaysToUse.setRawValue(null);
        txtDaysToWithdrawCard.setRawValue(null);
        dtbBeginDateValidity.setRawValue(null);
        dtbEndDateValidity.setRawValue(null);
    }

    public Product getProductParent() {
        return productParent;
    }
    
    private void loadFields(Product product) {
        try {
            txtName.setText(product.getName());
            txtDaysBeforeExpiration.setValue(product.getDaysBeforeExpiration().toString());
            txtDaysToInactivate.setValue(product.getDaysToInactivate().toString());
            txtDaysToActivate.setValue(product.getDaysToActivate().toString());
            txtDaysToUse.setValue(product.getDaysToUse().toString());
            txtDaysToWithdrawCard.setValue(product.getDaysToWithdrawCard().toString());
            dtbBeginDateValidity.setValue(product.getBeginDateValidity());
            dtbEndDateValidity.setValue(product.getEndDateValidity());
            switch (product.getValidityMonths()) {
                case WebConstants.VALIDITY_MONTH_24:   
                    r24Months.setChecked(true);
                    break;
                case WebConstants.VALIDITY_MONTH_36:  
                    r36Months.setChecked(true);
                    break;
                case WebConstants.VALIDITY_MONTH_48:
                    r48Months.setChecked(true);
                    break;
            }
            loadProgramData(product.getProgramId());
            validateProductUse(product.getProductUseId().getId());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtName.setReadonly(true);
        cmbCountry.setReadonly(true);
        btnSave.setVisible(false);
    }

    private void saveProduct(Product _product) throws RegisterNotFoundException, NullParameterException, GeneralException {
        int validityMonth = 0;
        try {
            Product product = null;
            
            if (_product != null) {
                product = _product;
            } else {//New Product
                product = new Product();
            }
            
            if (r24Months.isChecked()) {
                validityMonth = WebConstants.VALIDITY_MONTH_24;
            } else if (r36Months.isChecked()) {
                validityMonth = WebConstants.VALIDITY_MONTH_36;
            } else {
                validityMonth = WebConstants.VALIDITY_MONTH_48;
            }
    
            //Guardar Producto
            product.setName(txtName.getText());
            product.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            product.setIssuerId(((Program) cmbProgram.getSelectedItem().getValue()).getIssuerId());
            product.setProductTypeId(((Program) cmbProgram.getSelectedItem().getValue()).getProductTypeId());
            product.setBinSponsorId(((Program) cmbProgram.getSelectedItem().getValue()).getBinSponsorId());
            product.setBinNumber(((Program) cmbProgram.getSelectedItem().getValue()).getBiniinNumber());
            product.setKindCardId((KindCard) cmbKindCard.getSelectedItem().getValue());
            product.setProgramTypeId((ProgramType) cmbProgramType.getSelectedItem().getValue());
            product.setLevelProductId((LevelProduct) cmbLevelProduct.getSelectedItem().getValue());
            product.setProductUseId((ProductUse) cmbProductUse.getSelectedItem().getValue());
            product.setDomesticCurrencyId((Currency) cmbDomesticCurrency.getSelectedItem().getValue());
            product.setInternationalCurrencyId((Currency) cmbInternationalCurrency.getSelectedItem().getValue());
            product.setStorageMedioid((StorageMedio) cmbStorageMedio.getSelectedItem().getValue());
            product.setDaysBeforeExpiration((Integer.parseInt(txtDaysBeforeExpiration.getText())));
            product.setDaysToInactivate((Integer.parseInt(txtDaysToInactivate.getText())));
            product.setDaysToActivate((Integer.parseInt(txtDaysToActivate.getText())));
            product.setDaysToUse((Integer.parseInt(txtDaysToUse.getText())));
            product.setDaysToWithdrawCard((Integer.parseInt(txtDaysToWithdrawCard.getText())));
            product.setBeginDateValidity((dtbBeginDateValidity.getValue()));
            product.setEndDateValidity((dtbEndDateValidity.getValue())); 
            product.setsegmentMarketingId((SegmentMarketing) cmbSegmentMarketing.getSelectedItem().getValue());
            product.setProgramId((Program) cmbProgram.getSelectedItem().getValue());
            product.setValidityMonths(validityMonth);
            product = productEJB.saveProduct(product);
            productParam = product;
            productParent = product;
            this.showMessage("sp.common.save.success", false, null);
            tabCommerceCategory.setDisabled(false);
            tabRestrictions.setDisabled(false);
        } catch (Exception ex) {
            showError(ex);
        }
    }
            
        public Boolean validateEmpty() {
        if (txtName.getText().isEmpty()) {
            txtName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }
    
    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException {
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
    
    public void onChange$cmbProgramType() {
        cmbProgram.setVisible(true);
        ProgramType programType = (ProgramType) cmbProgramType.getSelectedItem().getValue();
        loadCmbProgram(eventType, programType.getId());
    }
    
    public void onChange$cmbProgram() {
        Program program = (Program) cmbProgram.getSelectedItem().getValue();
        lblIssuer.setVisible(true);
        lblProductType.setVisible(true);
        lblBinSponsor.setVisible(true);
        lblBinNumber.setVisible(true);
        loadProgramData(program);
    }
    
    public void loadProgramData(Program program) {
        lblIssuer.setValue(program.getIssuerId().getId().toString());
        lblProductType.setValue(program.getProductTypeId().getName());
        lblBinSponsor.setValue(program.getBinSponsorId().getDescription());
        lblBinNumber.setValue(program.getBiniinNumber());
    }
    
    public void onChange$cmbProductUse() {
        ProductUse productUse = (ProductUse) cmbProductUse.getSelectedItem().getValue();
        validateProductUse(productUse.getId());
    }  
    
    public void validateProductUse(int productUseId) {
        switch (productUseId) {
                case 1:
                    cmbDomesticCurrency.setDisabled(false);
                    cmbInternationalCurrency.setDisabled(true);
                    break;
                case 2:
                    cmbDomesticCurrency.setDisabled(true);
                    cmbInternationalCurrency.setDisabled(false);
                    break;
                case 3:
                    cmbDomesticCurrency.setDisabled(false);
                    cmbInternationalCurrency.setDisabled(false);
                    break;
            }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                productParent = productParam;
                loadFields(productParam);
                loadCmbCountry(eventType);
                loadCmbKindCard(eventType);
                loadCmbProgramType(eventType);
                loadCmbLevelProduct(eventType);
                loadCmbProductUse(eventType);
                loadCmbDomesticCurrency(eventType);
                loadCmbInternationalCurrency(eventType);
                loadCmbStorageMedio(eventType);
                loadCmbSegmentMarketing(eventType);
                onChange$cmbProgramType();
                break;
            case WebConstants.EVENT_VIEW:
                productParent = productParam;
                loadFields(productParam);
                txtName.setReadonly(true);
                r24Months.setDisabled(true);
                r36Months.setDisabled(true);
                r48Months.setDisabled(true);
                txtDaysBeforeExpiration.setReadonly(true);
                txtDaysToInactivate.setReadonly(true);
                txtDaysToActivate.setReadonly(true);
                txtDaysToUse.setReadonly(true);
                txtDaysToWithdrawCard.setReadonly(true);
                dtbBeginDateValidity.setReadonly(true);
                dtbEndDateValidity.setReadonly(true);
                blockFields();
                loadCmbCountry(eventType);
                loadCmbKindCard(eventType);
                loadCmbProgramType(eventType);
                loadCmbLevelProduct(eventType);
                loadCmbProductUse(eventType);
                loadCmbDomesticCurrency(eventType);
                loadCmbInternationalCurrency(eventType);
                loadCmbStorageMedio(eventType);
                loadCmbSegmentMarketing(eventType);
                onChange$cmbProgramType();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbKindCard(eventType);
                loadCmbProgramType(eventType);
                loadCmbLevelProduct(eventType);
                loadCmbProductUse(eventType);
                loadCmbDomesticCurrency(eventType);
                loadCmbInternationalCurrency(eventType);
                loadCmbStorageMedio(eventType);
                loadCmbSegmentMarketing(eventType);
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

    private void setText(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
