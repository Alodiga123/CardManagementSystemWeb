package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.ProgramEJB;
import com.alodiga.cms.commons.ejb.RequestEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.PlasticCustomizingRequest;
import com.cms.commons.models.PlasticManufacturer;
import com.cms.commons.models.Program;
import com.cms.commons.models.StatusPlasticCustomizingRequest;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class AdminPlasticRequestController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtRequestNumber;
    private Combobox cmbPlasticManufacturer;
    private Combobox cmbPrograms;
    private Datebox dtbRequestDate;
    private PlasticCustomizingRequest plasticCustomizingRequestParam;
    private RequestEJB requestEJB = null;
    private ProgramEJB programEJB = null;
    private PersonEJB personEJB = null;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            plasticCustomizingRequestParam = null;
        } else {
            plasticCustomizingRequestParam = (PlasticCustomizingRequest) Sessions.getCurrent().getAttribute("object");
        }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("cms.common.plasticRequest.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("cms.common.plasticRequest.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("cms.common.plasticRequest.add"));
                break;
            default:
                break;
        }
        try {
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtRequestNumber.setRawValue(null);
        dtbRequestDate.setRawValue(null);
    }

    private void loadFields(PlasticCustomizingRequest plasticCustomizingRequest) {
        try {
            txtRequestNumber.setText(plasticCustomizingRequest.getRequestNumber());
            dtbRequestDate.setValue(plasticCustomizingRequest.getRequestDate());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtRequestNumber.setReadonly(true);
        dtbRequestDate.setReadonly(true);
        cmbPlasticManufacturer.setReadonly(true);
        cmbPrograms.setReadonly(true);
        btnSave.setVisible(false);
    }

    private void savePlasticCustomizingRequest(PlasticCustomizingRequest _plasticCustomizingRequest) {
        try {
            PlasticCustomizingRequest plasticCustomizingRequest = null;

            if (_plasticCustomizingRequest != null) {
                plasticCustomizingRequest = _plasticCustomizingRequest;
            } else {//New collectionsRequest
                plasticCustomizingRequest = new PlasticCustomizingRequest();
            }

            //colocar estatus de solicitud "EN PROCESO"
            EJBRequest request1 = new EJBRequest();
            request1 = new EJBRequest();
            request1.setParam(Constants.STATUS_PLASTIC_REQUEST);
            StatusPlasticCustomizingRequest StatusPlastic = requestEJB.loadStatusPlasticCustomizingRequest(request1);

            plasticCustomizingRequest.setRequestNumber((txtRequestNumber.getText()));
            plasticCustomizingRequest.setRequestDate((dtbRequestDate.getValue()));
            plasticCustomizingRequest.setPlasticManufacturerId((PlasticManufacturer) cmbPlasticManufacturer.getSelectedItem().getValue());
            plasticCustomizingRequest.setStatusPlasticCustomizingRequestId(StatusPlastic);
            plasticCustomizingRequest.setProgramId((Program) cmbPrograms.getSelectedItem().getValue());
            if (eventType == WebConstants.EVENT_ADD) {
                plasticCustomizingRequest.setCreateDate(new Timestamp(new Date().getTime()));
            } else {
                plasticCustomizingRequest.setUpdateDate(new Timestamp(new Date().getTime()));
            }
            plasticCustomizingRequest = requestEJB.savePlasticCustomizingRequest(plasticCustomizingRequest);
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }

    }

    public void onClick$btnSave() {
        switch (eventType) {
            case WebConstants.EVENT_ADD:
                savePlasticCustomizingRequest(null);
                break;
            case WebConstants.EVENT_EDIT:
                savePlasticCustomizingRequest(plasticCustomizingRequestParam);
                break;
            default:
                break;

        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(plasticCustomizingRequestParam);
                loadCmbPrograms(eventType);
                loadCmbPersonType(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(plasticCustomizingRequestParam);
                loadCmbPrograms(eventType);
                loadCmbPersonType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbPrograms(eventType);
                loadCmbPersonType(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbPrograms(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Program> programs;

        try {
            programs = programEJB.getProgram(request1);
            loadGenericCombobox(programs, cmbPrograms, "name", evenInteger, Long.valueOf(plasticCustomizingRequestParam != null ? plasticCustomizingRequestParam.getProgramId().getId() : 0));
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
        List<PlasticManufacturer> plasticManufacturer;

        try {
            plasticManufacturer = personEJB.getPlasticManufacturer(request1);
            loadGenericCombobox(plasticManufacturer, cmbPlasticManufacturer, "name", evenInteger, Long.valueOf(plasticCustomizingRequestParam != null ? plasticCustomizingRequestParam.getPlasticManufacturerId().getId() : 0));
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
