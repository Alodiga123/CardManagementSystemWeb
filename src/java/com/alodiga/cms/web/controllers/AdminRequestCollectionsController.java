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
import com.cms.commons.models.CollectionsRequest;
import com.cms.commons.models.Country;
import com.cms.commons.models.PersonType;
import com.cms.commons.models.ProductType;
import com.cms.commons.models.Program;
import com.cms.commons.models.Request;
import com.cms.commons.models.RequestHasCollectionsRequest;
import com.cms.commons.models.RequestType;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.io.Files;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Window;

public class AdminRequestCollectionsController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Request requestParam;
    private CollectionsRequest CollectionsRequestParam;
    private RequestHasCollectionsRequest requestHasCollectionsRequestParam;
    private UtilsEJB utilsEJB = null;
    private ProgramEJB programEJB = null;
    private RequestEJB requestEJB = null;
    private Radio rApprovedYes;
    private Radio rApprovedNo;
    private Label lblInfo;
    private Textbox txtNumber;
    private Textbox txtObservations;
    private Textbox txtUrlImageFile;
    private Combobox cmbCountry;
    private Combobox cmbPrograms;
    private Combobox cmbPersonType;
    private Combobox cmbProductType;
    private Combobox cmbRequestType;
    private Combobox cmbCollectionType;
    private Button btnSave;
    private Button btnUpload;
    private Image image;
    public Window winAdminRequestCollections;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        CollectionsRequestParam = (Sessions.getCurrent().getAttribute("object") != null) ? (CollectionsRequest) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onChange$cmbCountry() {
        cmbPersonType.setVisible(true);
        cmbCollectionType.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbPersonType(4, country.getId());
        loadCmbCollectionType(4, country.getId());
    }

    public void clearFields() {
    }

    private void loadField(Request request) {
        Request RequestNumber = null;
        AdminRequestController adminRequestController = new AdminRequestController();
        if (adminRequestController.getRequest().getId() != null) {
            RequestNumber = adminRequestController.getRequest();
        }
        
        try {
            txtNumber.setText(RequestNumber.getRequestNumber());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadFields(RequestHasCollectionsRequest requestHasCollectionsRequest) {
        try {
            if (requestHasCollectionsRequest.getIndApproved() == 1) {
                rApprovedYes.setChecked(true);
            } else {
                rApprovedNo.setChecked(true);
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    /*public void onUpload$btnUploads(org.zkoss.zk.ui.event.UploadEvent event) throws Throwable {
     org.zkoss.util.media.Media media = event.getMedia();
     if (media != null) {
     if (validateFormatFile(media)) {
     File csv = new File("/tmp/" + media.getName());
     File csvTemp = csv;
     csvTemp.delete();
     btnUpload.setDisabled(true);
     if (media.isBinary()) {
     Files.copy(csv, media.getStreamData());
     } else {
     BufferedWriter writer = new BufferedWriter(new FileWriter(csv));
     Files.copy(writer, media.getReaderData());
     }
     }
     } else {
     lblInfo.setValue("Error");
     }
     }

     public void onUpload$btnUploa(Media[] media) {
     if (media != null) {
     for (int i = 0; i < media.length; i++) {
     if (media[i] instanceof org.zkoss.image.Image) {
     image.setContent((org.zkoss.image.Image) media[i]);
     } else {
     //Messagebox.show("Not an image: " + media[i], "Error", Messagebox.OK, Messagebox.ERROR);
     break; //not to show too many errors
     }
     }
     }
     }
    
     private boolean validateFormatFile(org.zkoss.util.media.Media media) throws InterruptedException {
     if (!(media.getName().equalsIgnoreCase("pricelist_open_range_alodigaor.csv"))) {
     Messagebox.show(Labels.getLabel("sp.error.fileupload.invalid.file"), "Advertencia", 0, Messagebox.EXCLAMATION);
     return false;
     }

     return true;
     }*/
    public void blockFields() {
        btnSave.setVisible(false);
    }

    public void onClick$btnBack() {
        winAdminRequestCollections.detach();
    }

    private void saveRequest(RequestHasCollectionsRequest _requestHasCollectionsRequest) {
        Request RequestId = null;
        short indApproved = 0;
        try {
            RequestHasCollectionsRequest requestHasCollectionsRequest = null;

            if (_requestHasCollectionsRequest != null) {
                requestHasCollectionsRequest = _requestHasCollectionsRequest;
            } else {//New Request
                requestHasCollectionsRequest = new RequestHasCollectionsRequest();
            }

            if (rApprovedYes.isChecked()) {
                indApproved = 1;
            } else {
                indApproved = 0;
            }

            //Se obtiene la persona asociada al solicitante de tarjeta
            AdminRequestController adminRequestController = new AdminRequestController();
            if (adminRequestController.getRequest().getId() != null) {
                RequestId = adminRequestController.getRequest();
            }

            //Guarda la solicitud en la BD
            //numberRequest
            requestHasCollectionsRequest.setRequestId(RequestId);
            requestHasCollectionsRequest.setIndApproved(indApproved);
            requestHasCollectionsRequest.setObservations(txtObservations.getText());
            requestHasCollectionsRequest.setUrlImageFile(txtUrlImageFile.getText());
            requestHasCollectionsRequest = requestEJB.saveRequestHasCollectionsRequest(requestHasCollectionsRequest);
            this.showMessage("sp.common.save.success", false, null);
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
                saveRequest(requestHasCollectionsRequestParam);
                break;
            default:
                break;
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(requestHasCollectionsRequestParam);
                loadField(requestParam);
                txtNumber.setReadonly(true);
                loadCmbCountry(4);
                loadCmbProductType(4);
                loadCmbProgram(4);
                loadCmbRequestType(4);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(requestHasCollectionsRequestParam);
                loadField(requestParam);
                blockFields();
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
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(CollectionsRequestParam != null ? CollectionsRequestParam.getCountryId().getId() : 0));
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
            loadGenericCombobox(productTypes, cmbProductType, "name", evenInteger, Long.valueOf(CollectionsRequestParam != null ? CollectionsRequestParam.getProductTypeId().getId() : 0));
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
            loadGenericCombobox(programs, cmbPrograms, "name", evenInteger, Long.valueOf(CollectionsRequestParam != null ? CollectionsRequestParam.getProgramId().getId() : 0));
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
            loadGenericCombobox(personTypes, cmbPersonType, "description", evenInteger, Long.valueOf(CollectionsRequestParam != null ? CollectionsRequestParam.getPersonTypeId().getId() : 0));
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

    private void loadCmbCollectionType(Integer evenInteger, int countryId) {
        //cmbDocumentsPersonType
        EJBRequest request = new EJBRequest();
        cmbCollectionType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        request.setParams(params);
        List<CollectionType> collectionTypes;
        try {
            collectionTypes = requestEJB.getCollectionTypeByCountry(request);
            loadGenericCombobox(collectionTypes, cmbCollectionType, "description", evenInteger, Long.valueOf(CollectionsRequestParam != null ? CollectionsRequestParam.getCollectionTypeId().getId() : 0));
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
