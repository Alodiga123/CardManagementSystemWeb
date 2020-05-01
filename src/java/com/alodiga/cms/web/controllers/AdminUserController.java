package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.commons.exception.RegisterNotFoundException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Issuer;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.ComercialAgency;
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.Employee;
import com.cms.commons.models.IssuerType;
import com.cms.commons.models.Person;
import com.cms.commons.models.PersonClassification;
import com.cms.commons.models.PersonType;
import com.cms.commons.models.PhonePerson;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.QueryConstants;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static org.apache.commons.codec.digest.DigestUtils.md5;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Toolbarbutton;

public class AdminUserController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentificationNumber;
    private Textbox txtFirstName;
    private Textbox txtLastName;
    private Textbox txtUserEmail;
    private Textbox txtLogin;
    private Textbox txtPassword;
    private Label lblPosition;
    private Label lblUserExtAlodiga;
    private Label lblAuthorizeExtAlodiga;
    private Combobox cmbCountry;
    private Combobox cmbDocumentsPersonType;
    private Combobox cmbEmployee;
    private Combobox cmbComercialAgency;
    private Combobox cmbAuthorizeEmployee;
    private Radio rEnabledYes;
    private Radio rEnabledNo;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private User userParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        userParam = (Sessions.getCurrent().getAttribute("object") != null) ? (User) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
           userParam = null;                    
       } else {
           userParam = (User) Sessions.getCurrent().getAttribute("object");            
       }
        initialize();
    }

    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.user.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.user.view"));
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
        txtIdentificationNumber.setRawValue(null);
        txtFirstName.setRawValue(null);
        txtLastName.setRawValue(null);
        txtUserEmail.setRawValue(null);
        txtLogin.setRawValue(null);
        txtPassword.setRawValue(null);
        lblPosition.setValue(null);
        lblUserExtAlodiga.setValue(null);
        lblAuthorizeExtAlodiga.setValue(null);
    } 
    
        
    private void loadFields(User user) {
        List<PhonePerson> phonePersonUserList = null;
        PhonePerson phonePersonUser = null;
        List<PhonePerson> phonePersonEmployeeAuthorizeList = null;
        PhonePerson phonePersonEmployeeAuthorize = null;
        try {
            txtIdentificationNumber.setText(user.getIdentificationNumber());
            txtFirstName.setText(user.getFirstNames());
            txtLastName.setText(user.getLastNames());
            txtUserEmail.setText(user.getPersonId().getEmail());
            txtLogin.setText(user.getLogin());
            txtPassword.setText(user.getPassword());
            lblPosition.setValue(user.getEmployeeId().getEmployedPositionId().getName());
            lblUserExtAlodiga.setValue(user.getEmployeeId().getPersonId().getPhonePerson().getNumberPhone());
            lblAuthorizeExtAlodiga.setValue(user.getEmployeeId().getPersonId().getPhonePerson().getNumberPhone());
            if (user.getEmployeeId() != null) {
                EJBRequest request = new EJBRequest(); 
                HashMap params = new HashMap();
                params.put(Constants.PERSON_KEY, user.getEmployeeId().getPersonId().getId());
                request.setParams(params);
                phonePersonUserList = personEJB.getPhoneByPerson(request);
                for (PhonePerson phoneUser : phonePersonUserList) {
                    phonePersonUser = phoneUser;
                }
                lblUserExtAlodiga.setValue(phonePersonUser.getExtensionPhoneNumber());
            }
            if (user.getAuthorizedEmployeeId() != null) {
                EJBRequest request = new EJBRequest(); 
                HashMap params = new HashMap();
                params.put(Constants.PERSON_KEY, user.getAuthorizedEmployeeId().getPersonId().getId());
                request.setParams(params);
                phonePersonEmployeeAuthorizeList = personEJB.getPhoneByPerson(request);
                for (PhonePerson phoneEmployeeAuthorize : phonePersonEmployeeAuthorizeList) {
                    phonePersonEmployeeAuthorize = phoneEmployeeAuthorize;
                }
                lblAuthorizeExtAlodiga.setValue(phonePersonEmployeeAuthorize.getExtensionPhoneNumber());
            }
//            if (user.getEnabled() == true) {
//                rEnabledYes.setChecked(true);
//            } else {
//                rEnabledNo.setChecked(true);
//            }
        
        } catch (Exception ex) {
            showError(ex);
        }
    }     

    public void blockFields() {
        txtIdentificationNumber.setReadonly(true);
        txtFirstName.setReadonly(true);
        txtLastName.setReadonly(true);
        txtUserEmail.setReadonly(true);
        txtLogin.setReadonly(true);
        txtPassword.setReadonly(true);
        btnSave.setVisible(false);
    }
    
    public Boolean validateEmpty() {
        if (txtFirstName.getText().isEmpty()) {
            txtFirstName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtIdentificationNumber.getText().isEmpty()) {
            txtIdentificationNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtLastName.getText().isEmpty()) {
            txtLastName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtUserEmail.getText().isEmpty()) {
            txtUserEmail.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtLogin.getText().isEmpty()) {
            txtLogin.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtPassword.getText().isEmpty()) {
            txtPassword.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;
    }
    
    public void onChange$cmbCountry() {
        cmbDocumentsPersonType.setVisible(true);
        Country country = (Country) cmbCountry.getSelectedItem().getValue();
        loadCmbDocumentsPersonType(eventType, country.getId());
    }
    
    public void onChange$cmbEmployee() {
        lblPosition.setVisible(true);
        lblUserExtAlodiga.setVisible(true);
        Employee employee = (Employee) cmbEmployee.getSelectedItem().getValue();
        lblPosition.setValue(employee.getEmployedPositionId().getName());
        if (employee.getPersonId().getPhonePerson() != null) {
            lblUserExtAlodiga.setValue(employee.getPersonId().getPhonePerson().getExtensionPhoneNumber());
        }
    }

    public void onChange$cmbAuthorizeEmployee() {
        lblAuthorizeExtAlodiga.setVisible(true);
        Employee employeeAuthorize = (Employee) cmbAuthorizeEmployee.getSelectedItem().getValue();
        lblAuthorizeExtAlodiga.setValue(employeeAuthorize.getPersonId().getPhonePerson().getNumberPhone());
    }    
    
    
    private void saveUser(User _user) throws RegisterNotFoundException, NullParameterException, GeneralException {
        boolean indEnabled = true;
        try {
            User user = null;

            if (_user != null) {
                user = _user;
            } else {
                user = new User();
            }

            if (rEnabledYes.isChecked()) {
                indEnabled = true;
            } else {
                indEnabled = false;
            }

            //Obtener la clasificacion del Empleado / Usuario
            EJBRequest request1 = new EJBRequest();
            request1.setParam(Constants.CLASSIFICATION_PERSON_USER);
            PersonClassification personClassification = utilsEJB.loadPersonClassification(request1);

            //Guardar la persona
            Person person = new Person();
            person.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            person.setPersonTypeId(((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue()).getPersonTypeId());
            person.setEmail(txtUserEmail.getText());
            person.setCreateDate(new Timestamp(new Date().getTime()));
            person.setPersonClassificationId(personClassification);
            person = personEJB.savePerson(person);
            
            //Guarda el Usuario
            user.setLogin(txtLogin.getText());
            user.setPassword(txtPassword.getText());
            user.setPersonId(person);
            user.setDocumentsPersonTypeId((DocumentsPersonType) cmbDocumentsPersonType.getSelectedItem().getValue());
            user.setIdentificationNumber(txtIdentificationNumber.getText().toString());
            user.setCode(txtIdentificationNumber.getText().toString());
            user.setFirstNames(txtFirstName.getText());
            user.setLastNames(txtLastName.getText());
            user.setEmployeeId((Employee) cmbEmployee.getSelectedItem().getValue());            
            user.setComercialAgencyId((ComercialAgency) cmbComercialAgency.getSelectedItem().getValue());
            user.setAuthorizedEmployeeId((Employee) cmbAuthorizeEmployee.getSelectedItem().getValue());
//            user.setEnabled(indEnabled);
            user = personEJB.saveUser(user);
            userParam =user;
            this.showMessage("sp.common.save.success", false, null);
        } catch (WrongValueException ex) {
            showError(ex);
        }
    }  
    
    
    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveUser(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveUser(userParam);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void onclick$btnBack() {
        Executions.getCurrent().sendRedirect("listUser.zul");
    }
    
    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(userParam);
                txtIdentificationNumber.setReadonly(true);
                txtFirstName.setReadonly(true);
                txtLastName.setReadonly(true);
                txtUserEmail.setReadonly(true);
                txtLogin.setReadonly(true);
                txtPassword.setReadonly(true);
                loadCmbCountry(eventType);
                loadCmbEmployee(eventType);
                loadCmbComercialAgency(eventType);
                loadCmbAuthorizeEmployee(eventType);
                onChange$cmbCountry();
                onChange$cmbEmployee();
                onChange$cmbAuthorizeEmployee();
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(userParam);
                txtIdentificationNumber.setReadonly(true);
                txtFirstName.setReadonly(true);
                txtLastName.setReadonly(true);
                txtUserEmail.setReadonly(true);
                txtLogin.setReadonly(true);
                txtPassword.setReadonly(true);
                loadCmbCountry(eventType);
                loadCmbComercialAgency(eventType);
                loadCmbAuthorizeEmployee(eventType);
                blockFields();
                onChange$cmbCountry();
                onChange$cmbEmployee();
                onChange$cmbAuthorizeEmployee();
                rEnabledYes.setDisabled(true);
                rEnabledNo.setDisabled(true);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbEmployee(eventType);
                loadCmbComercialAgency(eventType);
                loadCmbAuthorizeEmployee(eventType);
                break;
            default:
                break;
        }
    
    }    
    
    private void loadCmbCountry(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Country> country;
        try {
            country = utilsEJB.getCountries(request1);
            loadGenericCombobox(country, cmbCountry, "name", evenInteger, Long.valueOf(userParam != null ? userParam.getPersonId().getCountryId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private void loadCmbDocumentsPersonType(Integer evenInteger, Integer countryId) {
        EJBRequest request1 = new EJBRequest();
        cmbDocumentsPersonType.getItems().clear();
        Map params = new HashMap();
        params.put(QueryConstants.PARAM_COUNTRY_ID, countryId);
        params.put(QueryConstants.PARAM_IND_NATURAL_PERSON, WebConstants.IND_NATURAL_PERSON);
        request1.setParams(params);
        List<DocumentsPersonType> documentsPersonType;
        try {
            documentsPersonType = utilsEJB.getDocumentsPersonByCountry(request1);
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger, Long.valueOf(userParam != null ? userParam.getDocumentsPersonTypeId().getId(): 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }

    }
    
    private void loadCmbEmployee(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Employee> employeeList;
        String nameEmployee = "";
        try {
            employeeList = personEJB.getEmployee(request1);
            for (int i = 0; i < employeeList.size(); i++) {
                Comboitem item = new Comboitem();
                item.setValue(employeeList.get(i));
                nameEmployee = employeeList.get(i).getFirstNames()+" "+employeeList.get(i).getLastNames();
                item.setLabel(nameEmployee);
                item.setParent(cmbEmployee);
                if (eventType != 1) {
                    if (employeeList.get(i).getId().equals(userParam.getEmployeeId().getId())) {
                        cmbEmployee.setSelectedItem(item);
                    }
                }
            }
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }
    
    private void loadCmbComercialAgency(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<ComercialAgency> comercialAgency;
        try {
            comercialAgency = personEJB.getComercialAgency(request1);
            loadGenericCombobox(comercialAgency, cmbComercialAgency, "name", evenInteger, Long.valueOf(userParam != null ? userParam.getComercialAgencyId().getId() : 0));
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }
    
    private void loadCmbAuthorizeEmployee(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<Employee> employeeList;
        String nameEmployee = "";
        try {
            employeeList = personEJB.getEmployee(request1);
            for (int i = 0; i < employeeList.size(); i++) {
                Comboitem item = new Comboitem();
                item.setValue(employeeList.get(i));
                nameEmployee = employeeList.get(i).getFirstNames()+" "+employeeList.get(i).getLastNames();
                item.setLabel(nameEmployee);
                item.setParent(cmbAuthorizeEmployee);
                if (eventType != 1) {
                    if (employeeList.get(i).getId().equals(userParam.getAuthorizedEmployeeId().getId())) {
                        cmbAuthorizeEmployee.setSelectedItem(item);
                    }
                }
            }
        } catch (EmptyListException ex) {
            showError(ex);
        } catch (GeneralException ex) {
            showError(ex);
        } catch (NullParameterException ex) {
            showError(ex);
        }
    }

    private Object getSelectedItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
  }
