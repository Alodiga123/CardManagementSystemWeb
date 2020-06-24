package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.CollectionType;
import com.cms.commons.models.Country;
import com.cms.commons.models.CollectionsRequest;
import com.cms.commons.models.PersonType;
import com.cms.commons.models.Program;
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
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;

public class AdminCollectionsRequestController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private CollectionsRequest collectionsRequestParam;
    private Label lblProductType;
    private UtilsEJB utilsEJB = null;
    private RequestEJB requestEJB = null;
    private ProgramEJB programEJB = null;
    private Combobox cmbCountry;
    private Combobox cmbPrograms;
    private Combobox cmbPersonType;
    private Combobox cmbCollectionType;
    public static Program program = null;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            collectionsRequestParam = null;
        } else {
            collectionsRequestParam = (CollectionsRequest) Sessions.getCurrent().getAttribute("object");
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.collectionsRequest.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.collectionsRequest.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.collectionsRequest.add"));
                break;
            default:
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        btnSave.setVisible(false);
    }

    private void loadFields(CollectionsRequest collectionsRequest) {
        btnSave.setVisible(true);
    }

    public void onChange$cmbCountry() {
        cmbCollectionType.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbCollectionType(eventType, country.getId());
    }
    
    public void onChange$cmbPrograms() {
        
        program = (Program) cmbPrograms.getSelectedItem().getValue();
        lblProductType.setVisible(true);
        loadProgramData(program);
    }
    
    public void loadProgramData(Program program) {
        lblProductType.setValue(program.getProductTypeId().getName());
    }

    public void blockFields() {
        btnSave.setVisible(false);
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
            collectionsRequest.setProductTypeId(((Program) cmbPrograms.getSelectedItem().getValue()).getProductTypeId());
            collectionsRequest.setProgramId((Program) cmbPrograms.getSelectedItem().getValue());
            collectionsRequest.setPersonTypeId((PersonType) cmbPersonType.getSelectedItem().getValue());
            collectionsRequest.setCollectionTypeId((CollectionType) cmbCollectionType.getSelectedItem().getValue());
            collectionsRequest = requestEJB.saveCollectionRequest(collectionsRequest);
            collectionsRequestParam = collectionsRequest;
            this.showMessage("sp.common.save.success", false, null);
            btnSave.setVisible(false);
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public Boolean validateEmpty() {
        if (cmbCountry.getSelectedItem() == null) {
            cmbCountry.setFocus(true);
            this.showMessage("cms.error.country.notSelected", true, null);
        } else if (cmbPrograms.getText().isEmpty()) {
            cmbPrograms.setFocus(true);
            this.showMessage("cms.error.program.notSelected", true, null);
        } else if (cmbPersonType.getSelectedItem() == null) {
            cmbPersonType.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (cmbCollectionType.getText().isEmpty()) {
            cmbCollectionType.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
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
                loadCmbPrograms(eventType);
                loadCmbPersonType(eventType);
                onChange$cmbCountry();
                onChange$cmbPrograms();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(collectionsRequestParam);
                blockFields();
                loadCmbCountry(eventType);
                loadCmbPrograms(eventType);
                loadCmbPersonType(eventType);
                onChange$cmbCountry();
                onChange$cmbPrograms();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbPrograms(eventType);
                loadCmbPersonType(eventType);
                onChange$cmbCountry();
                onChange$cmbPrograms();
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(collectionsRequestParam != null ? collectionsRequestParam.getCountryId().getId() : 0));
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
        EJBRequest request1 = new EJBRequest();
        List<Program> programs;

        try {
            programs = programEJB.getProgram(request1);
            loadGenericCombobox(programs, cmbPrograms, "name", evenInteger, Long.valueOf(collectionsRequestParam != null ? collectionsRequestParam.getProgramId().getId() : 0));
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
        EJBRequest request1 = new EJBRequest();
        List<PersonType> personTypes;

        try {
            personTypes = utilsEJB.getPersonTypes(request1);
            loadGenericCombobox(personTypes, cmbPersonType, "description", evenInteger, Long.valueOf(collectionsRequestParam != null ? collectionsRequestParam.getPersonTypeId().getId() : 0));
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

    private void loadCmbCollectionType(Integer evenInteger, int countryId) {
        EJBRequest request = new EJBRequest();
        cmbCollectionType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        request.setParams(params);
        List<CollectionType> collectionTypes;
        try {
            collectionTypes = requestEJB.getCollectionTypeByCountry(request);
            loadGenericCombobox(collectionTypes, cmbCollectionType, "description", evenInteger, Long.valueOf(collectionsRequestParam != null ? collectionsRequestParam.getCollectionTypeId().getId() : 0));
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
