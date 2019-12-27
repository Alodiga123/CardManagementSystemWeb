package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Country;
import com.cms.commons.models.Person;
import com.cms.commons.models.PersonType;
import com.cms.commons.models.ProductType;
import com.cms.commons.models.Program;
import com.cms.commons.models.Request;
import com.cms.commons.models.RequestType;
import com.cms.commons.models.Sequences;
import com.cms.commons.models.StatusRequest;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;

import org.zkoss.zul.Toolbarbutton;

public class AdminRequestController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Request requestParam;
    public static Request requestCard = null;
    private UtilsEJB utilsEJB = null;
    private ProgramEJB programEJB = null;
    private PersonEJB personEJB = null;
    private RequestEJB requestEJB = null;
    private Combobox cmbCountry;
    private Combobox cmbPrograms;
    private Combobox cmbPersonType;
    private Combobox cmbProductType;
    private Combobox cmbRequestType;
    private Button btnSave;
    private Tab tabMain;
    public Integer eventType;
    private Toolbarbutton tbbTitle;
    public Tabbox tb;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        requestParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Request) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public Request getRequest() {
        return this.requestCard;
    }
    
    public void onChange$cmbCountry() {
        cmbPersonType.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbPersonType(eventType, country.getId());
    }

    public void clearFields() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadFields(Request request) {
        try {
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        btnSave.setVisible(false);
    }

    private void saveRequest(Request _request) {
        try {
            Request request = null;

            if (_request != null) {
                request = _request;
            } else {//New Request
                request = new Request();
            }
            //Obtiene el numero de secuencia para documento Request
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.DOCUMENT_TYPE_KEY, Constants.DOCUMENT_TYPE_REQUEST);
            request1.setParams(params);
            List<Sequences> sequence = utilsEJB.getSequencesByDocumentType(request1);
            String numberRequest = utilsEJB.generateNumberSequence(sequence,Constants.ORIGIN_APPLICATION_CMS_ID);
            //colocar estatus de solicitud "EN PROCESO"
            request1 = new EJBRequest();
            request1.setParam(Constants.STATUS_REQUEST_IN_PROCESS);
            StatusRequest statusRequest = utilsEJB.loadStatusRequest(request1);
            //Solicitante sin registrar
            request1 = new EJBRequest();
            request1.setParam(Constants.PERSON_NOT_REGISTER);
            Person personNotRegister = personEJB.loadPerson(request1);
            //Guarda la solicitud en la BD
            request.setRequestNumber(numberRequest);
            Date dateRequest = new Date();
            request.setRequestDate(dateRequest);
            request.setPersonId(personNotRegister);
            request.setStatusRequestId(statusRequest);
            request.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            request.setPersonTypeId((PersonType) cmbPersonType.getSelectedItem().getValue());
            request.setProductTypeId((ProductType) cmbProductType.getSelectedItem().getValue());
            request.setProgramId((Program) cmbPrograms.getSelectedItem().getValue());
            request.setRequestTypeId((RequestType) cmbRequestType.getSelectedItem().getValue());
            request = requestEJB.saveRequest(request);
            requestParam = request;
            this.showMessage("sp.common.save.success", false, null);
            tabMain.setSelected(true);
            requestCard = request;
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void onClick$btnSave() {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                saveRequest(null);
                break;
            case WebConstants.EVENT_EDIT:
                saveRequest(requestParam);
                break;
            default:
                break;
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadCmbCountry(eventType);
                loadCmbProductType(eventType);
                loadCmbProgram(eventType);
                loadCmbRequestType(eventType);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_VIEW:
                loadCmbCountry(eventType);
                loadCmbProductType(eventType);
                loadCmbProgram(eventType);
                loadCmbRequestType(eventType);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbProductType(eventType);
                loadCmbProgram(eventType);
                loadCmbRequestType(eventType);
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
            loadGenericCombobox(countries,cmbCountry, "name",evenInteger,Long.valueOf(requestParam != null? requestParam.getCountryId().getId(): 0) );            
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
        EJBRequest request1 = new EJBRequest();
        List<ProductType> productTypes;
        try {
            productTypes = utilsEJB.getProductTypes(request1);
            loadGenericCombobox(productTypes,cmbProductType, "name",evenInteger,Long.valueOf(requestParam != null? requestParam.getProductTypeId().getId(): 0) );            
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
     
    private void loadCmbProgram(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Program> programs;
        try {
            programs = programEJB.getProgram(request1);
            loadGenericCombobox(programs,cmbPrograms, "name",evenInteger,Long.valueOf(requestParam != null? requestParam.getProgramId().getId(): 0) );            
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
    
    private void loadCmbPersonType(Integer evenInteger, int countryId) {
        EJBRequest request1 = new EJBRequest();
        cmbPersonType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        params.put(QueryConstants.PARAM_ORIGIN_APPLICATION_ID, Constants.ORIGIN_APPLICATION_CMS_ID);
        request1.setParams(params);
        List<PersonType> personTypes;
        try {
            personTypes = utilsEJB.getPersonTypesByCountry(request1);
            loadGenericCombobox(personTypes,cmbPersonType, "description",evenInteger,Long.valueOf(requestParam != null? requestParam.getPersonTypeId().getId(): 0) );            
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
    
    private void loadCmbRequestType(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<RequestType> requestTypeList;
        try {
            requestTypeList = utilsEJB.getRequestType(request1);
            loadGenericCombobox(requestTypeList,cmbRequestType, "description",evenInteger,Long.valueOf(requestParam != null? requestParam.getRequestTypeId().getId(): 0));
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