package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProductEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.CommerceCategory;
import com.cms.commons.models.Product;
import com.cms.commons.models.ProductHasCommerceCategory;
import com.cms.commons.models.SegmentCommerce;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

public class AdminAddCommerceCategoryController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Listbox lbxRecords;
    private Combobox cmbSegmentCommerce;
    private Combobox cmbCommerceCategory;
    private Label txtProduct;
    private ProductEJB productEJB = null;
    private ProductHasCommerceCategory productHasCommerceCategoryParam;
    private Button btnSave;
    public Window winAdminAddCommerceCategory;
    private Integer eventType;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                productHasCommerceCategoryParam = (Sessions.getCurrent().getAttribute("object") != null) ? (ProductHasCommerceCategory) Sessions.getCurrent().getAttribute("object") : null;
                break;
            case WebConstants.EVENT_VIEW:
                productHasCommerceCategoryParam = (ProductHasCommerceCategory) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_ADD:
                productHasCommerceCategoryParam = null;
                break;
        }
        initialize();
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

    public void onChange$cmbSegmentCommerce() {
        cmbCommerceCategory.setVisible(true);
        SegmentCommerce segmentCommerce = (SegmentCommerce) cmbSegmentCommerce.getSelectedItem().getValue();
        loadCmbCommerceCategory(eventType, segmentCommerce.getId());
    }

    public void clearFields() {
    }

    private void loadFields(ProductHasCommerceCategory ProductHasCommerceCategory) {
         try {
             cmbSegmentCommerce.setText(ProductHasCommerceCategory.toString());
           
            } catch (Exception ex) {
            showError(ex);
            }              
    }
    
//    private void loadField(ProductHasCommerceCategory productHasCommerceCategory) {
//        Product product = null;
//        AdminProductController adminProduct = new AdminProductController();
//        if (adminProduct.getProductParent().getId() != null) {
//            product = adminProduct.getProductParent();
//        }
//        txtProduct.setValue(product.getName());
//    }

    public void blockFields() {
        cmbSegmentCommerce.setDisabled(true);
        btnSave.setVisible(false);
    }

    private void saveProductHasCommerceCategory(ProductHasCommerceCategory _productHasCommerceCategory) {
        Product product = null;
        
        try {
            ProductHasCommerceCategory productHasCommerceCategory = null;

            if (_productHasCommerceCategory != null) {
                productHasCommerceCategory = _productHasCommerceCategory;
            } else {
                productHasCommerceCategory = new ProductHasCommerceCategory();
            }
            
            //Product
            AdminProductController adminProduct = new AdminProductController();
            if (adminProduct.getProductParent().getId() != null) {
                product = adminProduct.getProductParent();
            }

            //Guardar ProductHasCommerceCategory
            productHasCommerceCategory = new ProductHasCommerceCategory();
            productHasCommerceCategory.setProductId(product);
            productHasCommerceCategory.setCommerceCategoryId((CommerceCategory) cmbCommerceCategory.getSelectedItem().getValue());
            productHasCommerceCategory = productEJB.saveProductHasCommerceCategory(productHasCommerceCategory);
            productHasCommerceCategoryParam = productHasCommerceCategory;
            
            this.showMessage("sp.common.save.success", false, null);
            EventQueues.lookup("updateCommerceCategory", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    
    }

    public void onClick$btnSave() {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveProductHasCommerceCategory(null);
                break;
            case WebConstants.EVENT_EDIT:
                saveProductHasCommerceCategory(productHasCommerceCategoryParam);
                break;
            default:
                break;
        }
    }

    public void onClick$btnBack() {
        winAdminAddCommerceCategory.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(productHasCommerceCategoryParam);
                loadCmbSegmentCommerce(eventType);
                onChange$cmbSegmentCommerce();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(productHasCommerceCategoryParam);
                blockFields();
                loadCmbSegmentCommerce(eventType);
                onChange$cmbSegmentCommerce();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbSegmentCommerce(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbSegmentCommerce(Integer eventType) {
        EJBRequest request1 = new EJBRequest();
        List<SegmentCommerce> segmentCommerceList;
        try {
            segmentCommerceList = productEJB.getSegmentCommerce(request1);
            loadGenericCombobox(segmentCommerceList,cmbSegmentCommerce,"name",eventType,Long.valueOf(productHasCommerceCategoryParam != null? productHasCommerceCategoryParam.getCommerceCategoryId().getsegmentCommerceId().getId(): 0) );            
            segmentCommerceList = null;
            System.gc();
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
            loadGenericCombobox(commerceCategoryList,cmbCommerceCategory,"economicActivity",evenInteger,Long.valueOf(productHasCommerceCategoryParam != null? productHasCommerceCategoryParam.getCommerceCategoryId().getId(): 0) );            
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
