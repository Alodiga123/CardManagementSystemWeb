package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Country;
import com.cms.commons.models.CollectionsRequest;
import com.cms.commons.models.PersonType;
import com.cms.commons.models.ProductType;
import com.cms.commons.models.Program;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;

public class AdminRequestController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtDescription;
    
    private CollectionsRequest collectionsRequestParam;
    private UtilsEJB utilsEJB = null;
    private ProgramEJB programEJB = null;
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
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtDescription.setRawValue(null);
    }

    private void loadFields(CollectionsRequest collectionsRequest) {
        try {
            txtDescription.setText(collectionsRequest.getDescription());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtDescription.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtDescription.getText().isEmpty()) {
            txtDescription.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;

    }

    private void saveCollectionsRequest(CollectionsRequest _collectionsRequest) {
        try {
            CollectionsRequest collectionsRequest = null;

            if (_collectionsRequest != null) {
                collectionsRequest = _collectionsRequest;
            } else {//New collectionsRequest
                collectionsRequest = new CollectionsRequest();
            }
            collectionsRequest.setDescription(txtDescription.getText());
            collectionsRequest.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            collectionsRequest.setProductTypeId((ProductType) cmbProductType.getSelectedItem().getValue());
            collectionsRequest.setProgramId((Program) cmbPrograms.getSelectedItem().getValue());
            collectionsRequest.setPersonTypeId((PersonType) cmbPersonType.getSelectedItem().getValue());
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
                loadFields(collectionsRequestParam);
                loadCmbCountry(eventType);
                loadCmbProductType(eventType);
                loadCmbPrograms(eventType);
                loadCmbPersonType(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(collectionsRequestParam);
                txtDescription.setDisabled(true);
                loadCmbCountry(eventType);
                loadCmbProductType(eventType);
                loadCmbPrograms(eventType);
                loadCmbPersonType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbProductType(eventType);
                loadCmbPrograms(eventType);
                loadCmbPersonType(eventType);
                break;
            default:
                break;
        }
    }
    
  /*  private void loadCmbCountry(Integer evenInteger) {
        //cmbCurrency
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;
 
        try {
            countries = utilsEJB.getCountries(request1);
            cmbCountry.getItems().clear();
            for (Country c : countries) {
 
                Comboitem item = new Comboitem();
                item.setValue(c);
                item.setLabel(c.getName());
                item.setDescription(c.getName());
                item.setParent(cmbCountry);
                if (collectionsRequestParam != null && c.getId().equals(collectionsRequestParam.getCountryId().getId())) {
                    cmbCountry.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_ADD)) {
                cmbCountry.setSelectedIndex(0);
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
    
    private void loadCmbProductType(Integer evenInteger) {
        //cmbProductType
        EJBRequest request1 = new EJBRequest();
        List<ProductType> productTypes;
 
        try {
            productTypes = utilsEJB.getProductTypes(request1);
            cmbProductType.getItems().clear();
            for (ProductType c : productTypes) {
 
                Comboitem item = new Comboitem();
                item.setValue(c);
                item.setLabel(c.getName());
                item.setDescription(c.getName());
                item.setParent(cmbProductType);
                if (collectionsRequestParam != null && c.getId().equals(collectionsRequestParam.getProductTypeId().getId())) {
                    cmbProductType.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_ADD)) {
                cmbProductType.setSelectedIndex(0);
            } if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
                cmbProductType.setDisabled(true);
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
    
    private void loadCmbPrograms(Integer evenInteger) {
        //cmbPrograms
        EJBRequest request1 = new EJBRequest();
        List<Program> programs;
 
        try {
            programs = programEJB.getProgram(request1);
            cmbPrograms.getItems().clear();
            for (Program c : programs) {
 
                Comboitem item = new Comboitem();
                item.setValue(c);
                item.setLabel(c.getName());
                item.setDescription(c.getName());
                item.setParent(cmbPrograms);
                if (collectionsRequestParam != null && c.getId().equals(collectionsRequestParam.getProgramId().getId())) {
                    cmbPrograms.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_ADD)) {
                cmbPrograms.setSelectedIndex(0);
            } if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
                cmbPrograms.setDisabled(true);
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
    
    private void loadCmbPersonType(Integer evenInteger) {
        //cmbPersonType
        EJBRequest request1 = new EJBRequest();
        List<PersonType> personTypes;
 
        try {
            personTypes = utilsEJB.getPersonTypes(request1);
            cmbPersonType.getItems().clear();
            for (PersonType c : personTypes) {
 
                Comboitem item = new Comboitem();
                item.setValue(c);
                item.setLabel(c.getDescription());
                item.setDescription(c.getDescription());
                item.setParent(cmbPersonType);
                if (collectionsRequestParam != null && c.getId().equals(collectionsRequestParam.getPersonTypeId().getId())) {
                    cmbPersonType.setSelectedItem(item);
                }
            }
            if (evenInteger.equals(WebConstants.EVENT_ADD)) {
                cmbPersonType.setSelectedIndex(0);
            } if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
                cmbPersonType.setDisabled(true);
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
    */
    
    private void loadCmbCountry(Integer evenInteger) {
        //cmbCountry
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;

        try {
            countries = utilsEJB.getCountries(request1);
            loadGenericCombobox(countries,cmbCountry, "name",evenInteger,Long.valueOf(collectionsRequestParam != null? collectionsRequestParam.getId(): 0) );            
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
    
    
    private void loadCmbProductType(Integer evenInteger) {
        //cmbProductType
        
        EJBRequest request1 = new EJBRequest();
        List<ProductType> productTypes;
        try {
            productTypes = utilsEJB.getProductTypes(request1);
            loadGenericCombobox(productTypes,cmbProductType, "name",evenInteger,Long.valueOf(collectionsRequestParam != null? collectionsRequestParam.getId(): 0) );            
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
     
    private void loadCmbPrograms(Integer evenInteger) {
        //cmbProductType
        EJBRequest request1 = new EJBRequest();
        List<Program> programs;
        
        try {
            programs = programEJB.getProgram(request1);
            loadGenericCombobox(programs,cmbPrograms, "name",evenInteger,Long.valueOf(collectionsRequestParam != null? collectionsRequestParam.getId(): 0) );            
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
    
    private void loadCmbPersonType(Integer evenInteger) {
        //cmbPersonType
        EJBRequest request1 = new EJBRequest();
        List<PersonType> personTypes;
        
        try {
            personTypes = utilsEJB.getPersonTypes(request1);
            loadGenericCombobox(personTypes,cmbPersonType, "description",evenInteger,Long.valueOf(collectionsRequestParam != null? collectionsRequestParam.getId(): 0) );            
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