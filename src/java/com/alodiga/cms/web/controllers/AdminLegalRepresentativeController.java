package com.alodiga.cms.web.controllers;

import com.alodiga.cms.commons.ejb.UtilsEJB;
import com.alodiga.cms.commons.exception.EmptyListException;
import com.alodiga.cms.commons.exception.GeneralException;
import com.alodiga.cms.commons.exception.NullParameterException;
import com.alodiga.cms.web.generic.controllers.GenericAbstractAdminController;
import com.alodiga.cms.web.utils.WebConstants;
import com.cms.commons.genericEJB.EJBRequest;
import com.cms.commons.models.Country;
import com.cms.commons.models.DocumentsPersonType;
import com.cms.commons.models.EconomicActivity;
import com.cms.commons.models.LegalPerson;
import com.cms.commons.models.LegalRepresentatives;
import com.cms.commons.models.PhonePerson;
import com.cms.commons.util.EJBServiceLocator;
import com.cms.commons.util.EjbConstants;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.RadioButton;
import org.apache.http.impl.conn.Wire;
import org.codehaus.groovy.tools.shell.Command;
import org.jboss.weld.metadata.Selectors;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;

public class AdminLegalRepresentativeController extends GenericAbstractAdminController {

    private static final long serialVersionUID = -9145887024839938515L;
    private Textbox txtIdentificationNumber;
    private Textbox txtFullName;
    private Textbox txtFullLastName;
    private Textbox txtBirthPlace;
    private Textbox txtAge;
    private Textbox txtPhoneNumber;
    private Combobox cmbCountry;
    private Combobox cmbDocumentsPersonType;
    private Combobox cmbCivilState;
    private RadioButton gender;
    private Datebox txtDueDateIdentification;
    private Datebox txtBirthDay;
    
    private UtilsEJB utilsEJB = null;
    private LegalRepresentatives legalRepresentativesParam;
    private Button btnSave;
    private Integer eventType;
    
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        legalRepresentativesParam = (Sessions.getCurrent().getAttribute("object") != null) ? (LegalRepresentatives) Sessions.getCurrent().getAttribute("object") : null;
        eventType = (Integer) Sessions.getCurrent().getAttribute(WebConstants.EVENTYPE);
        initialize();
        //initView(eventType, "sp.crud.country");
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            utilsEJB = (UtilsEJB) EJBServiceLocator.getInstance().get(EjbConstants.UTILS_EJB);
            loadData();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void clearFields() {
        txtIdentificationNumber.setRawValue(null);
        txtFullName.setRawValue(null);
        txtFullLastName.setRawValue(null);
        txtBirthPlace.setRawValue(null);
        txtAge.setRawValue(null);
        txtPhoneNumber.setRawValue(null);
    }

    private void loadFields(LegalRepresentatives legalRepresentatives) {
        try {
            txtIdentificationNumber.setText(legalRepresentatives.getIdentificationNumber());
            txtFullName.setText(legalRepresentatives.getFirstNames());
            txtFullLastName.setText(legalRepresentatives.getLastNames());
            txtBirthPlace.setText(legalRepresentatives.getPlaceBirth());
            txtAge.setText(legalRepresentatives.getAge().toString());
            txtPhoneNumber.setText(legalRepresentatives.getPersonsId().getPhonePerson().getNumberPhone());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void blockFields() {
        txtIdentificationNumber.setReadonly(true);
        txtFullName.setReadonly(true);
        txtFullLastName.setReadonly(true);
        txtBirthPlace.setReadonly(true);
        txtAge.setReadonly(true);
        txtPhoneNumber.setReadonly(true);
        cmbCountry.setDisabled(true);
        cmbDocumentsPersonType.setDisabled(true);
        btnSave.setVisible(false);
    }

    public Boolean validateEmpty() {
        if (txtIdentificationNumber.getText().isEmpty()) {
            txtIdentificationNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtFullName.getText().isEmpty()) {
            txtFullName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtFullLastName.getText().isEmpty()) {
            txtFullLastName.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtPhoneNumber.getText().isEmpty()) {
            txtPhoneNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtBirthPlace.getText().isEmpty()) {
            txtBirthPlace.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else if (txtPhoneNumber.getText().isEmpty()) {
            txtPhoneNumber.setFocus(true);
            this.showMessage("sp.error.field.cannotNull", true, null);
        } else {
            return true;
        }
        return false;

    }

    public void onClick$btnCodes() {
        Executions.getCurrent().sendRedirect("/docs/T-SP-E.164D-2009-PDF-S.pdf", "_blank");
    }

    private void saveLegalRepresentatives(LegalRepresentatives _legalRepresentatives) {
        try {
            LegalRepresentatives legalRepresentatives = null;

            if (_legalRepresentatives != null) {
                legalRepresentatives = _legalRepresentatives;
            } else {//New LegalPerson
                legalRepresentatives = new LegalRepresentatives();
            }
            
            legalRepresentatives.setIdentificationNumber(txtIdentificationNumber.getText());
            legalRepresentatives.setFirstNames(txtFullName.getText());
            legalRepresentatives.setLastNames(txtFullLastName.getText());
            legalRepresentatives.setPlaceBirth(txtBirthPlace.getText());
            legalRepresentatives.setAge(txtAge.getText().length());
            //legalRepresentatives.setPersonsId((PhonePerson)txtPhoneNumber);
            
            if (txtDueDateIdentification.getValue() != null) {
                legalRepresentatives.setDueDateDocumentIdentification(new Timestamp(txtDueDateIdentification.getValue().getTime()));
            } else {
                legalRepresentatives.setDueDateDocumentIdentification(new Timestamp(new Date().getTime()));
            }
            if (txtBirthDay.getValue() != null) {
                legalRepresentatives.setDueDateDocumentIdentification(new Timestamp(txtBirthDay.getValue().getTime()));
            } else {
                legalRepresentatives.setDueDateDocumentIdentification(new Timestamp(new Date().getTime()));
            }

            legalRepresentatives = utilsEJB.saveLegalRepresentatives(legalRepresentatives);
            legalRepresentativesParam = legalRepresentatives;
            this.showMessage("sp.common.save.success", false, null);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    public void onClick$btnSave() {
        if (validateEmpty()) {
            switch (eventType) {
                case WebConstants.EVENT_ADD:
                    saveLegalRepresentatives(null);
                    break;
                case WebConstants.EVENT_EDIT:
                    saveLegalRepresentatives(legalRepresentativesParam);
                    break;
                default:
                    break;
            }
        }
    }

    public void loadData() {
        switch (eventType) {
            case WebConstants.EVENT_EDIT:
                loadFields(legalRepresentativesParam);
                loadCmbCountry(eventType);
                loadCmbDocumentsPersonType(eventType);
                break;
            case WebConstants.EVENT_VIEW:
                loadFields(legalRepresentativesParam);
                txtIdentificationNumber.setDisabled(true);
                txtFullName.setDisabled(true);
                txtFullLastName.setDisabled(true);
                txtBirthPlace.setDisabled(true);
                txtAge.setDisabled(true);
                txtPhoneNumber.setDisabled(true);
                txtDueDateIdentification.setDisabled(true);
                txtBirthDay.setDisabled(true);
                loadCmbCountry(eventType);
                loadCmbDocumentsPersonType(eventType);
                break;
            case WebConstants.EVENT_ADD:
                loadCmbCountry(eventType);
                loadCmbDocumentsPersonType(eventType);
                break;
            default:
                break;
        }
    }

    private void loadCmbCountry(Integer evenInteger) {
        //cmbCountry
        EJBRequest request1 = new EJBRequest();
        List<Country> countries;

        try {
            countries = utilsEJB.getCountries(request1);
            loadGenericCombobox(countries, cmbCountry, "name", evenInteger, Long.valueOf(legalRepresentativesParam != null ? legalRepresentativesParam.getPersonsId().getCountryId().getId() : 0));
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

    private void loadCmbDocumentsPersonType(Integer evenInteger) {
        //cmbDocumentsPersonType
        EJBRequest request1 = new EJBRequest();
        List<DocumentsPersonType> documentsPersonType;

        try {
            documentsPersonType = utilsEJB.getDocumentsPersonTypes(request1);
            loadGenericCombobox(documentsPersonType, cmbDocumentsPersonType, "description", evenInteger, Long.valueOf(legalRepresentativesParam != null ? legalRepresentativesParam.getDocumentsPersonTypeId().getId() : 0));
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
