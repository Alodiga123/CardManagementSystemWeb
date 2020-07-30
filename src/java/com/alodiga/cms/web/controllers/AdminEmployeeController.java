package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.ejb.UserEJB;
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.models.ComercialAgency;
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.EmployedPosition;
import com.cms.commons.models.Employee;
import com.cms.commons.models.Person;
import com.cms.commons.models.PersonClassification;
import com.cms.commons.models.PersonType;
import com.cms.commons.models.PhonePerson;
import com.cms.commons.models.User;
import com.cms.commons.util.Constants;
import com.cms.commons.util.QueryConstants;
import java.sql.Timestamp;
import java.util.ArrayList;
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

public class AdminEmployeeController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Combobox cmbCountry;
    private Combobox cmbPersonType;
    private Intbox indIdentification;
    private Textbox txtName;
    private Textbox txtLastName;
    private Textbox txtEmail;
    private Combobox cmbPositionEnterprise;
    private Combobox cmbComercialAgency;
    private PersonEJB personEJB = null;
    private UtilsEJB utilsEJB = null;
    private UserEJB userEJB = null;
    private Employee employeeParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;
    private List<PhonePerson> phonePersonUserList = null;
    List<Employee> employeeList = new ArrayList<Employee>();

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        employeeParam = (Sessions.getCurrent().getAttribute("object") != null) ? (Employee) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
            employeeParam = null;
        } else {
            employeeParam = (Employee) Sessions.getCurrent().getAttribute("object");
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
            userEJB = (UserEJB) EJBServiceLocator.getInstance().get(EjbConstants.USER_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {

    }

    private void loadFields(Employee employee) {
        
        try {
            indIdentification.setValue(employee.getIdentificationNumber());
            txtName.setText(employee.getFirstNames());
            txtLastName.setText(employee.getLastNames());
            txtEmail.setText(employee.getPersonId().getEmail());
            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
 
    }

    public void blockFields() {
       indIdentification.setRawValue(null); 
       txtName.setRawValue(null);
       txtLastName.setRawValue(null);
       txtEmail.setRawValue(null);
       btnSave.setVisible(false);
       cmbCountry.setDisabled(true);
       cmbPersonType.setDisabled(true);
       cmbPositionEnterprise.setDisabled(true);
       cmbComercialAgency.setDisabled(true);
    }

    public Boolean validateEmpty() {
        return true;
    }
   
    private void saveEmployee(Employee _employee) throws RegisterNotFoundException, NullParameterException, GeneralException {
       
        try {
            Employee employee = null;

            if (_employee != null) {
                employee = _employee;
            } else {
                employee = new Employee();
            }
            
            //Obtener la clasificacion del Empleado
            EJBRequest request1 = new EJBRequest();
            request1.setParam(Constants.CLASSIFICATION_PERSON_EMPLOYEE);
            PersonClassification personClassification = utilsEJB.loadPersonClassification(request1);
            
            //Guardar la persona
            Person person = new Person();
            person.setCountryId((Country) cmbCountry.getSelectedItem().getValue());
            person.setPersonTypeId((PersonType) cmbPersonType.getSelectedItem().getValue());
            person.setEmail(txtEmail.getText());
            person.setPersonClassificationId(personClassification);
            person.setCreateDate(new Timestamp(new Date().getTime()));
            person = personEJB.savePerson(person);
            
            //Guardar el empleado
            employee.setPersonId(person);
            employee.setIdentificationNumber(indIdentification.getValue());
            employee.setFirstNames(txtName.getText());
            employee.setLastNames(txtLastName.getText());
            employee.setComercialAgencyId((ComercialAgency) cmbComercialAgency.getSelectedItem().getValue());
            employee.setEmployedPositionId((EmployedPosition) cmbPositionEnterprise.getSelectedItem().getValue()) ;
            employee = personEJB.saveEmployee(employee);
            employeeParam = employee;
            this.showMessage("sp.common.save.success", false, null);
            
        } catch (WrongValueException ex) {
            showError(ex);
        }
    }
 

    public void onClick$btnSave() throws RegisterNotFoundException, NullParameterException, GeneralException {
        
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                        saveEmployee(null);
                break;
                case WebConstants.EVENT_EDIT:
                    saveEmployee(employeeParam);
                break;
                default:
                break;
            }
        
    }

    public void onclick$btnBack() {
        Executions.getCurrent().sendRedirect("listEmployee.zul");
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(employeeParam);
                loadCmbContryId(eventType);
                loadCmbPersonType(eventType);
                loadCmbComercialAgency(eventType);
                loadCmbPositionEnterprise(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(employeeParam);
                loadCmbContryId(eventType);
                loadCmbPersonType(eventType);
                loadCmbComercialAgency(eventType);
                loadCmbPositionEnterprise(eventType);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                loadCmbContryId(eventType);
                loadCmbPersonType(eventType);
                loadCmbComercialAgency(eventType);
                loadCmbPositionEnterprise(eventType);
                
                break;
            default:
                break;
        }
    }
    
    private void loadCmbContryId(Integer evenInteger) {
        //cmbCountry
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;
        try {
            countries = utilsEJB.getCountries(request1);
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(employeeParam != null ? employeeParam.getPersonId().getCountryId().getId() : 0));
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
            loadGenericCombobox(personTypes, cmbPersonType, "description", evenInteger, Long.valueOf(employeeParam != null ? employeeParam.getDocumentsPersonTypeId().getId() : 0));
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
    
    private void loadCmbComercialAgency(Integer evenInteger) {
        //Cargos de Nomina analista 1, analista 2, atencion al cliente 1 ,atencion al cliente 2, especialista seguridad 1 y 2
        //cmbComercialAgency
        
        EJBRequest request1 = new EJBRequest();
        List<ComercialAgency> comcercialAgency;
        try {
            comcercialAgency = userEJB.getComercialAgency(request1);
            loadGenericCombobox(comcercialAgency, cmbComercialAgency, "name", evenInteger, Long.valueOf(employeeParam != null ? employeeParam.getComercialAgencyId().getId() : 0));
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
    
    private void loadCmbPositionEnterprise(Integer evenInteger) {
        //Cargos de Nomina analista 1, analista 2, atencion al cliente 1 ,atencion al cliente 2, especialista seguridad 1 y 2
        //cmbPositionEnterprise
        //position en personejb
        EJBRequest request1 = new EJBRequest();
        List<EmployedPosition> employedPosition;
        try {
            employedPosition = personEJB.getEmployedPosition(request1);
            loadGenericCombobox(employedPosition, cmbPositionEnterprise, "name", evenInteger, Long.valueOf(employeeParam != null ? employeeParam.getEmployedPositionId().getId() : 0));
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
    
    private Object getSelectedItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
