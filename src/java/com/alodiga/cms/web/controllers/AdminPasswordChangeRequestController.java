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
import org.zkoss.zul.Datebox;
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
    private Datebox dtbRequestDate;
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
    private User user;
    int attempts = 0;
    
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
//            case WebConstants.EVENT_EDIT:
//                tbbTitle.setLabel(Labels.getLabel("cms.crud.password.change.request.edit"));
//                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.password.change.request.view"));
                break;
            case WebConstants.EVENT_ADD:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.password.change.request.add"));
                break;
            default:
                break;
        }
        rApprovedYes.setVisible(false);
        rApprovedNo.setVisible(false);
        try {
            user = (User) session.getAttribute(Constants.USER_OBJ_SESSION);
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    public void onFocus$txtCurrentPassword() {
        this.clearMessage();
        txtCurrentPassword.setText("");
        txtNewPassword.setText("");
        txtRepeatNewPassword.setText("");
    }
    
    public void clearFields() {
        txtCurrentPassword.setRawValue(null);
        txtNewPassword.setRawValue(null);
        txtRepeatNewPassword.setRawValue(null);
        lblRequestNumber.setValue(null);
        dtbRequestDate.setValue(null);
        lblIdentificationNumber.setValue(null);
        lblUser.setValue(null);
        lblComercialAgency.setValue(null);
    } 
    
    private void loadFields(PasswordChangeRequest passwordChangeRequest) {

        try {
            lblRequestNumber.setValue(passwordChangeRequest.getRequestNumber().toString());
            dtbRequestDate.setValue(passwordChangeRequest.getRequestDate());
            lblIdentificationNumber.setValue(passwordChangeRequest.getUserId().getIdentificationNumber());
            lblUser.setValue(passwordChangeRequest.getUserId().getFirstNames());
            lblComercialAgency.setValue(passwordChangeRequest.getUserId().getComercialAgencyId().getName());
            txtCurrentPassword.setText(passwordChangeRequest.getCurrentPassword());
            txtNewPassword.setText(passwordChangeRequest.getNewPassword());
            txtRepeatNewPassword.setText(passwordChangeRequest.getNewPassword());
                  
              if (passwordChangeRequest.getCurrentPassword() != null ) {
                    txtCurrentPassword.setValue(passwordChangeRequest.getCurrentPassword());
              }
              
              if (passwordChangeRequest.getCurrentPassword() == null) {
                    txtCurrentPassword.setValue(passwordChangeRequest.getNewPassword());
              } 
                
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
        List<User> userList = null;
        PasswordChangeRequest passwordChangeRequest = null;
        
        try {
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
             lblRequestNumber.setValue(numberRequest);
                   
            //Valida si la contraseña actual es correcta
            params = new HashMap();
            params.put(Constants.CURRENT_PASSWORD, txtCurrentPassword.getValue());
            params.put(Constants.USER_KEY,user.getId());
            request1.setParams(params);
            userList = personEJB.validatePassword(request1);  
            
            if (userList.size() > 0) {
                //Se aprueba la solicitud automaticamente
                indApproved = true;                
                //Se crea el objeto passwordChangeRequest
                createPasswordChangeRequest(passwordChangeRequest, numberRequest, indApproved); 
                
                //Guardar la solicitud de cambio de contraseña en la BD
                passwordChangeRequest = personEJB.savePasswordChangeRequest(passwordChangeRequest);
                passwordChangeRequestParam = passwordChangeRequest;

                //Actualizar la contraseña del usuario en la BD
                user.setPassword(txtNewPassword.getText());
                user = personEJB.saveUser(user);
                
                rApprovedYes.setVisible(true);
                rApprovedNo.setVisible(true);                
                if (passwordChangeRequest.getIndApproved() == true) {
                    rApprovedYes.setChecked(true);
                } else {
                    rApprovedNo.setChecked(true);
                }                
                rApprovedYes.setDisabled(true);
                rApprovedNo.setDisabled(true);               
                
                this.showMessage("cms.msj.passwordChangedRequestApproved", false, null);
                btnSave.setVisible(false);
            }             
        } catch (WrongValueException ex) {
            showError(ex);
        } catch (EmptyListException ex) {
            showError(ex);
        } finally {
            if (userList == null) {
                attempts++;
                if (attempts != 3) {
                    this.showMessage("cms.msj.errorCurrentPasswordNotMatchInBD", false, null);
                }
            }
            if (attempts == 3) {
                //Se rechaza la solicitud automaticamente
                indApproved = false;
                //Se crea el objeto passwordChangeRequest
                createPasswordChangeRequest(passwordChangeRequest, numberRequest, indApproved); 

                //Guardar la solicitud de cambio de contraseña en la BD
                passwordChangeRequest = personEJB.savePasswordChangeRequest(passwordChangeRequest);
                passwordChangeRequestParam = passwordChangeRequest;                
                
                rApprovedYes.setVisible(true);
                rApprovedNo.setVisible(true);                
                if (passwordChangeRequest.getIndApproved() == true) {
                    rApprovedYes.setChecked(true);
                } else {
                    rApprovedNo.setChecked(true);
                }                
                rApprovedYes.setDisabled(true);
                rApprovedNo.setDisabled(true);

                this.showMessage("cms.msj.passwordChangedRequestRejected", false, null);
                btnSave.setVisible(false);
            }
                    
        }
    }  
    
    public void createPasswordChangeRequest(PasswordChangeRequest passwordChangeRequest, String numberRequest, boolean indApproved) {
        passwordChangeRequest.setRequestNumber(numberRequest);
        passwordChangeRequest.setRequestDate((dtbRequestDate.getValue()));
        passwordChangeRequest.setUserId(user);
        passwordChangeRequest.setCurrentPassword(txtCurrentPassword.getText());
        passwordChangeRequest.setNewPassword(txtNewPassword.getText());
        passwordChangeRequest.setNewPassword(txtRepeatNewPassword.getText());
        passwordChangeRequest.setIndApproved(indApproved);
        passwordChangeRequest.setCreateDate(new Timestamp(new Date().getTime()));
    }
      
    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException, EmptyListException {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    savePasswordChangeRequest(null);
                    break;
//                case WebConstants.EVENT_EDIT:
//                    savePasswordChangeRequest(passwordChangeRequestParam);
//                    break;
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
//            case WebConstants.EVENT_EDIT:
//                loadFields(passwordChangeRequestParam);
//                txtCurrentPassword.setReadonly(true);
//                txtNewPassword.setReadonly(true);
//                txtRepeatNewPassword.setReadonly(true);
//                break;
            case WebConstants.EVENT_VIEW:
                loadFields(passwordChangeRequestParam);
                txtCurrentPassword.setReadonly(true);
                txtNewPassword.setReadonly(true);
                txtRepeatNewPassword.setDisabled(false);
                dtbRequestDate.setReadonly(true);
                blockFields();
                rApprovedYes.setDisabled(true);
                rApprovedNo.setDisabled(true);
                break;
            case WebConstants.EVENT_ADD:
                lblComercialAgency.setValue(user.getComercialAgencyId().getName());
                lblUser.setValue(user.getFirstNames() + " " + user.getLastNames());
                lblIdentificationNumber.setValue(user.getIdentificationNumber());
                break;
            default:
                break;
        }

      }
     
    private Object getSelectedItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
  }
