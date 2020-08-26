package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import static com.alodiga.cms.web.controllers.AdminRequestController.eventType;
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.image.AImage;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

public class AdminRequestCollectionsController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label lblRequestNumber;
    private Label lblRequestDate;
    private Label lblStatusRequest;
    private Request requestParam = null;
    private CollectionsRequest collectionsRequestParam;
    private RequestHasCollectionsRequest requestHasCollectionsRequestParam;
    private List<RequestHasCollectionsRequest> requestHasCollectionsRequestList;
    private UtilsEJB utilsEJB = null;
    private RequestEJB requestEJB = null;
    private Radio rApprovedYes;
    private Radio rApprovedNo;
    private Label lblInfo;
    private Label txtPrograms;
    private Label txtProductType;
    private Combobox cmbCollectionType;
    private Textbox txtObservations;
    private Label lblPersonType;
    private Label lblCountry;
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
        AdminRequestController adminRequestController = new AdminRequestController();
        if (adminRequestController.getRequest().getId() != null) {
            requestParam = adminRequestController.getRequest();
        }
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                requestHasCollectionsRequestParam = (RequestHasCollectionsRequest) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_VIEW:
                requestHasCollectionsRequestParam = (RequestHasCollectionsRequest) Sessions.getCurrent().getAttribute("object");
                break;
            case WebConstants.EVENT_ADD:
                requestHasCollectionsRequestParam = null;
                break;
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
        requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
        loadData();
    }

    public void clearFields() {
    }

    private void loadField(Request requestData) {
        try {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            if (requestData.getRequestNumber() != null) {
                lblRequestNumber.setValue(requestData.getRequestNumber());
                lblRequestDate.setValue(simpleDateFormat.format(requestData.getRequestDate()));
                lblStatusRequest.setValue(requestData.getStatusRequestId().getDescription());
                lblCountry.setValue(requestData.getCountryId().getName());
                lblPersonType.setValue(requestData.getPersonTypeId().getDescription());
                txtPrograms.setValue(requestData.getProgramId().getName());
                txtProductType.setValue(requestData.getProductTypeId().getName());
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
 
//    private void loadFieldC(RequestHasCollectionsRequest requestHasCollectionsRequest) {
//      txtPrograms.setValue(requestCard.getProgramId().getName());
//      txtProductType.setValue(collectionsRequest.getProductTypeId().getName());
//    }

    private void loadFields(RequestHasCollectionsRequest requestHasCollectionsRequest) {
        if (requestHasCollectionsRequest != null) {
            try {
                if (requestHasCollectionsRequest.getIndApproved() == 1) {
                    rApprovedYes.setChecked(true);
                } else {
                    rApprovedNo.setChecked(true);
                }
                txtObservations.setValue(requestHasCollectionsRequest.getObservations());
                UrlFile = requestHasCollectionsRequest.getUrlImageFile();

                AImage image;
                image = new org.zkoss.image.AImage(requestHasCollectionsRequest.getUrlImageFile());
                org.zkoss.zul.Image imageFile = new org.zkoss.zul.Image();
                imageFile.setWidth("250px");
                imageFile.setContent(image);
                imageFile.setParent(divPreview);
            } catch (Exception ex) {
                showError(ex);
            }
        }
    }

    public Boolean validateEmpty() {
        if ((!rApprovedYes.isChecked()) && (!rApprovedNo.isChecked())) {
            this.showMessage("cms.error.radio.approved", true, null);
        } else if (txtObservations.getText().isEmpty()) {
            txtObservations.setFocus(true);
            this.showMessage("cms.error.renewal.observations", true, null);
        } else if (UrlFile.isEmpty()) {
            this.showMessage("cms.error.urlFile", true, null);
        } else {
            return true;
        }
        return false;
    }

    public void onUpload$btnUpload(org.zkoss.zk.ui.event.UploadEvent event) throws Throwable {
        org.zkoss.util.media.Media media = event.getMedia();
        if (media != null) {
            divPreview.getChildren().clear();
            media = event.getMedia();
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
        } else {
            lblInfo.setValue("Error");
        }
    }

    public void blockFields() {
        btnSave.setVisible(false);
        btnUpload.setDisabled(true);
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
            } else {
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
            if (eventType == WebConstants.EVENT_ADD) {
                requestHasCollectionsRequest.setCreateDate(new Timestamp(new Date().getTime()));
            } else {
                requestHasCollectionsRequest.setUpdateDate(new Timestamp(new Date().getTime()));
            }
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
                loadCmbCollectionType(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(requestHasCollectionsRequestParam);
                loadField(requestParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCollectionType(eventType);
                loadField(requestParam);
                break;
            default:
                break;
        }
    }
    
    private void loadCmbCollectionType(Integer evenInteger) {
        Request requestCard = null;
        String descriptionType = "";
        
        AdminRequestController adminRequestController = new AdminRequestController();
        if (adminRequestController.getRequest().getId() != null) {
            requestCard = adminRequestController.getRequest();
        }
        
        List<CollectionsRequest> collectionsRequest;
        try {
            EJBRequest request1 = new EJBRequest();
            Map params = new HashMap();
            params.put(Constants.COUNTRY_KEY, requestCard.getCountryId().getId());
            params.put(Constants.PRODUCT_TYPE_KEY, requestCard.getProductTypeId().getId());
            params.put(Constants.PROGRAM_KEY, requestCard.getProgramId().getId());
            params.put(Constants.PERSON_TYPE_KEY, requestCard.getPersonTypeId().getId());
            request1.setParams(params);
            collectionsRequest = requestEJB.getCollectionsByRequest(request1);
            
            for (int i = 0; i < collectionsRequest.size(); i++) {
                Comboitem item = new Comboitem();
                item.setValue(collectionsRequest.get(i));
                descriptionType = collectionsRequest.get(i).getCollectionTypeId().getDescription();
                item.setLabel(descriptionType);
                item.setParent(cmbCollectionType);
                if (eventType != 1) {
                    if (collectionsRequest.get(i).getId().equals(requestHasCollectionsRequestParam.getCollectionsRequestid().getId())) {
                        cmbCollectionType.setSelectedItem(item);
                    }
                }
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
}
