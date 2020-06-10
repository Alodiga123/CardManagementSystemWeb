package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.ComercialAgency;
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.Employee;
import com.cms.commons.models.PasswordChangeRequest;
import com.cms.commons.models.Person;
import com.cms.commons.models.PersonClassification;
import com.cms.commons.models.PhonePerson;
import com.cms.commons.models.Sequences;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.QueryConstants;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Toolbarbutton;

public class AdminPasswordChangeRequestController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtCurrentPassword;
    private Textbox txtNewPassword;
    private Textbox txtRepeatNewPassword;
    private Label lblRequestNumber;
    private Label lblRequestDate;
    private Label lblIdentificationNumber;
    private Label lblUser;
    private Label lblComercialAgency;
    private Radio rApprovedYes;
    private Radio rApprovedNo;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private PasswordChangeRequest passwordChangeRequestParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        passwordChangeRequestParam = (Sessions.getCurrent().getAttribute("object") != null) ? (PasswordChangeRequest) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
           passwordChangeRequestParam = null;                    
       } else {
           passwordChangeRequestParam = (PasswordChangeRequest) Sessions.getCurrent().getAttribute("object");            
       }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.password.change.request.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.password.change.request.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.password.change.request.add"));
                break;
            default:
                break;
        }
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    
    public void clearFields() {
        txtCurrentPassword.setRawValue(null);
        txtNewPassword.setRawValue(null);
        txtRepeatNewPassword.setRawValue(null);
        lblRequestNumber.setValue(null);
        lblRequestDate.setValue(null);
        lblIdentificationNumber.setValue(null);
        lblUser.setValue(null);
        lblComercialAgency.setValue(null);
    } 
    
        
    private void loadFields(PasswordChangeRequest passwordChangeRequest) {

        try {
            txtCurrentPassword.setText(passwordChangeRequest.getCurrentPassword());
            txtNewPassword.setText(passwordChangeRequest.getNewPassword());
            txtRepeatNewPassword.setText(passwordChangeRequest.getNewPassword());
            lblRequestNumber.setValue(passwordChangeRequest.getRequestNumber().toString());
            lblRequestDate.setValue(passwordChangeRequest.getRequestDate().toString());
            lblIdentificationNumber.setValue(passwordChangeRequest.getUserid().getIdentificationNumber());
            lblUser.setValue(passwordChangeRequest.getUserid().getEmployeeId().getFirstNames());
//            + " " + passwordChangeRequest.getUserid().getEmployeeId().getLastNames());
//            + " " + passwordChangeRequest.getUserid().getLastNames());
            lblComercialAgency.setValue(passwordChangeRequest.getUserid().getComercialAgencyId().getName());
              if (passwordChangeRequest.getCurrentPassword() == null) {
                    txtCurrentPassword.setValue(passwordChangeRequest.getNewPassword());
                }
//              if (passwordChangeRequest.getCurrentPassword() != null) {
//                    txtCurrentPassword.setValue(passwordChangeRequest.getNewPassword());
//                }
//            if (user.getEmployeeId() != null) {
//                EJBRequest request = new EJBRequest(); 
//                HashMap params = new HashMap();
//                params.put(Constants.PERSON_KEY, user.getEmployeeId().getPersonId().getId());
//                request.setParams(params);
//                phonePersonUserList = personEJB.getPhoneByPerson(request);
//                for (PhonePerson phoneUser : phonePersonUserList) {
//                    phonePersonUser = phoneUser;
//                }
//                lblUserExtAlodiga.setValue(phonePersonUser.getExtensionPhoneNumber());
//            }
//            if (user.getAuthorizedEmployeeId() != null) {
//                EJBRequest request = new EJBRequest(); 
//                HashMap params = new HashMap();
//                params.put(Constants.PERSON_KEY, user.getAuthorizedEmployeeId().getPersonId().getId());
//                request.setParams(params);
//                phonePersonEmployeeAuthorizeList = personEJB.getPhoneByPerson(request);
//                for (PhonePerson phoneEmployeeAuthorize : phonePersonEmployeeAuthorizeList) {
//                    phonePersonEmployeeAuthorize = phoneEmployeeAuthorize;
//                }
//                lblAuthorizeExtAlodiga.setValue(phonePersonEmployeeAuthorize.getExtensionPhoneNumber());
//            }
            if (passwordChangeRequest.getIndApproved() == true) {
                rApprovedYes.setChecked(true);
            } else {
                rApprovedNo.setChecked(true);
            }
            btnSave.setVisible(true);
        
        } catch (Exception ex) {
            showError(ex);
        }
    }     

    public void blockFields() {
        txtCurrentPassword.setReadonly(true);
        txtNewPassword.setReadonly(true);
        txtRepeatNewPassword.setReadonly(true);
        btnSave.setVisible(false);
    }
    
    public Boolean validateEmpty() {
        if (txtCurrentPassword.getText().isEmpty()) {
            txtCurrentPassword.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtNewPassword.getText().isEmpty()) {
            txtNewPassword.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtRepeatNewPassword.getText().isEmpty()) {
            txtRepeatNewPassword.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }    
    
    private void savePasswordChangeRequest(PasswordChangeRequest _passwordChangeRequest) throws RegisterNotFoundException, NullParameterException, GeneralException, EmptyListException {
        boolean indApproved = true;
        EJBRequest request1 = new EJBRequest();
        String numberRequest = "";
        Date dateRequest = null;
        try {
            PasswordChangeRequest passwordChangeRequest = null;

            if (_passwordChangeRequest != null) {
                passwordChangeRequest = _passwordChangeRequest;
            } else {
                passwordChangeRequest = new PasswordChangeRequest();
            }

            if (rApprovedYes.isChecked()) {
                indApproved = true;
            } else {
                indApproved = false;
            }
            
             //Obtiene el numero de secuencia para Solicitud de Cambio de Contraseña
             Map params = new HashMap();
             params.put(Constants.DOCUMENT_TYPE_KEY, Constants.DOCUMENT_TYPE_RENEWAL_REQUEST);
             request1.setParams(params);
             List<Sequences> sequence = utilsEJB.getSequencesByDocumentType(request1);
             numberRequest = utilsEJB.generateNumberSequence(sequence, Constants.ORIGIN_APPLICATION_CMS_ID);
             dateRequest = new Date();
             
        
//            //Obtener la clasificacion del Empleado / Usuario
//            request1 = new EJBRequest();
//            request1.setParam(Constants.CLASSIFICATION_PERSON_USER);
//            PersonClassification personClassification = utilsEJB.loadPersonClassification(request1);

            //Obtener y guardar datos del Usuario 
//            User user = new User();
//            user.setIdentificationNumber(lblIdentificationNumber.getValue());
//            user.setFirstNames(lblUser.getValue());
//            user.setComercialAgencyId(lblComercialAgency.getValue().toString());        
////        (lblComercialAgency.getValue());
//            person.setPersonClassificationId(personClassification);
//            person = personEJB.savePerson(person);
            
            //Guarda el Cambio de Contraseña
            passwordChangeRequest.setRequestNumber(lblRequestNumber.getValue());
            passwordChangeRequest.setIndApproved(indApproved);
            passwordChangeRequest.setCurrentPassword(txtCurrentPassword.getText());
            passwordChangeRequest.setNewPassword(txtNewPassword.getText());
            passwordChangeRequest.setNewPassword(txtRepeatNewPassword.getText()); 
            passwordChangeRequest.setIndApproved(indApproved);
            
            if (eventType == WebConstants.EVENT_ADD) {
                passwordChangeRequest.setCreateDate(new Timestamp(new Date().getTime()));
            } else {
                passwordChangeRequest.setUpdateDate(new Timestamp(new Date().getTime()));
            }
      
            passwordChangeRequest = personEJB.savePasswordChangeRequest(passwordChangeRequest);
            passwordChangeRequestParam = passwordChangeRequest;
            this.showMessage("sp.common.save.success", false, null);
            btnSave.setVisible(false);
        } catch (WrongValueException ex) {
            showError(ex);
        }
    }  
    
    
    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException, EmptyListException {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    savePasswordChangeRequest(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    savePasswordChangeRequest(passwordChangeRequestParam);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void onclick$btnBack() {
        Executions.getCurrent().sendRedirect("listPasswordChangeRequest.zul");
    }
    
    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(passwordChangeRequestParam);
                txtCurrentPassword.setReadonly(true);
                txtNewPassword.setReadonly(true);
                txtRepeatNewPassword.setReadonly(true);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(passwordChangeRequestParam);
                txtCurrentPassword.setReadonly(true);
                txtNewPassword.setReadonly(true);
                txtRepeatNewPassword.setDisabled(true);
                blockFields();
                rApprovedYes.setDisabled(true);
                rApprovedNo.setDisabled(true);
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }    


    private Object getSelectedItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
  }