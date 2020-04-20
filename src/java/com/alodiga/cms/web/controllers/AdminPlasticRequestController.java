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
import com.cms.commons.models.PlasticCustomizingRequest;
import com.cms.commons.models.PlasticManufacturer;
import com.cms.commons.models.Program;
import com.cms.commons.models.Sequences;
import com.cms.commons.models.StatusPlasticCustomizingRequest;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;

public class AdminPlasticRequestController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Label txtStatus = null;
    private Combobox cmbPlasticManufacturer;
    private Combobox cmbPrograms;
    private Combobox cmbStatusPlasticRequest;
    private Datebox dtbRequestDate;
    private PlasticCustomizingRequest plasticCustomizingRequestParam;
    private UtilsEJB utilsEJB = null;
    private RequestEJB requestEJB = null;
    private ProgramEJB programEJB = null;
    private PersonEJB personEJB = null;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;
    private StatusPlasticCustomizingRequest statusPending;
    public static PlasticCustomizingRequest plasticCustomer = null;

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
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            requestEJB = (RequestEJB) EJBServiceLocator.getInstance().get(EjbConstants.REQUEST_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            programEJB = (ProgramEJB) EJBServiceLocator.getInstance().get(EjbConstants.PROGRAM_EJB);
            if (eventType == WebConstants.EVENT_ADD) {
                EJBRequest request1 = new EJBRequest();
                request1.setParam(WebConstants.STATUS_PROGRAM_LOYALTY_PENDING);
                statusPending = requestEJB.loadStatusPlasticCustomizingRequest(request1);
                txtStatus.setValue(statusPending.getDescription());
            }
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public PlasticCustomizingRequest getPlasticCustomizingRequest() {
        return this.plasticCustomer;
    }

    public void clearFields() {
        dtbRequestDate.setRawValue(null);
    }

    private void loadFields(PlasticCustomizingRequest plasticCustomizingRequest) {
        try {
            txtStatus.setValue(plasticCustomizingRequest.getStatusPlasticCustomizingRequestId().getDescription());
            dtbRequestDate.setValue(plasticCustomizingRequest.getRequestDate());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        dtbRequestDate.setReadonly(true);
        cmbPlasticManufacturer.setReadonly(true);
        cmbPrograms.setReadonly(true);
        btnSave.setVisible(false);
    }

    private void savePlasticCustomizingRequest(PlasticCustomizingRequest _plasticCustomizingRequest) {
        String numberRequest = "";
        try {
            PlasticCustomizingRequest plasticCustomizingRequest = null;

            if (_plasticCustomizingRequest != null) {
                plasticCustomizingRequest = _plasticCustomizingRequest;
                numberRequest = plasticCustomizingRequest.getRequestNumber();
            } else {//New collectionsRequest
                plasticCustomizingRequest = new PlasticCustomizingRequest();

                //Obtiene el numero de secuencia para documento Request
                EJBRequest request1 = new EJBRequest();
                Map params = new HashMap();
                params.put(Constants.DOCUMENT_TYPE_KEY, Constants.DOCUMENT_TYPE_PLASTIC_REQUEST);
                request1.setParams(params);
                List<Sequences> sequence = utilsEJB.getSequencesByDocumentType(request1);
                numberRequest = utilsEJB.generateNumberSequence(sequence, Constants.ORIGIN_APPLICATION_CMS_ID);
            }

            plasticCustomizingRequest.setRequestNumber(numberRequest);
            plasticCustomizingRequest.setRequestDate((dtbRequestDate.getValue()));
            plasticCustomizingRequest.setPlasticManufacturerId((PlasticManufacturer) cmbPlasticManufacturer.getSelectedItem().getValue());
            if (eventType == WebConstants.EVENT_ADD) {
                plasticCustomizingRequest.setStatusPlasticCustomizingRequestId(statusPending);
            } else {
                plasticCustomizingRequest.setStatusPlasticCustomizingRequestId((StatusPlasticCustomizingRequest) cmbStatusPlasticRequest.getSelectedItem().getValue());
            }
            plasticCustomizingRequest.setProgramId((Program) cmbPrograms.getSelectedItem().getValue());
            if (eventType == WebConstants.EVENT_ADD) {
                plasticCustomizingRequest.setCreateDate(new Timestamp(new Date().getTime()));
            } else {
                plasticCustomizingRequest.setUpdateDate(new Timestamp(new Date().getTime()));
            }
            plasticCustomizingRequest = requestEJB.savePlasticCustomizingRequest(plasticCustomizingRequest);
            this.showMessage("sp.common.save.success", false, null);
            
            plasticCustomer = plasticCustomizingRequest;
            btnSave.setVisible(false);
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
                txtStatus.setVisible(false);
                plasticCustomer = plasticCustomizingRequestParam;
                loadFields(plasticCustomizingRequestParam);
                loadCmbPrograms(eventType);
                loadCmbPersonType(eventType);
                loadCmbStatusPlasticRequest(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                txtStatus.setVisible(false);
                plasticCustomer = plasticCustomizingRequestParam;
                loadFields(plasticCustomizingRequestParam);
                loadCmbPrograms(eventType);
                loadCmbPersonType(eventType);
                loadCmbStatusPlasticRequest(eventType);
                break;
            case WebConstants.EVENT_ADD:
                txtStatus.setVisible(true);
                cmbStatusPlasticRequest.setVisible(false);
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

    private void loadCmbStatusPlasticRequest(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<StatusPlasticCustomizingRequest> statusPlastic;
        try {
            statusPlastic = (List<StatusPlasticCustomizingRequest>) requestEJB.getStatusPlasticCustomizingRequest(request1);
            cmbStatusPlasticRequest.getItems().clear();
            for (StatusPlasticCustomizingRequest c : statusPlastic) {
                Comboitem item = new Comboitem();
                item.setValue(c);
                item.setLabel(c.getDescription());
                item.setDescription(c.getDescription());
                item.setParent(cmbStatusPlasticRequest);
                if (plasticCustomizingRequestParam != null && c.getId().equals(plasticCustomizingRequestParam.getStatusPlasticCustomizingRequestId().getId())) {
                    cmbStatusPlasticRequest.setSelectedItem(item);
                }
            }
            if (eventType.equals(WebConstants.EVENT_ADD)) {
                cmbStatusPlasticRequest.setSelectedIndex(0);
            }
            if (evenInteger.equals(WebConstants.EVENT_VIEW)) {
                cmbStatusPlasticRequest.setDisabled(true);
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
