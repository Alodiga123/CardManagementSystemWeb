package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.PersonEJB;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.ApplicantNaturalPerson;
import com.cms.commons.models.FamilyReferences;
import com.cms.commons.util.Constants;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class AdminFamilyReferencesController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;

    private Textbox txtFullName;
    private Textbox txtFullLastName;
    private Textbox txtCity;
    private Textbox txtCellPhone;
    private Textbox txtLocalPhone;
    private PersonEJB personEJB = null;
    private FamilyReferences familyReferencesParam;
    private Button btnSave;
    private Integer eventType;
    public Window winAdminFamilyReferences;
    public String indGender = null;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        familyReferencesParam = (Sessions.getCurrent().getAttribute("object") != null) ? (FamilyReferences) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
        //initView(eventType, "sp.crud.country");
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            personEJB = (PersonEJB) EJBServiceLocator.getInstance().get(EjbConstants.PERSON_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtFullName.setRawValue(null);
        txtCity.setRawValue(null);
        txtLocalPhone.setRawValue(null);
        txtCellPhone.setRawValue(null);
        txtFullLastName.setRawValue(null);
    }

    private void loadFields(FamilyReferences familyReferences) {
        try {
            txtFullName.setText(familyReferences.getFirstNames());
            txtCity.setText(familyReferences.getCity());
            txtLocalPhone.setText(familyReferences.getLocalPhone());
            txtCellPhone.setText(familyReferences.getCellPhone());
            txtFullLastName.setText(familyReferences.getLastNames());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtFullName.setReadonly(true);
        txtCity.setReadonly(true);
        txtLocalPhone.setReadonly(true);
        txtCellPhone.setReadonly(true);
        txtFullLastName.setReadonly(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtFullName.getText().isEmpty()) {
            txtFullName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtFullLastName.getText().isEmpty()) {
            txtFullLastName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;

    }

    private void saveFamilyReferences(FamilyReferences _familyReferences) {
        ApplicantNaturalPerson applicantNaturalPerson = null;
        try {
            FamilyReferences familyReferences = null;
            
            if (_familyReferences != null) {
                familyReferences = _familyReferences;
            } else {//New LegalPerson
                familyReferences = new FamilyReferences();
            }
            
            //Solicitante
            AdminNaturalPersonController adminNaturalPerson = new AdminNaturalPersonController();
            if (adminNaturalPerson.getApplicantNaturalPerson() != null) {
                applicantNaturalPerson = adminNaturalPerson.getApplicantNaturalPerson();
            }
            
            //Guarda la referencia familiar asociada al solicitante
            familyReferences.setFirstNames(txtFullName.getText());
            familyReferences.setApplicantNaturalPersonId(applicantNaturalPerson);
            familyReferences.setCity(txtCity.getText());
            familyReferences.setLocalPhone(txtLocalPhone.getText());
            familyReferences.setCellPhone(txtCellPhone.getText());
            familyReferences.setLastNames(txtFullLastName.getText());
            familyReferences = personEJB.saveFamilyReferences(familyReferences);
            familyReferencesParam = familyReferences;
            this.showMessage("sp.common.save.success", false, null);
            EventQueues.lookup("updateFamilyReferences", EventQueues.APPLICATION, true).publish(new Event(""));
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveFamilyReferences(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveFamilyReferences(familyReferencesParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void onClick$btnBack() {
        winAdminFamilyReferences.detach();
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(familyReferencesParam);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(familyReferencesParam);
                blockFields();
                break;
            case WebConstants.EVENT_ADD:
                break;
            default:
                break;
        }
    }
}
