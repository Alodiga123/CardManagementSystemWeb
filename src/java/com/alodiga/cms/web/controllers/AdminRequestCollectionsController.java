package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.CollectionsRequest;
import com.cms.commons.models.Country;
import com.cms.commons.models.PersonType;
import com.cms.commons.models.Request;
import com.cms.commons.models.RequestHasCollectionsRequest;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import com.cms.commons.util.QueryConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

public class AdminRequestCollectionsController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Request requestParam;
    private CollectionsRequest collectionsRequestParam;
    private RequestHasCollectionsRequest requestHasCollectionsRequestParam;
    private List<RequestHasCollectionsRequest> requestHasCollectionsRequestList;
    private UtilsEJB utilsEJB = null;
    private RequestEJB requestEJB = null;
    private Radio rApprovedYes;
    private Radio rApprovedNo;
    private Label lblInfo;
    private Label txtNumber;
    private Label txtPrograms;
    private Label txtProductType;
    private Label txtCollectionType;
    private Textbox txtObservations;
    private Combobox cmbPersonType;
    private Combobox cmbCountry;
    private Button btnSave;
    private Button btnUpload;
    private Image image;
    public Window winAdminRequestCollections;
    private Vbox divPreview;
    String UrlFile = "";
    String format = "";
    Request RequestNumber = null;
    private boolean uploaded = false;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        collectionsRequestParam = (Sessions.getCurrent().getAttribute("object") != null) ? (CollectionsRequest) Sessions.getCurrent().getAttribute("object") : null;
        AdminRequestController adminRequestController = new AdminRequestController();
        if (adminRequestController.getRequest().getId() != null) {
            requestParam = adminRequestController.getRequest();
        }
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(QueryConstants.PARAM_REQUEST_ID, requestParam.getId());
            params.put(QueryConstants.PARAM_COLLECTION_REQUEST_ID, collectionsRequestParam.getId());
            request1.setParams(params);
            requestHasCollectionsRequestList = requestEJB.getRequestsHasCollectionsRequestByRequestByCollectionRequest(request1);
            for (RequestHasCollectionsRequest r : requestHasCollectionsRequestList) {
                requestHasCollectionsRequestParam = r;
            }
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onChange$cmbCountry() {
        cmbPersonType.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbPersonType(4, country.getId());
    }

    public void clearFields() {
    }

    private void loadField(Request request) {
        try {
            txtNumber.setValue(request.getRequestNumber());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void loadFieldC(CollectionsRequest collectionsRequest) {
        txtPrograms.setValue(collectionsRequest.getProgramId().getName());
        txtProductType.setValue(collectionsRequest.getProductTypeId().getName());
        txtCollectionType.setValue(collectionsRequest.getCollectionTypeId().getDescription());
    }

    private void loadFields(RequestHasCollectionsRequest requestHasCollectionsRequest) {
        if (requestHasCollectionsRequest != null) {
            try {
                if (requestHasCollectionsRequest.getIndApproved() == 1) {
                    rApprovedYes.setChecked(true);
                } else {
                    rApprovedNo.setChecked(true);
                }
                txtObservations.setValue(requestHasCollectionsRequest.getObservations());

                AImage image;
                image = new org.zkoss.image.AImage(requestHasCollectionsRequest.getUrlImageFile());
                org.zkoss.zul.Image imageFile = new org.zkoss.zul.Image();
                imageFile.setContent(image);
                imageFile.setParent(divPreview);
            } catch (Exception ex) {
                showError(ex);
            }
        }
    }

    public Boolean validateEmpty() {
        if (txtObservations.getText().isEmpty()) {
            txtObservations.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }

    public void onUpload$btnUpload(org.zkoss.zk.ui.event.UploadEvent event) throws Throwable {
        org.zkoss.util.media.Media media = event.getMedia();
        if (media != null) {
            //if(media.getFormat()==""){   
            divPreview.getChildren().clear();
            media = event.getMedia();

            //File file = new File("/opt/proyecto/cms/imagenes/"+RequestNumber.getRequestNumber()+"/"+media.getName());
            File file = new File("/opt/proyecto/cms/imagenes/" + media.getName());
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(media.getByteData());
            fos.flush();
            fos.close();
            UrlFile = file.getAbsolutePath();
            format = media.getFormat();
            org.zkoss.zul.Image image = new org.zkoss.zul.Image();
            image.setContent((org.zkoss.image.Image) media);
            image.setWidth("250px");
            image.setParent(divPreview);
            uploaded = true;
            //}
        } else {
            lblInfo.setValue("Error");
        }
    }

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

            //Guarda la solicitud en requestHasCollectionsRequest
            requestHasCollectionsRequest.setCollectionsRequestid(collectionsRequestParam);
            requestHasCollectionsRequest.setRequestId(RequestId);
            requestHasCollectionsRequest.setIndApproved(indApproved);
            requestHasCollectionsRequest.setObservations(txtObservations.getText());
            requestHasCollectionsRequest.setUrlImageFile(UrlFile);
            requestHasCollectionsRequest = requestEJB.saveRequestHasCollectionsRequest(requestHasCollectionsRequest);
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
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
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(requestHasCollectionsRequestParam);
                loadField(requestParam);
                loadFieldC(collectionsRequestParam);
                loadCmbCountry(4);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(requestHasCollectionsRequestParam);
                loadField(requestParam);
                loadFieldC(collectionsRequestParam);
                blockFields();
                loadCmbCountry(eventType);
                onChange$cmbCountry();
                break;
            case WebConstants.EVENT_ADD:
                loadField(requestParam);
                loadFieldC(collectionsRequestParam);
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

}
