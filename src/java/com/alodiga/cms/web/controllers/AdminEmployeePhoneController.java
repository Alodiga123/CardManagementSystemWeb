package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.PhoneType;
import com.cms.commons.models.PhonePerson;
import com.cms.commons.models.Person;
import com.cms.commons.models.Employee;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.util.List;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

public class AdminEmployeePhoneController extends GenericAbstractAdminController {

            
    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtPhone;
    private PersonEJB personEJB = null;
    private Combobox cmbPhoneType;
    private PhonePerson phonePersonParam;
    private Button btnSave;
    private Integer eventType;
    private Toolbarbutton tbbTitle;
    public Window winAdminPhoneEmployee;
    

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);        
        eventType = (Integer) Sessions.getCurrent().getAttribute( WebConstants.EVENTYPE);
        if (eventType == WebConstants.EVENT_ADD) {
           phonePersonParam = null;                    
       } else {
           phonePersonParam = (PhonePerson) Sessions.getCurrent().getAttribute("object");            
       }
        initialize();
    }
    
    @Override
    public void initialize() {
        super.initialize();
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.phoneEmployee.edit"));
                break;
            case WebConstants.EVENT_VIEW:
                tbbTitle.setLabel(Labels.getLabel("cms.crud.phoneEmployee.view"));
                break;
            default:
                break;
        }        
        try {
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }   

    public void clearFields() {
        txtPhone.setRawValue(null);;

    }

    private void loadFields(PhonePerson phonePerson) {
        try {
            txtPhone.setText(phonePerson.getNumberPhone());
            btnSave.setVisible(true);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtPhone.setReadonly(true);
        btnSave.setVisible(false);
    }
    
    public void onClick$btnBack() {
        winAdminPhoneEmployee.detach();
    }

    public Boolean validateEmpty() {
        if (txtPhone.getText().isEmpty()) {
            txtPhone.setFocus(true);
            this.showMessage("cms.error.field.phoneNumber", true, null);
        
        } else if (cmbPhoneType.getSelectedItem() == null) {
            cmbPhoneType.setFocus(true);
            this.showMessage("cms.error.phoneType.notSelected", true, null);
        }else {
            return true;
        }
        return false;

    }


    private void savePhone(PhonePerson _phonePerson) {
        Employee employee = null;       
        try {
            PhonePerson phonePerson = null;

            if (_phonePerson != null) {

           phonePerson = _phonePerson;
            } else {//New country
                phonePerson = new PhonePerson();
            }
            
            //Obtener Person
             AdminEmployeeController adminEmployee = new AdminEmployeeController();
            if (adminEmployee.getEmployeeParent().getPersonId().getId() != null) {
                employee = adminEmployee.getEmployeeParent();
            }
            
            //Guardar telefono
            phonePerson.setPersonId(employee.getPersonId());
            phonePerson.setNumberPhone(txtPhone.getText());
            phonePerson.setPhoneTypeId((PhoneType) cmbPhoneType.getSelectedItem().getValue());
            phonePerson = personEJB.savePhonePerson(phonePerson);
            this.showMessage("sp.common.save.success", false, null);
            btnSave.setVisible(false);
            
            } catch (Exception ex) {
                 showError(ex);
            }

    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    blockFields();
                    savePhone(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    savePhone(phonePersonParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(phonePersonParam);
                loadcmbPhoneType(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(phonePersonParam);
                txtPhone.setReadonly(true);
                blockFields();
                loadcmbPhoneType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadcmbPhoneType(eventType);
                break;
            default:
                break;
        }
    }
    
    private void loadcmbPhoneType(Integer evenInteger) {
        EJBRequest request1 = new EJBRequest();
        List<PhoneType> phoneTypes;
        try {
            phoneTypes = personEJB.getPhoneType(request1);
            loadGenericCombobox(phoneTypes,cmbPhoneType, "description",evenInteger,Long.valueOf(phonePersonParam != null? phonePersonParam.getPhoneTypeId().getId() : 0) );
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

    
 

